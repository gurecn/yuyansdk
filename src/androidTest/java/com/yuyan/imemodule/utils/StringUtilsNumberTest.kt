package com.yuyan.imemodule.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.regex.Pattern

/**
 * StringUtils 手写数字/字母判定的语义等价性验证：逐条对比改造前的正则。
 */
class StringUtilsNumberTest {
    private val numberPattern = Pattern.compile("^[+-]?\\d*(\\.\\d*)?$")
    private val letterPattern = Pattern.compile("[a-zA-Z]*")

    private fun originalIsNumber(str: String?): Boolean {
        if (str.isNullOrBlank()) return false
        return numberPattern.matcher(str).matches()
    }

    private fun originalIsLetter(str: String?): Boolean =
        letterPattern.matcher(str.toString()).matches()

    @Test
    fun isNumberMatchesOriginalPattern() {
        listOf(
            "", " ", "1", "12345", "+1", "-1", "1.", ".5", ".", "+", "-", "+.",
            "1.2.3", "1+1", "1e3", "１", "1.5", "--1", "1 2", "abc", "1-2", "..", "0", "-0.0",
        ).forEach {
            assertEquals("isNumber(\"$it\")", originalIsNumber(it), StringUtils.isNumber(it))
        }
    }

    @Test
    fun isLetterMatchesOriginalPattern() {
        // 空串与 null 有意与原实现分歧（见下个用例），这里只比对非空入参
        listOf("abc", "A", "abC", "a b", "1", "ab1", "中文", "sin").forEach {
            assertEquals("isLetter(\"$it\")", originalIsLetter(it), StringUtils.isLetter(it))
        }
    }

    @Test
    fun isLetterRejectsEmptyAndNull() {
        // 原实现 "[a-zA-Z]*" 允许零次匹配、null 又被 str.toString() 拼成 "null"，两者都误判为字母
        assertEquals(true, originalIsLetter(""))
        assertEquals(false, StringUtils.isLetter(""))
        assertEquals(true, originalIsLetter(null))
        assertEquals(false, StringUtils.isLetter(null))
        assertEquals(false, StringUtils.isLetter(" "))
    }
}
