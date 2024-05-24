package com.yuyan.imemodule.view.keyboard

import android.content.Context
import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.service.DecodingInfo
import com.yuyan.imemodule.utils.LogUtil.d
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
    private var mDecInfo: DecodingInfo? = null
    private var mInputModeSwitcher: InputModeSwitcherManager? = null

    /**
     * 输入法状态
     * 无，空闲，输入，编辑，联想，完成
     */
    enum class KeyboardType {
        T9,
        QWERTY,
        LX17,
        QWERTYABC,
        NUMBER,
        SYMBOL,
        SETTINGS,
        HANDWRITING,
        CANDIDATES
    }

    private var mKeyboardRootView: InputViewParent? = null
    private val keyboards = HashMap<KeyboardType, BaseContainer?>()
    private var mCurrentKeyboardName: KeyboardType? = null
    var currentContainer: BaseContainer? = null
        private set

    fun setData(
        keyboardRootView: InputViewParent?,
        inputView: InputView,
        decInfo: DecodingInfo?,
        inputModeSwitcher: InputModeSwitcherManager?
    ) {
        keyboards.clear() // TODO 清空缓存界面，发现调用 PinyinService.onCreateInputView时，原输入界面全部会失效。
        mKeyboardRootView = keyboardRootView
        mInputView = inputView
        mDecInfo = decInfo
        mInputModeSwitcher = inputModeSwitcher
    }

    fun clearKeyboard() {
        d(TAG, "clearKeyboard")
        keyboards.clear()
        if (mInputView != null) {
            mInputView!!.initView(mInputView!!.context)
        }
    }

    fun addKeyboard(keyboardName: KeyboardType, baseContainer: BaseContainer?) {
        keyboards[keyboardName] = baseContainer
    }

    fun switchKeyboard(keyboardName: KeyboardType) {
        if (mKeyboardRootView == null) return
        d(TAG, "switchKeyboard1")
        val container = getContainer(keyboardName)
        mKeyboardRootView!!.showView(container)
        mCurrentKeyboardName = keyboardName
        currentContainer = container
    }

    fun switchKeyboard(layout: Int) {
        if (mKeyboardRootView == null) return
        d(TAG, "switchKeyboard2")
        val keyboardName = when (layout) {
            0x1000 -> KeyboardType.QWERTY
            0x4000 -> KeyboardType.QWERTYABC
            0x3000 -> KeyboardType.HANDWRITING
            0x5000 -> KeyboardType.NUMBER
            0x6000 -> KeyboardType.LX17
            else -> KeyboardType.T9
        }
        val container = getContainer(keyboardName)
        mKeyboardRootView!!.showView(container)
        mCurrentKeyboardName = keyboardName
        currentContainer = container
    }

    val isInputKeyboard: Boolean
        get() = currentContainer is InputBaseContainer

    /**
     * 获取 BaseContainer对象，不存在则创建。
     */
    @Synchronized
    private fun getContainer(keyboardName: KeyboardType): BaseContainer {
        d(TAG, "getContainer keyboardName:$keyboardName")
        var container = keyboards[keyboardName]
        if (container == null) {
            container = when (keyboardName) {
                KeyboardType.CANDIDATES -> {
                    val mCandidatesContainer = CandidatesContainer(ImeSdkApplication.context)
                    mCandidatesContainer.setService(mInputView, mDecInfo, mInputModeSwitcher)
                    instance!!.addKeyboard(KeyboardType.CANDIDATES, mCandidatesContainer)
                    mCandidatesContainer
                }

                KeyboardType.HANDWRITING -> {
                    val mHandwritingTextContainer = HandwritingTextContainer(ImeSdkApplication.context)
                    mHandwritingTextContainer.setService(mInputView, mDecInfo, mInputModeSwitcher)
                    mHandwritingTextContainer.updateSkbLayout(InputModeSwitcherManager.MASK_SKB_LAYOUT_HANDWRITING)
                    instance!!.addKeyboard(KeyboardType.HANDWRITING, mHandwritingTextContainer)
                    mHandwritingTextContainer
                }

                KeyboardType.NUMBER -> {
                    val mNumberContainer = NumberContainer(ImeSdkApplication.context)
                    mNumberContainer.setService(mInputView, mDecInfo, mInputModeSwitcher)
                    mNumberContainer.updateSkbLayout(InputModeSwitcherManager.MASK_SKB_LAYOUT_NUMBER)
                    instance!!.addKeyboard(KeyboardType.NUMBER, mNumberContainer)
                    mNumberContainer
                }

                KeyboardType.QWERTY -> {
                    val mQwertyTextContainer = QwertyTextContainer(ImeSdkApplication.context)
                    mQwertyTextContainer.setService(mInputView, mDecInfo, mInputModeSwitcher)
                    mQwertyTextContainer.updateSkbLayout(InputModeSwitcherManager.MASK_SKB_LAYOUT_QWERTY_PINYIN)
                    instance!!.addKeyboard(KeyboardType.QWERTY, mQwertyTextContainer)
                    mQwertyTextContainer
                }

                KeyboardType.SETTINGS -> {
                    val mSettingsContainer = SettingsContainer(ImeSdkApplication.context)
                    mSettingsContainer.setService(mInputView, mDecInfo, mInputModeSwitcher)
                    instance!!.addKeyboard(KeyboardType.SETTINGS, mSettingsContainer)
                    mSettingsContainer
                }

                KeyboardType.SYMBOL -> {
                    val mSymbolContainer = SymbolContainer(ImeSdkApplication.context)
                    mSymbolContainer.setService(mInputView, mDecInfo, mInputModeSwitcher)
                    instance!!.addKeyboard(KeyboardType.SYMBOL, mSymbolContainer)
                    mSymbolContainer
                }

                KeyboardType.QWERTYABC -> {
                    val mQwertyABCTextContainer = QwertyTextContainer(ImeSdkApplication.context)
                    mQwertyABCTextContainer.setService(mInputView, mDecInfo, mInputModeSwitcher)
                    mQwertyABCTextContainer.updateSkbLayout(InputModeSwitcherManager.MASK_SKB_LAYOUT_QWERTY_ABC)
                    instance!!.addKeyboard(KeyboardType.QWERTYABC, mQwertyABCTextContainer)
                    mQwertyABCTextContainer
                }

                KeyboardType.LX17 -> {
                    val mQwertyABCTextContainer = QwertyTextContainer(ImeSdkApplication.context)
                    mQwertyABCTextContainer.setService(mInputView, mDecInfo, mInputModeSwitcher)
                    mQwertyABCTextContainer.updateSkbLayout(InputModeSwitcherManager.MASK_SKB_LAYOUT_LX17)
                    instance!!.addKeyboard(KeyboardType.LX17, mQwertyABCTextContainer)
                    mQwertyABCTextContainer
                }

                else -> {
                    val mT9TextContainer = T9TextContainer(ImeSdkApplication.context)
                    mT9TextContainer.setService(mInputView, mDecInfo, mInputModeSwitcher)
                    mT9TextContainer.updateSkbLayout(InputModeSwitcherManager.MASK_SKB_LAYOUT_T9_PINYIN)
                    instance!!.addKeyboard(KeyboardType.T9, mT9TextContainer)
                    mT9TextContainer
                }
            }
            keyboards[keyboardName] = container
        }
        return container
    }

    companion object {
        private val TAG = KeyboardManager::class.java.getSimpleName()
        private var mInstance: KeyboardManager? = null
        @JvmStatic
        val instance: KeyboardManager?
            get() {
                if (null == mInstance) {
                    mInstance = KeyboardManager()
                }
                return mInstance
            }
    }
}
