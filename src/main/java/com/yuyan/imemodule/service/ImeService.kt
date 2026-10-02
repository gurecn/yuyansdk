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
import androidx.lifecycle.Observer
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
    private lateinit var candidateView: CandidateView
    private lateinit var inputView: InputView
    private lateinit var mInputView: IKeyboardView
    private var cursorMonitoring = false
    // 光标前文本缓存，避免在按键处理中同步跨进程取文本
    private val textCache = EditorTextCache { currentInputConnection }

    private val onThemeChangeListener = OnThemeChangeListener { _: Theme? ->
        if (::mInputView.isInitialized) mInputView.updateTheme()
    }

    private val clipboardUpdateContentListener = ManagedPreference.OnChangeListener<String> { _, value ->
        if (getInstance().clipboard.clipboardSuggestion.getValue()) {
            if (value.isNotBlank()) {
                if (KeyboardManager.instance.currentContainer is ClipBoardContainer && (KeyboardManager.instance.currentContainer as ClipBoardContainer).getMenuMode() == SkbMenuMode.ClipBoard) {
                    (KeyboardManager.instance.currentContainer as ClipBoardContainer).showClipBoardView(SkbMenuMode.ClipBoard)
                } else {
                    if (::mInputView.isInitialized) mInputView.showSymbols(arrayOf(value))
                }
            }
        }
    }
    private val showVirtualKeyboardOnPhysicalKeyboardListener = ManagedPreference.OnChangeListener<Boolean> { _, value ->
        showVirtualKeyboardOnPhysicalKeyboard = value
        handleHardwareKeyboard()
        updateCandidatesViewShown(isHardwareKeyboard)
        updateInputViewShown()
    }
    private val candidatesObserver = Observer<Any?> { _ ->
        if (::mInputView.isInitialized) mInputView.onCandidateChanged()
    }

    override fun onCreate() {
        super.onCreate()
        addOnChangedListener(onThemeChangeListener)
        getInstance().internal.clipboardUpdateContent.registerOnChangeListener(clipboardUpdateContentListener)
        getInstance().keyboardSetting.showVirtualKeyboardOnPhysicalKeyboard.registerOnChangeListener(showVirtualKeyboardOnPhysicalKeyboardListener)
        if (!::inputView.isInitialized) inputView = InputView(baseContext, this)
        if (!::candidateView.isInitialized) candidateView = CandidateView(baseContext, this)
        handleHardwareKeyboard()
        DictDecoder.candidatesLiveData.observeForever(candidatesObserver)
    }

    override fun onCreateInputView(): View {
        if(!::inputView.isInitialized)inputView = InputView(baseContext, this)
        if(!isHardwareKeyboard)mInputView = inputView
        return inputView
    }

    override fun onCreateCandidatesView(): View {
        if(!::candidateView.isInitialized)candidateView = CandidateView(baseContext, this)
        if(isHardwareKeyboard) {
            mInputView = candidateView
            currentInputConnection?.requestCursorUpdates(InputConnection.CURSOR_UPDATE_MONITOR)
        }
        return candidateView
    }

    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        return if (showVirtualKeyboardOnPhysicalKeyboard) true else !isHardwareKeyboard
    }

    override fun onStartInput(editorInfo: EditorInfo?, restarting: Boolean) {
        super.onStartInput(editorInfo, restarting)
        textCache.onStartInput(editorInfo)
        if (::inputView.isInitialized) inputView.clearDeletedTextHistory()
        if (editorInfo != null) {
            YuyanEmojiCompat.setEditorInfo(editorInfo)
            InputModeSwitcher.requestInputWithSkb(editorInfo)
        }
        syncActiveView()
        updateCandidatesViewShown(isHardwareKeyboard)
        if (isHardwareKeyboard) {
            currentInputConnection?.requestCursorUpdates(InputConnection.CURSOR_UPDATE_MONITOR)
            cursorMonitoring = true
        }
    }

    override fun onStartInputView(editorInfo: EditorInfo, restarting: Boolean) {
        if (::mInputView.isInitialized) mInputView.onStartInputView(editorInfo, restarting)
        super.onStartInputView(editorInfo, restarting)
    }

    override fun onFinishInput() {
        textCache.onFinishInput()
        if (::inputView.isInitialized) inputView.clearDeletedTextHistory()
        super.onFinishInput()
    }

    override fun onDestroy() {
        super.onDestroy()
        textCache.onFinishInput()
        DictDecoder.candidatesLiveData.removeObserver(candidatesObserver)
        getInstance().keyboardSetting.showVirtualKeyboardOnPhysicalKeyboard.unregisterOnChangeListener(showVirtualKeyboardOnPhysicalKeyboardListener)
        removeOnChangedListener(onThemeChangeListener)
        getInstance().internal.clipboardUpdateContent.unregisterOnChangeListener(clipboardUpdateContentListener)
    }

    /**
     * 横竖屏切换
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        handleHardwareKeyboard(newConfig)
        updateCandidatesViewShown(isHardwareKeyboard)
        CoroutineScope(Dispatchers.Main).launch {
            delay(200) // 延时，解决获取屏幕尺寸不准确
            EnvironmentSingleton.instance.initData(baseContext)
            KeyboardLoaderUtil.instance.clearKeyboardMap()
            KeyboardManager.instance.clearKeyboard()
            KeyboardManager.instance.switchKeyboard()
            if (::mInputView.isInitialized) mInputView.setConfiguration(newConfig)
        }
        onSystemDarkModeChange(newConfig.isDarkMode())
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (0 != event.repeatCount || event.isShiftPressed || event.isMetaPressed) super.onKeyDown(keyCode, event)
        else if (event.isCtrlPressed && keyCode != KeyEvent.KEYCODE_SPACE) super.onKeyDown(keyCode, event)
        else if (::mInputView.isInitialized) mInputView.processKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
        else super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return if (0 != event.repeatCount || event.isShiftPressed || event.isMetaPressed) super.onKeyUp(keyCode, event)
        else if (event.isCtrlPressed && keyCode != KeyEvent.KEYCODE_SPACE) super.onKeyUp(keyCode, event)
        else if (::mInputView.isInitialized) mInputView.processKeyUp(event) || super.onKeyUp(keyCode, event)
        else super.onKeyUp(keyCode, event)
    }

    override fun setInputView(view: View) {
        (view.parent as? ViewGroup)?.removeView(view)
        super.setInputView(view)
        val layoutParams = view.layoutParams
        if (layoutParams != null && layoutParams.height != ViewGroup.LayoutParams.MATCH_PARENT) {
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            view.setLayoutParams(layoutParams)
        }
    }

    override fun setCandidatesView(view: View?) {
        if (view != null) (view.parent as? ViewGroup)?.removeView(view)
        super.setCandidatesView(view)
    }

    override fun onEvaluateFullscreenMode(): Boolean = false // 修复横屏之后输入框遮挡问题


    override fun onComputeInsets(outInsets: Insets) {
        val (x, y, width, height) = if(::mInputView.isInitialized) mInputView.getKeyboardRect() else intArrayOf(0, 0)
        outInsets.apply {
            if(isHardwareKeyboard || EnvironmentSingleton.instance.keyboardModeFloat) {
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
        textCache.onUpdateSelection(newSelStart, newSelEnd) // 先校准缓存，再响应界面逻辑
        if (::mInputView.isInitialized) mInputView.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesEnd)
    }

    private val cursorAnchorPosition = FloatArray(2)
    override fun onUpdateCursorAnchorInfo(cursorAnchorInfo: CursorAnchorInfo?) {
        super.onUpdateCursorAnchorInfo(cursorAnchorInfo)
        if (!isHardwareKeyboard || cursorAnchorInfo == null) return
        cursorAnchorPosition[0] = cursorAnchorInfo.insertionMarkerHorizontal
        cursorAnchorPosition[1] = cursorAnchorInfo.insertionMarkerBottom
        val matrix = cursorAnchorInfo.getMatrix()
        if (matrix != null) {
            matrix.mapPoints(cursorAnchorPosition)
        }
        if (cursorAnchorPosition[0].isNaN() || cursorAnchorPosition[1].isNaN()) return
        if (::mInputView.isInitialized) mInputView.updatePosition(cursorAnchorPosition)
    }

    override fun onWindowShown() {
        if (::mInputView.isInitialized) mInputView.onWindowShown()
        super.onWindowShown()
    }

    override fun onWindowHidden() {
        if (::mInputView.isInitialized) mInputView.onWindowHidden()
        super.onWindowHidden()
        if (cursorMonitoring) {
            currentInputConnection?.requestCursorUpdates(0)
            cursorMonitoring = false
        }
    }

    /**
     * 模拟 Enter 按键点击
     */
    fun sendEnterKeyEvent() {
        textCache.onEditorChanged()
        YuyanEmojiCompat.mEditorInfo?.run {
            if (inputType and InputType.TYPE_MASK_CLASS == InputType.TYPE_NULL || imeOptions.hasFlag(EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
                sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER)
            } else if (!actionLabel.isNullOrEmpty() && actionId != EditorInfo.IME_ACTION_UNSPECIFIED) {
                withInputConnection { it.performEditorAction(actionId) }
            } else when (val action = imeOptions and EditorInfo.IME_MASK_ACTION) {
                EditorInfo.IME_ACTION_UNSPECIFIED, EditorInfo.IME_ACTION_NONE -> sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER)
                else -> withInputConnection { it.performEditorAction(action) }
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
        // 按键的实际编辑范围由应用决定，不能据此推算或记录删除的文本。
        textCache.onEditorChanged()
    }

    fun sendDownKeyEvent(eventTime: Long, keyEventCode: Int, metaState: Int = 0) {
        withInputConnection {
            it.sendKeyEvent(
                KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyEventCode, 0, metaState,
                    KeyCharacterMap.VIRTUAL_KEYBOARD, keyEventCode, KeyEvent.FLAG_SOFT_KEYBOARD or KeyEvent.FLAG_KEEP_TOUCH_MODE)
            )
        }
    }

    fun sendUpKeyEvent(eventTime: Long, keyEventCode: Int, metaState: Int = 0) {
        withInputConnection {
            it.sendKeyEvent(
                KeyEvent(eventTime, SystemClock.uptimeMillis(), KeyEvent.ACTION_UP, keyEventCode, 0, metaState,
                    KeyCharacterMap.VIRTUAL_KEYBOARD, keyEventCode, KeyEvent.FLAG_SOFT_KEYBOARD or KeyEvent.FLAG_KEEP_TOUCH_MODE)
            )
        }
    }

    /**
     * 向输入框提交预选词
     */
    fun setComposingText(text: CharSequence) {
        withInputConnection {
            it.setComposingText(text, 1)
            textCache.onSetComposingText(text)
        }
    }


    /**
     * 结束提交预选词
     */
    fun finishComposingText() {
        withInputConnection {
            it.finishComposingText()
            textCache.onFinishComposingText()
        }
    }

    /**
     * 发送字符串给编辑框
     */
    fun commitText(text: String) {
        val content = StringUtils.converted2FlowerTypeface(text)
        withInputConnection {
            it.commitText(content, 1)
            textCache.onCommitText(content)
        }
    }

    /**
     * 发送字符串给编辑框
     */
    fun commitText(text: String, newCursorPosition: Int) {
        val content = StringUtils.converted2FlowerTypeface(text)
        withInputConnection {
            it.commitText(content, newCursorPosition)
            if (newCursorPosition == 1) textCache.onCommitText(content) else textCache.onEditorChanged()
        }
    }

    /** 原样还原已删除内容，不再执行花漾字体转换。 */
    fun restoreDeletedText(text: String): Boolean {
        var accepted = false
        withInputConnection {
            accepted = it.commitText(text, 1)
            if (accepted) textCache.onCommitText(text) else textCache.onEditorChanged()
        }
        return accepted
    }

    /** 删除请求被接受后才返回用于还原的文本，不同步查询编辑器。 */
    fun deleteLastCharBeforeCursor(): String? = textCache.deleteLastCharBeforeCursor()

    fun deleteTextBeforeCursor(length: Int): String? = textCache.deleteTextBeforeCursor(length)

    /**
     * 读取缓存中光标前文本，不做跨进程调用，缓存不可用时返回空串
     */
    fun peekTextBeforeCursor(length: Int): String = textCache.peekTextBeforeCursor(length)

    /**
     * 读取缓存中光标前一个字符，emoji 等代理对按整体返回
     */
    fun peekLastCharBeforeCursor(): String = textCache.peekLastCharBeforeCursor()

    /**
     * 异步请求光标前文本，缓存可用时同步回调
     */
    fun requestTextBeforeCursor(length: Int, callback: (String) -> Unit) = textCache.requestTextBeforeCursor(length, callback)

    fun commitTextEditMenu(id: Int) {
        withInputConnection {
            it.performContextMenuAction(id)
            textCache.onEditorChanged()
        }
    }

    fun performEditorAction(editorAction: Int) {
        withInputConnection {
            it.performEditorAction(editorAction)
            textCache.onEditorChanged()
        }
    }

    fun deleteSurroundingText(length: Int) {
        withInputConnection {
            if (it.deleteSurroundingText(length, 0)) textCache.onDeleteBeforeCursor(length)
            else textCache.onEditorChanged()
        }
    }

    fun setSelection(start: Int, end: Int) {
        withInputConnection {
            it.setSelection(start, end)
            textCache.onEditorChanged()
        }
    }

    /**
     * 输入框连接可能已失效，调用前做空安全与异常保护
     */
    private fun withInputConnection(block: (InputConnection) -> Unit) {
        val inputConnection = currentInputConnection ?: return
        try {
            block(inputConnection)
        } catch (_: Exception) {
            textCache.onEditorChanged()
        }
    }

    fun handleHardwareKeyboard(newConfig: Configuration? = null): Boolean {
        val hardwareKeyboard = if (showVirtualKeyboardOnPhysicalKeyboard) false
            else if (newConfig != null) (newConfig.keyboard != Configuration.KEYBOARD_NOKEYS)
            else resources.configuration.keyboard != Configuration.KEYBOARD_NOKEYS
        isHardwareKeyboard = hardwareKeyboard
        syncActiveView()
        return hardwareKeyboard
    }

    private fun syncActiveView() {
        if (::inputView.isInitialized && ::candidateView.isInitialized) {
            mInputView = if (isHardwareKeyboard) candidateView else inputView
        }
    }

    fun updateCandidatesViewShown(shown: Boolean) {
        setCandidatesViewShown(shown)
    }
}