package com.yuyan.inputmethod.util

object DoublePinYinUtils {
    val double_pinyin_abc = mapOf(
        'a' to "zh",
        'e' to "ch",
        'v' to "sh",
        ';' to "ing",
    )
    val double_pinyin_ziguang = mapOf(
        'u' to "zh",
        'a' to "ch",
        'i' to "sh",
        ';' to "ing",
    )
    val double_pinyin_ls17 = mapOf(
        'z' to "zh",
        'c' to "ch",
        's' to "sh",
    )
    val double_pinyin = mapOf(
        'v' to "zh",
        'i' to "ch",
        'u' to "sh",
        ';' to "ing",
    )

    val doublePinyinMap = mapOf(
        "double_pinyin_ziguang" to double_pinyin_abc,
        "double_pinyin_ziguang" to double_pinyin_ziguang,
        "double_pinyin_ls17" to double_pinyin_ls17,
    )

    fun getDoublePinYinComposition(rimeSchema: String, composition: String, comment: String): String {
        val compositionList = composition.filter { it.code <= 0xFF }.split("'".toRegex())
        return buildSpannedString {
            append(composition.filter { it.code > 0xFF })
            if(comment.isEmpty()){
                composition.forEach{  char -> append(doublePinyinMap.getOrElse(rimeSchema){double_pinyin}.getOrElse(char) {char.toString()}) }
            } else comment.split("'").zip(compositionList).forEach { (pinyin, compo) ->
                if(compo.isEmpty()) ""
                else if (compo.length == 1) append(doublePinyinMap.getOrElse(rimeSchema){double_pinyin}.getOrElse(compo[0]) { pinyin.first().toString() })
                else if (compo.length == 2) append(pinyin)
                else if (compo.all { it.isLowerCase() }) append(pinyin)
                else {
                    append(pinyin)
                    compo.drop(2).forEach{  char -> append(doublePinyinMap.getOrElse(rimeSchema){double_pinyin}.getOrElse(char) {char.toString()}) }
                }
                append("'")
            }
        }
    }
}