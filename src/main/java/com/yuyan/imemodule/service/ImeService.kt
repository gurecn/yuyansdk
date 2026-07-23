package com.yuyan.imemodule.service

import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.os.SystemClock
import android.text.InputType
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.CursorAnchorInfo
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import com.yuyan.imemodule.callback.IKeyboardView
import com.yuyan.imemodule.candidate.CandidateView
import com.yuyan.imemodule.data.emojicon.YuyanEmojiCompat
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager.OnThemeChangeListener
import com.yuyan.imemodule.data.theme.ThemeManager.addOnChangedListener
import com.yuyan.imemodule.data.theme.ThemeManager.onSystemDarkModeChange
import com.yuyan.imemodule.data.theme.ThemeManager.removeOnChangedListener
import com.yuyan.imemodule.keyboard.InputView
import com.yuyan.imemodule.keyboard.KeyboardManager
import com.yuyan.imemodule.keyboard.container.ClipBoardContainer
import com.yuyan.imemodule.manager.InputModeSwitcher
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance
import com.yuyan.imemodule.prefs.behavior.SkbMenuMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.utils.KeyboardLoaderUtil
import com.yuyan.imemodule.utils.StringUtils
import com.yuyan.imemodule.utils.isDarkMode
import com.yuyan.imemodule.view.preference.ManagedPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import splitties.bitflags.hasFlag

/**
 * Main class of the Pinyin input method. 输入法服务
 */
class ImeService : InputMethodService() {
    private var isHardwareKeyboard = false
    private var showVirtualKeyboardOnPhysicalKeyboard = false
    private lateinit var mInputView: IKeyboardView
    private val onThemeChangeListener = OnThemeChangeListener { _: Theme? -> mInputView.updateTheme()}
    private val clipboardUpdateContentListener = ManagedPreference.OnChangeListener<String> { _, value ->
        if(getInstance().clipboard.clipboardSuggestion.getValue()){
            if(value.isNotBlank()) {
                if(KeyboardManager.instance.currentContainer is ClipBoardContainer
                    && (KeyboardManager.instance.currentContainer as ClipBoardContainer).getMenuMode() == SkbMenuMode.ClipBoard ){
                    (KeyboardManager.instance.currentContainer as ClipBoardContainer).showClipBoardView(SkbMenuMode.ClipBoard)
                } else {
                    mInputView.showSymbols(arrayOf(value))
                }
            }
        }
    }
    private val showVirtualKeyboardOnPhysicalKeyboardListener = ManagedPreference.OnChangeListener<Boolean> { _, value ->
        showVirtualKeyboardOnPhysicalKeyboard = value
        handleHardwareKeyboard()
        updateInputViewShown()
    }
    override fun onCreate() {
        super.onCreate()
        addOnChangedListener(onThemeChangeListener)
        getInstance().keyboardSetting.showVirtualKeyboardOnPhysicalKeyboard.registerOnChangeListener(showVirtualKeyboardOnPhysicalKeyboardListener)
        getInstance().internal.clipboardUpdateContent.registerOnChangeListener(clipboardUpdateContentListener)
        handleHardwareKeyboard()
    }

    override fun onCreateInputView(): View {
        val inputView = InputView(baseContext, this)
        KeyboardManager.instance.switchKeyboard()
        if(!isHardwareKeyboard)mInputView = inputView
        return inputView
    }

