package com.yuyan.imemodule.singleton

import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.prefs.AppPrefs
import kotlin.math.max
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
    var systemNavbarWindowsBottom = 0 // 导航栏高度
    var mScreenWidth = 0 // 屏幕的宽度
    var mScreenHeight = 0 // 屏幕的高度
    var skbWidth = 0 // 键盘区域、候选词区域宽度
        private set
    var inputAreaHeight = 0 // 键盘区域高度
    var skbHeight = 0 // 键盘区域高度
        private set
    var holderWidth = 0 // 单手模式下键盘占位区域宽度
        private set
    var heightForCandidates = 0 // 候选词区域的高度
    var heightForComposingView = 0 // 拼音显示区域高度
    var keyTextSize = 0 // 正常按键中文本的大小
    var keyTextSmallSize = 0 // 正常按键中文本的大小,小值
    var candidateTextSize = 0 // 候选词字体大小
    var isLandscape = false //键盘是否横屏
    var keyXMargin = 0f //键盘按键水平间距
    var keyYMargin = 0f //键盘按键垂直间距
    // 按键 + 候选词+拼音总高度，值是相对于竖屏宽度，横屏高度。
    private var keyboardHeightRatio = 0f

    init {
        initData()
    }

    fun initData() {
        val resources = ImeSdkApplication.context.resources
        val dm = resources.displayMetrics
        mScreenWidth = dm.widthPixels
        mScreenHeight = dm.heightPixels
        isLandscape = mScreenHeight <= mScreenWidth
        var screenWidthVertical = min(dm.widthPixels, dm.heightPixels)
        var screenHeightVertical = max(dm.widthPixels, dm.heightPixels)
        if(isLandscape || AppPrefs.getInstance().keyboardSetting.keyboardModeFloat.getValue()){
            screenWidthVertical = (screenWidthVertical*3f/4).toInt()
            screenHeightVertical = (screenHeightVertical*3f/4).toInt()
        }
        val oneHandedMod = AppPrefs.getInstance().keyboardSetting.oneHandedModSwitch.getValue()
        // 按键 + 后续高度，值是相对于竖屏宽度，横屏高度。
        keyboardHeightRatio = AppPrefs.getInstance().internal.keyboardHeightRatio.getValue()
        skbHeight = min((screenHeightVertical * keyboardHeightRatio).toInt(), screenWidthVertical)
        // 键盘占位宽度（用于单手模式），值是相对于竖屏宽度，横屏高度。
        holderWidth = if (oneHandedMod) (AppPrefs.getInstance().internal.keyboardHolderWidthRatio.getValue() * screenWidthVertical).toInt() else 0
        skbWidth = screenWidthVertical - holderWidth
        heightForCandidates = (screenHeightVertical * 0.3 * 0.15f).toInt()
        heightForComposingView = (heightForCandidates*0.7f).toInt()
        keyTextSize = (skbHeight * 0.06f).toInt()
        keyTextSmallSize = (skbHeight * 0.04f).toInt()
        keyXMargin = AppPrefs.getInstance().keyboardSetting.keyXMargin.getValue() / 1000f
        keyYMargin = AppPrefs.getInstance().keyboardSetting.keyYMargin.getValue() / 1000f
        candidateTextSize = (heightForCandidates * (0.4f + AppPrefs.getInstance().keyboardSetting.candidateTextSize.getValue()/100f)).toInt()
        inputAreaHeight = skbHeight + heightForCandidates + heightForComposingView
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
        private var mInstance: EnvironmentSingleton? = null
        @JvmStatic
		val instance: EnvironmentSingleton
            get() {
                if (null == mInstance) {
                    mInstance = EnvironmentSingleton()
                }
                return mInstance!!
            }
    }
}
