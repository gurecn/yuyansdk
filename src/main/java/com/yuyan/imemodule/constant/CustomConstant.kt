package com.yuyan.imemodule.constant

import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.prefs.AppPrefs

/**
 * 用户自定义常量类
 * Created by Gaolei on 2016/11/25.
 */
object CustomConstant {
    var RIME_DICT_PATH = ImeSdkApplication.context.getExternalFilesDir("rime").toString()
    const val EMOJI_TYPR_FACE_DATA = 4 //emoji表情：4。   序号需和SymbolsManager中mSymbolsEmoji Key对应
    const val EMOJI_TYPR_SMILE_TEXT = 5 //颜文字：5。   序号需和SymbolsManager中mSymbolsEmoji Key对应
    //按键长按选择
    const val SCHEMA_ZH_T9 = "t9_pinyin" // 拼音九键
    const val SCHEMA_ZH_QWERTY = "pinyin" // 拼音全键
    const val SCHEMA_EN = "english"         // 英语方案
    const val SCHEMA_ZH_HANDWRITING = "handwriting" // 手写输入
    const val SCHEMA_ZH_DOUBLE_FLYPY = "double_pinyin_" // 小鹤双拼
    const val SCHEMA_ZH_DOUBLE_LX17 = "double_pinyin_ls17" // 乱序17双拼
    const val CURRENT_RIME_DICT_DATA_VERSIOM = 20241010
    const val YUYAN_IME_REPO = "https://github.com/gurecn/YuyanIme"
    const val YUYAN_SDK_REPO = "https://github.com/gurecn/yuyansdk"
    const val LICENSE_SPDX_ID = "LGPL-2.1-or-later"
    const val LICENSE_URL = "https://www.gnu.org/licenses/old-licenses/lgpl-2.1"
    const val FEEDBACK_TXC_REPO = "https://txc.qq.com/products/668191"
    var PREFIXS_PINYIN = AppPrefs.getInstance().internal.keyboardPrefixsPinyin.getValue().split(" ").toTypedArray()
    var PREFIXS_NUMBER = AppPrefs.getInstance().internal.keyboardPrefixsNumber.getValue().split(" ").toTypedArray()
}