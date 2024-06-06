package com.yuyan.imemodule.service

import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.data.theme.ThemeManager.OnThemeChangeListener
import com.yuyan.imemodule.data.theme.ThemeManager.addOnChangedListener
import com.yuyan.imemodule.data.theme.ThemeManager.removeOnChangedListener
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.inputmethod.core.Kernel
import com.yuyan.imemodule.utils.KeyboardLoaderUtil
import com.yuyan.imemodule.utils.LogUtil
import com.yuyan.imemodule.utils.thread.ThreadPoolUtils
import com.yuyan.imemodule.view.keyboard.InputView
import com.yuyan.imemodule.view.keyboard.KeyboardManager

/**
 * Main class of the Pinyin input method. 输入法服务
 */
class ImeService : InputMethodService() {
    private var mInputView: InputView? = null
    private val onThemeChangeListener =
        OnThemeChangeListener { _: Theme? -> if (mInputView != null) mInputView!!.updateTheme() }

    override fun onCreate() {
        super.onCreate()
        Kernel.initWiIme(getInstance().internal.pinyinModeRime.getValue())
        addOnChangedListener(onThemeChangeListener)
    }

    override fun onCreateInputView(): View {
        mInputView = InputView(baseContext, this)
        return mInputView!!
    }

    override fun onStartInputView(editorInfo: EditorInfo, restarting: Boolean) {
        if (!restarting) {
            if (mInputView != null) {
                mInputView!!.onStartInputView(editorInfo)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.d(TAG, "onDestroy")
        if (mInputView != null) mInputView!!.resetToIdleState()
        ThreadPoolUtils.executeSingleton { Kernel.freeIme() }
        removeOnChangedListener(onThemeChangeListener)
    }

    /**
     * 横竖屏切换
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LogUtil.d(TAG, "onConfigurationChanged")
        EnvironmentSingleton.instance.initData()
        KeyboardLoaderUtil.instance.clearKeyboardMap()
        KeyboardManager.instance.clearKeyboard()
        if (mInputView != null) mInputView!!.resetToIdleState()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        //  0 != event.getRepeatCount()   单次点击onKeyDown操作不处理，在onKeyUp时处理；长按时才处理onKeyDown操作。
        return if (isInputViewShown || 0 == event.repeatCount) {
            super.onKeyDown(keyCode, event)
        } else {
            mInputView!!.processKey(event) || super.onKeyDown(keyCode, event)
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        LogUtil.d(TAG, "ImeService   onKeyUp:" + event.keyCode)
        return if (isInputViewShown) {
            mInputView!!.processKey(event) || super.onKeyUp(keyCode, event)
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
        val (x, y) = intArrayOf(0, 0).also { mInputView?.mSkbRoot?.getLocationInWindow(it) }
        outInsets.apply {
            if(!ThemeManager.prefs.keyboardModeFloat.getValue()) {
                contentTopInsets = y
                touchableInsets = Insets.TOUCHABLE_INSETS_CONTENT
                touchableRegion.setEmpty()
                visibleTopInsets = y
            } else {
                contentTopInsets = EnvironmentSingleton.instance.mScreenHeight
                visibleTopInsets = EnvironmentSingleton.instance.mScreenHeight
                touchableInsets = Insets.TOUCHABLE_INSETS_REGION
                touchableRegion.set(x, y, x + mInputView!!.mSkbRoot!!.width, y + mInputView!!.mSkbRoot!!.height)
            }
        }
    }

    companion object {
        private val TAG = ImeService::class.java.getSimpleName()
    }
}
