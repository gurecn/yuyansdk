package com.yuyan.imemodule.application

import com.yuyan.imemodule.data.flower.FlowerTypefaceMode

/**
 * 用户自定义常量类
 */
object CustomConstant {
    var RIME_DICT_PATH = Launcher.instance.context.getExternalFilesDir("rime").toString()
    var HW_DICT_PATH: String  = Launcher.instance.context.getExternalFilesDir("hw").toString()
    const val SCHEMA_ZH_T9 = "t9_pinyin" // 拼音九键
    const val SCHEMA_ZH_QWERTY = "pinyin" // 拼音全键
    const val SCHEMA_EN = "english"         // 英语方案
    const val SCHEMA_ZH_HANDWRITING = "handwriting" // 手写输入
    const val SCHEMA_ZH_DOUBLE_FLYPY = "double_pinyin_" // 小鹤双拼
    const val SCHEMA_ZH_DOUBLE_LX17 = "double_pinyin_ls17" // 乱序17双拼
    const val SCHEMA_ZH_STROKE = "stroke" // 五笔画
    const val CURRENT_RIME_DICT_DATA_VERSIOM = 20251112
    const val YUYAN_IME_REPO = "https://github.com/gurecn/YuyanIme"
    const val LICENSE_URL = "https://www.gnu.org/licenses/gpl-3.0.html"
    const val FEEDBACK_TXC_REPO = "https://github.com/gurecn/YuyanIme/issues"

    // 花漾字状态
    var flowerTypeface = FlowerTypefaceMode.Disabled
    // 剪切板/常用语界面锁定
    var lockClipBoardEnable = false
}