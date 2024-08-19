package com.yuyan.imemodule.constant

import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.data.flower.FlowerTypefaceMode
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
    const val EMOJI_TYPR_FACE_DATA = 4 //emoji表情：4。   序号需和SymbolsManager中mSymbolsEmoji Key对应
    const val EMOJI_TYPR_SMILE_TEXT = 5 //颜文字：5。   序号需和SymbolsManager中mSymbolsEmoji Key对应
    //按键长按选择
    const val SCHEMA_ZH_T9 = "t9" // 拼音九键
    const val SCHEMA_ZH_QWERTY = "rime_ice" // 拼音全键
    const val SCHEMA_EN = "melt_eng" // 英文九键
    const val SCHEMA_ZH_HANDWRITING = "handwriting" // 手写输入
    const val SCHEMA_ZH_DOUBLE_FLYPY = "double_pinyin_flypy" // 明月拼音+小鹤双拼
    const val SCHEMA_ZH_DOUBLE_LX17 = "double_pinyin_ls17" // 乱序17
    const val CURRENT_RIME_DICT_DATA_VERSIOM = 20240819
    const val YuyanIMERepo = "https://github.com/gurecn/YuyanIme"
    const val YuyanSDKRepo = "https://github.com/gurecn/YuyanIme"
    const val licenseSpdxId = "LGPL-2.1-or-later"
    const val licenseUrl = "https://www.gnu.org/licenses/old-licenses/lgpl-2.1"
}
