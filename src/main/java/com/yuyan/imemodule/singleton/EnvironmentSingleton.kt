package com.yuyan.imemodule.singleton

import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.KeyboardOneHandedMod
import com.yuyan.imemodule.utils.LogUtil
import kotlin.math.min

/**
 * Global environment configurations for showing soft keyboard and candidate
 * view. All original dimension values are defined in float, and the real size
 * is calculated from the float values of and screen size. In this way, this
 * input method can work even when screen size is changed.
 * 该类保存布局的一些尺寸。比如：屏幕的宽度、屏幕的高度
 * 、按键的高度、候选词区域的高度、按键气泡宽度比按键宽度大的差值、按键气泡高度比按键高度大的差值、正常按键中文本的大小
 * 、功能按键中文本的大小、正常按键气泡中文本的大小、功能按键气泡中文本的大小。
 */
class EnvironmentSingleton private constructor() {
    private var mScreenWidth = 0 // 屏幕的宽度

    /**
     * 屏幕高度
     */
    var screenHeight = 0 // 屏幕的高度
        private set

    /**
     * 键盘宽度
     */
    var skbWidth = 0 // 键盘区域、候选词区域宽度
        private set

    /**
     * 键盘高度
     */
    var skbHeight = 0 // 键盘区域高度
        private set

    /**
     * 获取键盘占位宽度
     */
    var holderWidth = 0 // 单手模式下键盘占位区域宽度
        private set

    //候选词高度
    var heightForCandidates = 0 // 候选词区域的高度
        private set

    //拼音显示区域高度
    var heightForComposingView = 0 // 拼音显示区域高度
        private set

    /**
     * 获得按键的文本大小
     */
    var keyTextSize = 0 // 正常按键中文本的大小
        private set

    /**
     * 获得按键的文本大小（小字符）
     */
    var keyTextSmallSize = 0 // 正常按键中文本的大小,小值
        private set

    /**
     * 获得候选词大小
     */
    var candidateTextSize = 0 // 候选词字体大小
        private set

    /**
     * 是否为横屏
     */
    var isOrientationLandscape = false //键盘是否横屏
        private set

    /**
     * 按键左右间隔距离
     */
    var keyXMargin = 0f //键盘按键水平间距
        private set

    /**
     * 按键上下间隔距离
     */
    var keyYMargin = 0f //键盘按键垂直间距
        private set

    init {
        LogUtil.d(TAG, "EnvironmentSingleton")
        initData()
    }

    fun initData() {
        LogUtil.d(TAG, "initData")
        val resources = ImeSdkApplication.context.resources
        val dm = resources.displayMetrics
        mScreenWidth = dm.widthPixels
        screenHeight = dm.heightPixels
        isOrientationLandscape = screenHeight <= mScreenWidth
        val oneHandedMod = ThemeManager.prefs.oneHandedMod.getValue()
        // 按键 + 后续高度，值是相对于竖屏宽度，横屏高度。
        keyboardHeightRatio = AppPrefs.getInstance().internal.keyboardHeightRatio.getValue()
        val keyboardWidthRatio = 1f
        val mKeyboardWidthContainer = (mScreenWidth * keyboardWidthRatio).toInt()
        skbHeight = min((screenHeight * keyboardHeightRatio).toInt(), mKeyboardWidthContainer)
        // 键盘占位宽度（用于单手模式），值是相对于竖屏宽度，横屏高度。
        holderWidth =
            if (oneHandedMod != KeyboardOneHandedMod.None) (AppPrefs.getInstance().internal.keyboardHolderWidthRatio.getValue() * mKeyboardWidthContainer).toInt() else 0
        skbWidth = mKeyboardWidthContainer - holderWidth
        heightForCandidates = (skbHeight * 0.15f).toInt()
        heightForComposingView = (skbHeight * 0.1f).toInt()
        keyTextSize = (skbHeight * NORMAL_KEY_TEXT_SIZE_RATIO).toInt()
        keyTextSmallSize = (skbHeight * NORMAL_KEY_TEXT_SIZE_SMALL).toInt()
        keyXMargin = ThemeManager.prefs.keyXMargin.getValue() / 1000f
        keyYMargin = ThemeManager.prefs.keyYMargin.getValue() / 1000f
        candidateTextSize = keyTextSize + ThemeManager.prefs.candidateTextSize.getValue()
    }

    var keyBoardHeightRatio: Float
        /**
         * 获取键盘高度比例：相对于屏幕宽度
         */
        get() = keyboardHeightRatio
        /**
         * 设置键盘高度比例：相对于屏幕宽度
         */
        set(keyBoardHeightRatio) {
            keyboardHeightRatio = keyBoardHeightRatio
            AppPrefs.getInstance().internal.keyboardHeightRatio.setValue(keyBoardHeightRatio)
        }

    companion object {
        private val TAG = EnvironmentSingleton::class.java.getSimpleName()

        /**
         * The text size for normal keys. It is relative to the smaller one of
         * screen width and height. 正常按键的文本的大小（大值)，值是相对于屏幕高度和宽度较小的那一个。
         */
        private const val NORMAL_KEY_TEXT_SIZE_RATIO = 0.06f

        /**
         * The text size for normal keys. It is relative to the smaller one of
         * screen width and height. 正常按键的文本的大小(小值)，值是相对于屏幕高度和宽度较小的那一个。
         */
        private const val NORMAL_KEY_TEXT_SIZE_SMALL = 0.04f

        /**
         * The configurations are managed in a singleton. 该类的实例，该类采用设计模式的单例模式。
         */
        private var mInstance: EnvironmentSingleton? = null


        /**
         * 按键 + 候选词+拼音总高度，值是相对于竖屏宽度，横屏高度。
         */
        private var keyboardHeightRatio = 0f
        @kotlin.jvm.JvmStatic
		val instance: EnvironmentSingleton?
            get() {
                if (null == mInstance) {
                    mInstance = EnvironmentSingleton()
                }
                return mInstance
            }
    }
}
