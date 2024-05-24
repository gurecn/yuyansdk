package com.yuyan.imemodule.constant

import com.yuyan.imemodule.application.ImeSdkApplication
import java.io.File

/**
 * 用户自定义常量类
 * Created by Gaolei on 2016/11/25.
 */
object CustomConstant {
    var RIME_DICT_PATH = ImeSdkApplication.context.getExternalFilesDir("rime").toString()
    var HDW_DICT_PATH = ImeSdkApplication.context.getExternalFilesDir("hdw").toString()
    @JvmField
    var HDW_HANWANG_DICT_PATH = ImeSdkApplication.context.getExternalFilesDir("hdw").toString() + File.separator + "hanwang"
    const val SYMBOLS_TYPR_COMMON = 0 //符号： 0：中文；1：英文：2：数学；3：其他。序号需和SymbolsManager中mSymbolsEmoji Key对应
    const val EMOJI_TYPR_FACE_DATA = 4 //emoji表情：4。   序号需和SymbolsManager中mSymbolsEmoji Key对应
    const val EMOJI_TYPR_SMILE_TEXT = 5 //颜文字：5。   序号需和SymbolsManager中mSymbolsEmoji Key对应

    //按键长按选择
    const val LONG_PRESS_MOVE_ORIENTATION_NONE = 0
    const val LONG_PRESS_MOVE_ORIENTATION_LEFT = 1
    const val LONG_PRESS_MOVE_ORIENTATION_RIGHT = 2
    const val LONG_PRESS_MOVE_ORIENTATION_UP = -3
    const val LONG_PRESS_MOVE_ORIENTATION_DOWN = -4
    const val SCHEMA_ZH_T9 = "t9" // 拼音九键
    const val SCHEMA_ZH_QWERTY = "rime_ice" // 拼音全键
    const val SCHEMA_EN = "melt_eng" // 英文九键
    const val SCHEMA_ZH_HANDWRITING = "handwriting" // 手写输入
    const val SCHEMA_ZH_DOUBLE_FLYPY = "double_pinyin_flypy" // 明月拼音+小鹤双拼
    const val SCHEMA_ZH_DOUBLE_LX17 = "double_pinyin_ls17" // 乱序17
    const val SCHEMA_CLEAR = "clear" // 清除引擎，部分键盘不需要调用Rime引擎，比如：英文（非拼写模式）、手写
    const val CURRENT_RIME_DICT_DATA_VERSIOM = 20240408
    const val githubRepo = "https://github.com/fcitx5-android/fcitx5-android"
    const val licenseSpdxId = "LGPL-2.1-or-later"
    const val licenseUrl = "https://www.gnu.org/licenses/old-licenses/lgpl-2.1"
    const val privacyPolicyUrl = "https://fcitx5-android.github.io/privacy/"
}
