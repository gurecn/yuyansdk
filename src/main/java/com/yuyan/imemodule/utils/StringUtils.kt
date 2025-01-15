package com.yuyan.imemodule.utils

import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.imemodule.data.flower.FlowerTypefaceMode
import com.yuyan.imemodule.data.flower.simplified2HotPreset
import java.util.regex.Pattern

object StringUtils {
    /**
     * 判断字符串是不是字母
     */
    @JvmStatic
    fun isLetter(str: String?): Boolean {
        val pattern = Pattern.compile("[a-zA-Z]*")
        return pattern.matcher(str.toString()).matches()
    }

    /**
     * 判断字符串是不是英文
     */
    @JvmStatic
    fun isEnglishWord(str: String?): Boolean {
        val pattern = Pattern.compile("[a-zA-Z ]*")
        return pattern.matcher(str.toString()).matches()
    }

    @JvmStatic
    fun isNumber(str: String?): Boolean {
        if(str.isNullOrBlank())return false
        val pattern = Pattern.compile("^[+-]?\\d*(\\.\\d*)?\$")
        return pattern.matcher(str).matches()
    }

    fun isChineseEnd(input: String): Boolean {
        val chineseEndPattern = "[\\u4e00-\\u9fff]\$".toRegex()
        return chineseEndPattern.find(input) != null
    }

    // 标点全角半角关系
    // 1)半角字符(除空格外)是从33(0x21)开始到126(0x7E)结束;
    // 2)与半角字符对应的全角字符是从65281(unicode编码的0xFF01)开始到65374(unicode编码的0xFF3E)结束；
    // 3)其中半角的空格是32(0x20).对应的全角空格是12288(unicode编码的0x3000)；
    // 4)其中半角的句号是65377(0xFF61).对应的全角空格是12290(unicode编码的0xFF61)；
    // 5)半角和全角的关系很明显,除空格外的字符偏移量都是是65248(65281-33 = 65248)
    const val SBC_SPACE = 12288 // 全角空格 12288
        .toChar()
    const val DBC_SPACE = 32 //半角空格 32
        .toChar()
    const val SBC_PERIOD = 12290 // 全角句号
        .toChar()
    const val DBC_PERIOD = 65377 // 半角句号
        .toChar()
    const val ASCII_START = 30.toChar()
    const val ASCII_END = 126.toChar()
    const val UNICODE_START = 65278.toChar()
    const val UNICODE_END = 65374.toChar()
    const val DBC_SBC_STEP = 65248 // 全角半角转换间隔
        .toChar()

    // 全角转半角
    private fun sbc2dbc(src: Char): Char {
        return if (src == SBC_SPACE) {
            DBC_SPACE
        } else if (src == SBC_PERIOD) {
            DBC_PERIOD
        } else {
            if (src in UNICODE_START..UNICODE_END) {
                (src.code - DBC_SBC_STEP.code).toChar()
            } else src
        }
    }

    // 全角转半角
    @JvmStatic
    fun sbc2dbcCase(src: String?): String? {
        if (src == null) {
            return null
        }
        val c = src.toCharArray()
        for (i in c.indices) {
            c[i] = sbc2dbc(c[i])
        }
        return String(c)
    }

    /**
     * 判断是否是半角符号
     * 排除数字、大小写字母
     */
    fun isDBCSymbol(src: String?): Boolean {
        if (src == null || src.length > 1) {
            return false
        }
        val c = src[0]
        return c.code in 32..47 || c.code in 58..64 || c.code in 91..96 || c.code in 123..126
    }

    /**
     * 字符串转花漾字
     */
    fun converted2FlowerTypeface(src: String): String {
         return  when(CustomConstant.flowerTypeface) {
             FlowerTypefaceMode.Disabled -> {
                 src
             }
            FlowerTypefaceMode.Mars -> {  //焱暒妏
                 src.map { simplified2HotPreset[it]?:it }.joinToString("")
            }
            FlowerTypefaceMode.FlowerVine -> {   // ζั͡花ั͡藤ั͡字ั͡✾
                "ζั͡" + src.map { it }.joinToString("ั͡").plus("ั͡✾")
            }
            FlowerTypefaceMode.Messy -> {  //"҉҉҉凌҉҉҉乱҉҉҉字҉҉҉"
                "҉҉҉" + src.map { it }.joinToString("҉҉҉").plus("҉҉҉")
            }
            FlowerTypefaceMode.Germinate -> {  //ོ发ོ芽ོ字ོ
                "ོ" + src.map { it }.joinToString("ོ").plus("ོ")
            }
            FlowerTypefaceMode.Fog -> {  //"҈҈҈҈雾҈҈҈҈霾҈҈҈҈字҈҈҈҈"
                "҈҈҈҈" + src.map { it }.joinToString( "҈҈҈҈").plus("҈҈҈҈")
            }
            FlowerTypefaceMode.ProhibitAccess -> {  //禁⃠止⃠查⃠看
               src.map { it }.joinToString( "⃠").plus("⃠")
            }
            FlowerTypefaceMode.Grass -> {  // 长҉҉҈草҉҉҈字҉҉҈
                "҈҈҈" + src.map { it }.joinToString( "҈҈҈").plus("҈҈҈")
            }
            FlowerTypefaceMode.Wind -> {  // =͟͟起=͟风͟͞͞=͟了͟͞͞͞͞
             "=͟͟͞͞" + src.map { it }.joinToString( "=͟͟͞͞")
            }
        }
    }
}
