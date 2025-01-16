package com.yuyan.imemodule.service

import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.yuyan.imemodule.data.emojicon.YuyanEmojiCompat
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager.OnThemeChangeListener
import com.yuyan.imemodule.data.theme.ThemeManager.addOnChangedListener
import com.yuyan.imemodule.data.theme.ThemeManager.removeOnChangedListener
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance
import com.yuyan.imemodule.prefs.behavior.SkbMenuMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.utils.KeyboardLoaderUtil
import com.yuyan.imemodule.view.keyboard.InputView
import com.yuyan.imemodule.view.keyboard.KeyboardManager
import com.yuyan.imemodule.view.keyboard.container.ClipBoardContainer
import com.yuyan.imemodule.view.preference.ManagedPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Main class of the Pinyin input method. 输入法服务
 */
class ImeService : InputMethodService() {
    private var isWindowShown = false // 键盘窗口是否已显示
    private lateinit var mInputView: InputView
    private val clipboardItemTimeout = getInstance().clipboard.clipboardItemTimeout.getValue()
    private val onThemeChangeListener = OnThemeChangeListener { _: Theme? -> if (::mInputView.isInitialized) mInputView.updateTheme() }
    private val clipboardUpdateContent = getInstance().internal.clipboardUpdateContent
    private val clipboardUpdateContentListener = ManagedPreference.OnChangeListener<String> { _, value ->
        if(getInstance().clipboard.clipboardSuggestion.getValue()){
            if(value.isNotBlank()) {
                if (::mInputView.isInitialized && mInputView.isShown) {
                    if(KeyboardManager.instance.currentContainer is ClipBoardContainer
                        && (KeyboardManager.instance.currentContainer as ClipBoardContainer).getMenuMode() == SkbMenuMode.ClipBoard ){
                        (KeyboardManager.instance.currentContainer as ClipBoardContainer).showClipBoardView(SkbMenuMode.ClipBoard)
                    } else {
                        mInputView.showSymbols(arrayOf(value))
                    }
                }
            }
        }
    }
    override fun onCreate() {
        super.onCreate()
        addOnChangedListener(onThemeChangeListener)
        clipboardUpdateContent.registerOnChangeListener(clipboardUpdateContentListener)
    }

    override fun onCreateInputView(): View {
        mInputView = InputView(baseContext, this)
        return mInputView
    }

    override fun onStartInputView(editorInfo: EditorInfo, restarting: Boolean) {
        if(!restarting) {
            YuyanEmojiCompat.setEditorInfo(editorInfo)
            mInputView.onStartInputView(editorInfo)
            if (getInstance().clipboard.clipboardSuggestion.getValue()) {
                val lastClipboardTime = getInstance().internal.clipboardUpdateTime.getValue()
                if (System.currentTimeMillis() - lastClipboardTime <= clipboardItemTimeout * 1000) {
                    val lastClipboardContent = getInstance().internal.clipboardUpdateContent.getValue()
                    if (lastClipboardContent.isNotBlank()) {
                        mInputView.showSymbols(arrayOf(lastClipboardContent))
                        getInstance().internal.clipboardUpdateTime.setValue(0L)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mInputView.isInitialized) mInputView.resetToIdleState()
        removeOnChangedListener(onThemeChangeListener)
        clipboardUpdateContent.unregisterOnChangeListener(clipboardUpdateContentListener)
    }

    /**
     * 横竖屏切换
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        CoroutineScope(Dispatchers.Main).launch {
            delay(200) //延时，解决获取屏幕尺寸不准确。
            EnvironmentSingleton.instance.initData()
            KeyboardLoaderUtil.instance.clearKeyboardMap()
            KeyboardManager.instance.clearKeyboard()
            if (::mInputView.isInitialized) KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        //  0 != event.getRepeatCount()   单次点击onKeyDown操作不处理，在onKeyUp时处理；长按时才处理onKeyDown操作。
        return if (0 != event.repeatCount) super.onKeyDown(keyCode, event)
        else if (isInputViewShown) true
        else super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return if (isInputViewShown) mInputView.processKey(event) || super.onKeyUp(keyCode, event)
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
        if (!::mInputView.isInitialized) return
        val (x, y) = intArrayOf(0, 0).also {if(mInputView.isAddPhrases) mInputView.mAddPhrasesLayout.getLocationInWindow(it) else mInputView.mSkbRoot.getLocationInWindow(it) }
        outInsets.apply {
            if(EnvironmentSingleton.instance.keyboardModeFloat) {
                contentTopInsets = EnvironmentSingleton.instance.mScreenHeight
                visibleTopInsets = EnvironmentSingleton.instance.mScreenHeight
                touchableInsets = Insets.TOUCHABLE_INSETS_REGION
                touchableRegion.set(x, y, x + mInputView.mSkbRoot.width, y + mInputView.mSkbRoot.height)
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
        if (::mInputView.isInitialized) mInputView.onUpdateSelection(newSelStart, newSelEnd)
    }

    override fun onWindowShown() {
        if(isWindowShown) return
        isWindowShown = true
        if (::mInputView.isInitialized) mInputView.onWindowShown()
        super.onWindowShown()
    }

    override fun onWindowHidden() {
        isWindowShown = false
        if(::mInputView.isInitialized) mInputView.onWindowHidden()
        super.onWindowHidden()
    }
}