    override fun onCreateCandidatesView(): View {
        val candidateView = CandidateView(baseContext, this)
        if(isHardwareKeyboard) mInputView = candidateView
        return candidateView
    }

    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        return if(showVirtualKeyboardOnPhysicalKeyboard) true else !isHardwareKeyboard
    }

    override fun onStartInput(editorInfo: EditorInfo?, restarting: Boolean) {
        super.onStartInput(editorInfo, restarting)
        if(editorInfo != null) {
            YuyanEmojiCompat.setEditorInfo(editorInfo)
            InputModeSwitcher.requestInputWithSkb(editorInfo)
        }
    }

    override fun onStartInputView(editorInfo: EditorInfo, restarting: Boolean) {
        mInputView.onStartInputView(editorInfo, restarting)
        super.onStartInputView(editorInfo, restarting)
    }

    override fun onDestroy() {
        super.onDestroy()
        getInstance().keyboardSetting.showVirtualKeyboardOnPhysicalKeyboard.unregisterOnChangeListener(showVirtualKeyboardOnPhysicalKeyboardListener)
        removeOnChangedListener(onThemeChangeListener)
        getInstance().internal.clipboardUpdateContent.unregisterOnChangeListener(clipboardUpdateContentListener)
    }

    /**
     * 横竖屏切换
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        CoroutineScope(Dispatchers.Main).launch {
            delay(200) //延时，解决获取屏幕尺寸不准确。
            handleHardwareKeyboard()
            EnvironmentSingleton.instance.initData(baseContext)
                KeyboardLoaderUtil.instance.clearKeyboardMap()
                KeyboardManager.instance.clearKeyboard()
                KeyboardManager.instance.switchKeyboard()
                mInputView.setConfiguration(newConfig)
        }
        onSystemDarkModeChange(newConfig.isDarkMode())
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (0 != event.repeatCount || event.isShiftPressed || event.isMetaPressed) super.onKeyDown(keyCode, event)
        else if(event.isCtrlPressed && keyCode != KeyEvent.KEYCODE_SPACE)super.onKeyDown(keyCode, event)
        else if (::mInputView.isInitialized) mInputView.processKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
        else super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return if (0 != event.repeatCount || event.isShiftPressed || event.isMetaPressed) super.onKeyUp(keyCode, event)
        else if(event.isCtrlPressed && keyCode != KeyEvent.KEYCODE_SPACE)super.onKeyUp(keyCode, event)
        else if (::mInputView.isInitialized) mInputView.processKeyUp(event) || super.onKeyUp(keyCode, event)
        else super.onKeyUp(keyCode, event)
    }

    override fun setInputView(view: View) {
        super.setInputView(view)
        val layoutParams = view.layoutParams
        if (layoutParams != null && layoutParams.height != ViewGroup.LayoutParams.MATCH_PARENT) {
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            view.setLayoutParams(layoutParams)
        }
    }

    override fun onEvaluateFullscreenMode(): Boolean = false //修复横屏之后输入框遮挡问题


    override fun onComputeInsets(outInsets: Insets) {
        val (x, y, width, height) = if (::mInputView.isInitialized) mInputView.getKeyboardRect() else intArrayOf(0, 0, 0,0)
        outInsets.apply {
            if(EnvironmentSingleton.instance.keyboardModeFloat) {
                contentTopInsets = EnvironmentSingleton.instance.mScreenHeight
                visibleTopInsets = EnvironmentSingleton.instance.mScreenHeight
                touchableInsets = Insets.TOUCHABLE_INSETS_REGION
                touchableRegion.set(x, y, x + width, y + height)
            } else {
                contentTopInsets = y
                touchableInsets = Insets.TOUCHABLE_INSETS_CONTENT
                touchableRegion.setEmpty()
                visibleTopInsets = y
            }
        }
    }

    override fun onUpdateSelection(oldSelStart: Int, oldSelEnd: Int, newSelStart: Int, newSelEnd: Int, candidatesStart: Int, candidatesEnd: Int) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd)
        mInputView.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesEnd)
    }

    private val cursorAnchorPosition = FloatArray(2)
    override fun onUpdateCursorAnchorInfo(cursorAnchorInfo: CursorAnchorInfo?) {
        super.onUpdateCursorAnchorInfo(cursorAnchorInfo)
        if (cursorAnchorInfo == null) return
        cursorAnchorPosition[0] = cursorAnchorInfo.insertionMarkerHorizontal
        cursorAnchorPosition[1] = cursorAnchorInfo.insertionMarkerBottom
        val matrix = cursorAnchorInfo.getMatrix()
        if (matrix != null) {
            matrix.mapPoints(cursorAnchorPosition)
        }
        mInputView.updatePosition(cursorAnchorPosition)
    }

    override fun onWindowShown() {
        mInputView.onWindowShown()
        super.onWindowShown()
    }

    override fun onWindowHidden() {
        mInputView.onWindowHidden()
        super.onWindowHidden()
    }

    /**
     * 模拟Enter按键点击
     */
    fun sendEnterKeyEvent() {
        val inputConnection = getCurrentInputConnection()
        YuyanEmojiCompat.mEditorInfo?.run {
            if (inputType and InputType.TYPE_MASK_CLASS == InputType.TYPE_NULL || imeOptions.hasFlag(EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
                sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER)
            } else if (!actionLabel.isNullOrEmpty() && actionId != EditorInfo.IME_ACTION_UNSPECIFIED) {
                inputConnection.performEditorAction(actionId)
            } else when (val action = imeOptions and EditorInfo.IME_MASK_ACTION) {
                EditorInfo.IME_ACTION_UNSPECIFIED, EditorInfo.IME_ACTION_NONE -> sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER)
                else -> inputConnection.performEditorAction(action)
            }
        }
    }

    fun sendCombinationKeyEvents(keyEventCode: Int, alt: Boolean = false, ctrl: Boolean = false, shift: Boolean = false) {
        var metaState = 0
        if (alt) metaState = KeyEvent.META_ALT_ON or KeyEvent.META_ALT_LEFT_ON
        if (ctrl) metaState = metaState or KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON
        if (shift) metaState = metaState or KeyEvent.META_SHIFT_ON or KeyEvent.META_SHIFT_LEFT_ON
        val eventTime = SystemClock.uptimeMillis()
        if (alt) sendDownKeyEvent(eventTime, KeyEvent.KEYCODE_ALT_LEFT)
        if (ctrl) sendDownKeyEvent(eventTime, KeyEvent.KEYCODE_CTRL_LEFT)
        if (shift) sendDownKeyEvent(eventTime, KeyEvent.KEYCODE_SHIFT_LEFT)
        sendDownKeyEvent(eventTime, keyEventCode, metaState)
        sendUpKeyEvent(eventTime, keyEventCode, metaState)
        if (shift) sendUpKeyEvent(eventTime, KeyEvent.KEYCODE_SHIFT_LEFT)
        if (ctrl) sendUpKeyEvent(eventTime, KeyEvent.KEYCODE_CTRL_LEFT)
        if (alt) sendUpKeyEvent(eventTime, KeyEvent.KEYCODE_ALT_LEFT)
    }

    fun sendDownKeyEvent(eventTime: Long, keyEventCode: Int, metaState: Int = 0) {
        currentInputConnection?.sendKeyEvent(
            KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyEventCode, 0, metaState,
                KeyCharacterMap.VIRTUAL_KEYBOARD, keyEventCode, KeyEvent.FLAG_SOFT_KEYBOARD or KeyEvent.FLAG_KEEP_TOUCH_MODE)
        )
    }

    fun sendUpKeyEvent(eventTime: Long, keyEventCode: Int, metaState: Int = 0) {
        currentInputConnection.sendKeyEvent(
            KeyEvent(eventTime, SystemClock.uptimeMillis(), KeyEvent.ACTION_UP, keyEventCode, 0, metaState,
                KeyCharacterMap.VIRTUAL_KEYBOARD, keyEventCode, KeyEvent.FLAG_SOFT_KEYBOARD or KeyEvent.FLAG_KEEP_TOUCH_MODE)
        )
    }

    /**
     * 向输入框提交预选词
     */
    fun setComposingText(text: CharSequence) {
        currentInputConnection.setComposingText(text, 1)
    }


    /**
     * 结束提交预选词
     */
    fun finishComposingText() {
        currentInputConnection.finishComposingText()
    }

    /**
     * 发送字符串给编辑框
     */
    fun commitText(text: String) {
        currentInputConnection.commitText(StringUtils.converted2FlowerTypeface(text), 1)
    }

    /**
     * 发送字符串给编辑框
     */
    fun commitText(text: String, newCursorPosition: Int) {
        currentInputConnection.commitText(StringUtils.converted2FlowerTypeface(text), newCursorPosition)
    }

    fun getTextBeforeCursor(length:Int) : String {
        return currentInputConnection.getTextBeforeCursor(length, 0).toString()
    }

    fun commitTextEditMenu(id:Int) {
        currentInputConnection.performContextMenuAction(id)
    }

    fun performEditorAction(editorAction:Int) {
        currentInputConnection.performEditorAction(editorAction)
    }

    fun deleteSurroundingText(length:Int) {
        currentInputConnection.deleteSurroundingText(length, 0)
    }

    fun setSelection(start: Int, end: Int) {
        currentInputConnection.setSelection(start, end)
    }

    fun handleHardwareKeyboard(newConfig: Configuration? = null): Boolean {
        val hardwareKeyboard = if (showVirtualKeyboardOnPhysicalKeyboard) false
            else if (newConfig != null) (newConfig.keyboard != Configuration.KEYBOARD_NOKEYS)
            else resources.configuration.keyboard != Configuration.KEYBOARD_NOKEYS
        isHardwareKeyboard = hardwareKeyboard
        return hardwareKeyboard
    }

    fun updateCandidatesViewShown(shown: Boolean) {
        setCandidatesViewShown(shown)
        currentInputConnection.requestCursorUpdates(if(shown)InputConnection.CURSOR_UPDATE_MONITOR else 0)
    }

}
