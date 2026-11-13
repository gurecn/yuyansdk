package com.yuyan.imemodule.service

import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.os.SystemClock
import android.text.InputType
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
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
import androidx.core.view.isGone

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
    private val suppressedFrames = mutableSetOf<View>()
    // 光标前文本缓存，避免在按键处理中同步跨进程取文本
    private val textCache = EditorTextCache { currentInputConnection }
    // 方向键语义的光标移动，避免把按键事件交给宿主触发焦点导航
    private val cursorMover = CursorMover({ currentInputConnection }, textCache)

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
        applyFloatingWindowLayout()
        KeyboardManager.instance.switchKeyboard()
    }
    private val candidatesObserver = Observer<Any?> { _ ->
        if (::mInputView.isInitialized) mInputView.onCandidateChanged()
    }

    override fun onCreate() {
        super.onCreate()
        showVirtualKeyboardOnPhysicalKeyboard = getInstance().keyboardSetting.showVirtualKeyboardOnPhysicalKeyboard.getValue()
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

    /**
     * 框架会根据系统设置 SHOW_IME_WITH_HARD_KEYBOARD 与 Configuration.keyboard 决定是否展示输入视图，
     * 并在为 false 时把窗口按“只显示候选栏”配置。
     */
    override fun onShowInputRequested(flags: Int, configChange: Boolean): Boolean {
        if (!isHardwareKeyboard || showVirtualKeyboardOnPhysicalKeyboard) return true
        return super.onShowInputRequested(flags, configChange)
    }

    override fun onStartInput(editorInfo: EditorInfo?, restarting: Boolean) {
        super.onStartInput(editorInfo, restarting)
        textCache.onStartInput(editorInfo)
        cursorMover.onStartInput(editorInfo)
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
        applyFloatingWindowLayout()
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
        updateInputViewShown()
        CoroutineScope(Dispatchers.Main).launch {
            delay(200) // 延时，解决获取屏幕尺寸不准确
            EnvironmentSingleton.instance.initData(baseContext)
            KeyboardLoaderUtil.instance.clearKeyboardMap()
            KeyboardManager.instance.clearKeyboard()
            KeyboardManager.instance.switchKeyboard()
            if (::mInputView.isInitialized) mInputView.setConfiguration(newConfig)
            applyFloatingWindowLayout()
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

    override fun onConfigureWindow(win: Window, isFullscreen: Boolean, isCandidatesOnly: Boolean) {
        super.onConfigureWindow(win, isFullscreen, isCandidatesOnly)
        applyFloatingWindowLayout(win, isCandidatesOnly)
    }

    fun applyFloatingWindowLayout() {
        val win = window?.window ?: return
        applyFloatingWindowLayout(win, false)
    }

    /**
     * 当前窗口是否需要在 inputArea 里显示 InputView。
     * 物理键盘（未开启软键盘开关）时只显示悬浮候选栏，不能按键盘高度配置窗口。
     */
    private fun isInputViewRequired(): Boolean {
        if (!::inputView.isInitialized || isHardwareKeyboard) return false
        if (!onEvaluateInputViewShown()) return false
        if (isShowInputRequested) return true
        return (inputView.parent as? View)?.visibility == View.VISIBLE
    }

    private fun applyFloatingWindowLayout(win: Window, isCandidatesOnly: Boolean) {
        val inputViewRequired = isInputViewRequired()
        val fillWindow = EnvironmentSingleton.instance.keyboardModeFloat &&
                (inputViewRequired || !isCandidatesOnly)
        val targetHeight = if (fillWindow) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
        win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, targetHeight)
        val inputFrame = if (::inputView.isInitialized) inputView.parent as? ViewGroup else null
        inputFrame?.layoutParams?.let { lp ->
            if (lp.height != targetHeight) {
                lp.height = targetHeight
                inputFrame.layoutParams = lp
            }
        }
        if (fillWindow || inputViewRequired) {
            val panel = inputFrame?.parent as? ViewGroup
            if (panel != null) {
                for (i in 0 until panel.childCount) {
                    val child = panel.getChildAt(i)
                    if (child !== inputFrame && child.visibility != View.GONE) {
                        suppressedFrames.add(child)
                        child.visibility = View.GONE
                    }
                }
            }
        } else if (suppressedFrames.isNotEmpty()) {
            suppressedFrames.forEach { if (it.isGone) it.visibility = View.VISIBLE }
            suppressedFrames.clear()
        }
    }

    override fun onComputeInsets(outInsets: Insets) {
        val (x, y, width, height) = if(::mInputView.isInitialized) mInputView.getKeyboardRect() else intArrayOf(0, 0, 0, 0)
        outInsets.apply {
            if(isHardwareKeyboard || EnvironmentSingleton.instance.keyboardModeFloat) {
                contentTopInsets = EnvironmentSingleton.instance.mScreenHeight
                visibleTopInsets = EnvironmentSingleton.instance.mScreenHeight
                touchableInsets = Insets.TOUCHABLE_INSETS_REGION
                touchableRegion.set(x, y, x + width, y + height)
            } else {
                contentTopInsets = y
                visibleTopInsets = y
                touchableInsets = Insets.TOUCHABLE_INSETS_CONTENT
                touchableRegion.setEmpty()
            }
        }
    }

    override fun onUpdateSelection(oldSelStart: Int, oldSelEnd: Int, newSelStart: Int, newSelEnd: Int, candidatesStart: Int, candidatesEnd: Int) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd)
        textCache.onUpdateSelection(newSelStart, newSelEnd) // 先校准缓存，再响应界面逻辑
        cursorMover.onSelectionReported(newSelStart, newSelEnd)
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
                    KeyCharacterMap.VIRTUAL_KEYBOARD, 0, KeyEvent.FLAG_SOFT_KEYBOARD or KeyEvent.FLAG_KEEP_TOUCH_MODE)
            )
        }
    }

    fun sendUpKeyEvent(eventTime: Long, keyEventCode: Int, metaState: Int = 0) {
        withInputConnection {
            it.sendKeyEvent(
                KeyEvent(eventTime, SystemClock.uptimeMillis(), KeyEvent.ACTION_UP, keyEventCode, 0, metaState,
                    KeyCharacterMap.VIRTUAL_KEYBOARD, 0, KeyEvent.FLAG_SOFT_KEYBOARD or KeyEvent.FLAG_KEEP_TOUCH_MODE)
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
     * 删除键交给宿主按自身语义删除，删掉的内容在光标回报后通过 [onDeleted] 回调。
     * 不走 sendCombinationKeyEvents，否则发键后缓存会被整份作废。
     */
    fun deleteBackwardByKey(onDeleted: (String) -> Unit) {
        if (currentInputConnection == null) return
        textCache.beginKeyDelete(onDeleted)
        val eventTime = SystemClock.uptimeMillis()
        sendDownKeyEvent(eventTime, KeyEvent.KEYCODE_DEL)
        sendUpKeyEvent(eventTime, KeyEvent.KEYCODE_DEL)
    }

    /** 当前输入框不响应删除键事件，需改用编辑命令删除 */
    val keyDeleteUnsupported: Boolean get() = textCache.keyDeleteUnsupported

    /** 不记录内容地删除光标前 [length] 个字符，返回编辑器是否接受 */
    fun deleteSurroundingText(length: Int): Boolean {
        var accepted = false
        withInputConnection { accepted = it.deleteSurroundingText(length, 0) }
        textCache.onEditorChanged()
        return accepted
    }

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

    /**
     * 方向键语义的选区变更（setSelection），文本内容未变，不触发缓存失效
     */
    fun setSelection(start: Int, end: Int) {
        withInputConnection {
            if (it.setSelection(start, end)) textCache.onSelectionMovedByIme(start, end)
            else textCache.onEditorChanged()
        }
    }

    /**
     * 方向键向左/右移动一格光标，活动实现见 [CursorMover]
     */
    fun moveCursor(keyCode: Int) = cursorMover.move(keyCode)

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