package com.yuyan.imemodule.service

import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager.OnThemeChangeListener
import com.yuyan.imemodule.data.theme.ThemeManager.addOnChangedListener
import com.yuyan.imemodule.data.theme.ThemeManager.removeOnChangedListener
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.inputmethod.core.Kernel
import com.yuyan.imemodule.utils.KeyboardLoaderUtil
import com.yuyan.imemodule.utils.thread.ThreadPoolUtils
import com.yuyan.imemodule.view.keyboard.InputView
import com.yuyan.imemodule.view.keyboard.KeyboardManager
import com.yuyan.imemodule.view.preference.ManagedPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Main class of the Pinyin input method. 输入法服务
 */
class ImeService : InputMethodService() {
    private lateinit var mInputView: InputView
    private val clipboardItemTimeout = getInstance().clipboard.clipboardItemTimeout.getValue()
    private val onThemeChangeListener = OnThemeChangeListener { _: Theme? -> if (::mInputView.isInitialized) mInputView.updateTheme() }
    private val clipboardUpdateContent = getInstance().internal.clipboardUpdateContent
    private val clipboardUpdateContentListener = ManagedPreference.OnChangeListener<String> { _, value ->
        if(getInstance().clipboard.clipboardSuggestion.getValue()){
            if(value.isNotBlank()) {
                if (::mInputView.isInitialized && mInputView.isShown) {
                    mInputView.showSymbols(arrayOf(value))
                    getInstance().internal.clipboardUpdateTime.setValue(0L)
                }
            }
        }
    }
    override fun onCreate() {
        super.onCreate()
        Kernel.initWiIme(getInstance().internal.pinyinModeRime.getValue())
        addOnChangedListener(onThemeChangeListener)
        clipboardUpdateContent.registerOnChangeListener(clipboardUpdateContentListener)
    }

    override fun onCreateInputView(): View {
        mInputView = InputView(baseContext, this)
        return mInputView
    }

    override fun onStartInputView(editorInfo: EditorInfo, restarting: Boolean) {
        mInputView.onStartInputView(editorInfo)
        if(getInstance().clipboard.clipboardSuggestion.getValue()){
            val lastClipboardTime = getInstance().internal.clipboardUpdateTime.getValue()
            if (System.currentTimeMillis() - lastClipboardTime <= clipboardItemTimeout * 1000) {
                val lastClipboardContent = getInstance().internal.clipboardUpdateContent.getValue()
                if(lastClipboardContent.isNotBlank()) {
                    mInputView.showSymbols(arrayOf(lastClipboardContent))
                    getInstance().internal.clipboardUpdateTime.setValue(0L)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mInputView.isInitialized) mInputView.resetToIdleState()
        ThreadPoolUtils.executeSingleton { Kernel.freeIme() }
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
            if (::mInputView.isInitialized) mInputView.resetToIdleState()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        //  0 != event.getRepeatCount()   单次点击onKeyDown操作不处理，在onKeyUp时处理；长按时才处理onKeyDown操作。
        return if (isInputViewShown || 0 == event.repeatCount) {
            super.onKeyDown(keyCode, event)
        } else {
            mInputView.processKey(event) || super.onKeyDown(keyCode, event)
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return if (isInputViewShown) {
            mInputView.processKey(event) || super.onKeyUp(keyCode, event)
        } else {
            super.onKeyUp(keyCode, event)
        }
    }

    override fun setInputView(view: View) {
        super.setInputView(view)
        val layoutParams = view.layoutParams
        if (layoutParams != null && layoutParams.height != ViewGroup.LayoutParams.MATCH_PARENT) {
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            view.setLayoutParams(layoutParams)
        }
    }

    override fun onComputeInsets(outInsets: Insets) {
        if (!::mInputView.isInitialized) return
        val (x, y) = intArrayOf(0, 0).also { mInputView.mSkbRoot.getLocationInWindow(it) }
        outInsets.apply {
            if(EnvironmentSingleton.instance.isLandscape || getInstance().keyboardSetting.keyboardModeFloat.getValue()) {
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

    override fun onWindowHidden() {
        if (::mInputView.isInitialized) mInputView.onWindowHidden()
        super.onWindowHidden()
    }
}
