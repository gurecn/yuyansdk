package com.yuyan.inputmethod.util

object T9PinYinUtils {

    fun getT9Composition(composition: String, comment: String): String {
        if (comment.isEmpty()) return composition
        val asciiBuilder = StringBuilder()
        val nonAsciiBuilder = StringBuilder()
        for (ch in composition) if (ch.code <= 0xFF) asciiBuilder.append(ch) else nonAsciiBuilder.append(ch)
        val compositionList = asciiBuilder.split("'")
        val commentParts = comment.split("'").filter { it.isNotEmpty() }
        return if(commentParts.size == compositionList.size) {
            buildString {
                append(nonAsciiBuilder)
                commentParts.zip(compositionList).forEach { (pinyin, compo) ->
                    append(pinyin.take(compo.length))
                    append("'")
                }
            }
        } else composition.lowercase()
    }
}
