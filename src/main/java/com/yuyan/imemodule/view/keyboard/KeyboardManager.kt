package com.yuyan.imemodule.view.keyboard

import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.view.keyboard.container.BaseContainer
import com.yuyan.imemodule.view.keyboard.container.CandidatesContainer
import com.yuyan.imemodule.view.keyboard.container.HandwritingTextContainer
import com.yuyan.imemodule.view.keyboard.container.InputBaseContainer
import com.yuyan.imemodule.view.keyboard.container.InputViewParent
import com.yuyan.imemodule.view.keyboard.container.NumberContainer
import com.yuyan.imemodule.view.keyboard.container.QwertyTextContainer
import com.yuyan.imemodule.view.keyboard.container.SettingsContainer
import com.yuyan.imemodule.view.keyboard.container.SymbolContainer
import com.yuyan.imemodule.view.keyboard.container.T9TextContainer

/**
 * 键盘显示管理类
 */
class KeyboardManager {
    private var mInputView: InputView? = null

    /**
     * 输入法状态
     * 无，空闲，输入，编辑，联想，完成
     */
    enum class KeyboardType {
        T9, QWERTY, LX17, QWERTYABC, NUMBER, SYMBOL, SETTINGS, HANDWRITING, CANDIDATES
    }

    private var mKeyboardRootView: InputViewParent? = null
    private val keyboards = HashMap<KeyboardType, BaseContainer?>()
    private var mCurrentKeyboardName: KeyboardType? = null
    var currentContainer: BaseContainer? = null
        private set

    fun setData(keyboardRootView: InputViewParent?, inputView: InputView) {
        keyboards.clear() // TODO 清空缓存界面，发现调用 PinyinService.onCreateInputView时，原输入界面全部会失效。
        mKeyboardRootView = keyboardRootView
        mInputView = inputView
    }

    fun clearKeyboard() {
        keyboards.clear()
        if (mInputView != null) {
            mInputView!!.initView(mInputView!!.context)
        }
    }

    fun switchKeyboard(layout: Int) {
        val keyboardName = when (layout) {
            0x1000 -> KeyboardType.QWERTY
            0x4000 -> KeyboardType.QWERTYABC
            0x3000 -> KeyboardType.HANDWRITING
            0x5000 -> KeyboardType.NUMBER
            0x6000 -> KeyboardType.LX17
            else -> KeyboardType.T9
        }
        switchKeyboard(keyboardName)
    }

    fun switchKeyboard(keyboardName: KeyboardType) {
        if (mKeyboardRootView == null) return
        var container = keyboards[keyboardName]
        if (container == null) {
            container = when (keyboardName) {
                KeyboardType.CANDIDATES ->  CandidatesContainer(ImeSdkApplication.context, mInputView)
                KeyboardType.HANDWRITING -> HandwritingTextContainer(ImeSdkApplication.context, mInputView)
                KeyboardType.NUMBER -> NumberContainer(ImeSdkApplication.context, mInputView)
                KeyboardType.QWERTY -> QwertyTextContainer(ImeSdkApplication.context, mInputView, InputModeSwitcherManager.MASK_SKB_LAYOUT_QWERTY_PINYIN)
                KeyboardType.SETTINGS -> SettingsContainer(ImeSdkApplication.context, mInputView)
                KeyboardType.SYMBOL -> SymbolContainer(ImeSdkApplication.context, mInputView)
                KeyboardType.QWERTYABC -> QwertyTextContainer(ImeSdkApplication.context, mInputView, InputModeSwitcherManager.MASK_SKB_LAYOUT_QWERTY_ABC)
                KeyboardType.LX17 -> QwertyTextContainer(ImeSdkApplication.context, mInputView, InputModeSwitcherManager.MASK_SKB_LAYOUT_LX17)
                else ->  T9TextContainer(ImeSdkApplication.context, mInputView)
            }
            container.updateSkbLayout()
            keyboards[keyboardName] = container
        }
        mKeyboardRootView!!.showView(container)
        mCurrentKeyboardName = keyboardName
        currentContainer = container
    }

    val isInputKeyboard: Boolean
        get() = currentContainer is InputBaseContainer

    companion object {
        private val TAG = KeyboardManager::class.java.getSimpleName()
        private var mInstance: KeyboardManager? = null
        @JvmStatic
        val instance: KeyboardManager
            get() {
                if (null == mInstance) {
                    mInstance = KeyboardManager()
                }
                return mInstance!!
            }
    }
}