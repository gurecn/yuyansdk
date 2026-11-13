package com.yuyan.inputmethod

import com.yuyan.imemodule.service.EditorTextCache
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 公式解析能力的验证：尾部扫描、只求值一次、长度上限与末字符预筛。
 */
class CustomEngineExpressionTest {

    private fun found(scan: CustomEngine.Scan): CustomEngine.Expression {
        assertTrue("期望拿到表达式，实际 $scan", scan is CustomEngine.Scan.Found)
        return (scan as CustomEngine.Scan.Found).expression
    }

    /** 构造长度为 [length] 的合法加法表达式，500/501/1000 这类长度都靠它凑齐 */
    private fun sumExpression(length: Int): String {
        val builder = StringBuilder("1")
        while (builder.length + 2 <= length) builder.append("+1")
        if (builder.length < length) builder.append('1')
        return builder.toString()
    }

    @Test
    fun takesTailExpressionOnly() {
        assertEquals("1+2", CustomEngine.calculateExpressionAtEnd("今天天气是1+2")?.text)
        assertEquals("1+2", CustomEngine.calculateExpressionAtEnd("value = 1+2")?.text)
        assertEquals("sqrt(9)", CustomEngine.calculateExpressionAtEnd("结果是sqrt(9)")?.text)
        // 末尾不是表达式字符时整体不联想，避免把前文的字母吞进表达式
        assertNull(CustomEngine.calculateExpressionAtEnd("1+2个"))
    }

    @Test
    fun returnsEvaluatedValue() {
        val expression = CustomEngine.calculateExpressionAtEnd("1+2*3")
        assertNotNull(expression)
        assertEquals("1+2*3", expression!!.text)
        assertEquals(7.0, expression.value, 0.0001)
    }

    @Test
    fun ignoresExpressionFollowedByEquals() {
        val expression = CustomEngine.calculateExpressionAtEnd("1+2=")
        assertNotNull(expression)
        assertEquals("1+2", expression!!.text)
        assertEquals(3.0, expression.value, 0.0001)
        // 已经打出等号时只给结果本身，不再补一个等号
        val candidates = CustomEngine.expressionCalculator("1+2=", expression)
        assertTrue(candidates.contains("3"))
        assertFalse(candidates.contains("=3"))
    }

    @Test
    fun skipsPlainNumberAndPlainLetters() {
        assertNull(CustomEngine.calculateExpressionAtEnd("12345"))
        assertNull(CustomEngine.calculateExpressionAtEnd("sin"))
    }

    @Test
    fun skipsIncompleteExpression() {
        assertNull(CustomEngine.calculateExpressionAtEnd("6-"))
        assertNull(CustomEngine.calculateExpressionAtEnd("sin("))
        assertNull(CustomEngine.calculateExpressionAtEnd(""))
    }

    @Test
    fun hasNoExpressionLengthLimit() {
        // 曾经的 500 字符上限已取消：只要没顶到读取上限就能算
        listOf(500, 501, 1000).forEach { length ->
            val expression = CustomEngine.calculateExpressionAtEnd(sumExpression(length))
            assertNotNull("长度 $length 的表达式应可计算", expression)
            assertTrue("表达式不应被截断", expression!!.text.length == length)
        }
    }

    @Test
    fun expandsWindowWhenExpressionTouchesLeftEdge() {
        // 窗口被读满且表达式顶到左边界：无法确定是否被截断，要求扩窗
        assertEquals(CustomEngine.Scan.NeedMore, CustomEngine.scanExpressionAtEnd("1+2+3", 5))
        // 宿主给不满整窗说明已读到文本开头，可以直接算
        assertEquals("1+2+3", found(CustomEngine.scanExpressionAtEnd("1+2+3", 64)).text)
        // 表达式前有分隔字符时左边界不在表达式上，不需要扩窗
        assertEquals("1+2", found(CustomEngine.scanExpressionAtEnd("今天天气1+2", 64)).text)
        // 光标处的字符数等于返回长度（已到文本开头）才算完整
        assertEquals("1+2+3", found(CustomEngine.scanExpressionAtEnd("1+2+3", 64, 5)).text)
        // 返回字符数比光标位置少：宿主自己也截断了，不能拿半截算式算一个错值
        assertEquals(CustomEngine.Scan.None, CustomEngine.scanExpressionAtEnd("1+2+3", 64, 7))
        // 纯数字串扩窗也算不出结果，不应触发扩窗
        assertEquals(CustomEngine.Scan.None, CustomEngine.scanExpressionAtEnd("123456789", 9))
        assertEquals(CustomEngine.Scan.None, CustomEngine.scanExpressionAtEnd("", 64))
        assertEquals(CustomEngine.Scan.None, CustomEngine.scanExpressionAtEnd("6-", 64))
    }

    @Test
    fun enlargesWindowUpToReadBudget() {
        assertEquals(256, CustomEngine.nextWindow(CustomEngine.INITIAL_WINDOW))
        assertEquals(1024, CustomEngine.nextWindow(256))
        assertEquals(CustomEngine.MAX_WINDOW, CustomEngine.nextWindow(1024))
        assertNull(CustomEngine.nextWindow(CustomEngine.MAX_WINDOW))
        // 扩窗只能靠镜像缓存兑现，缓存窗口必须不小于读取上限
        assertTrue(EditorTextCache.MAX_CACHE_LENGTH >= CustomEngine.MAX_WINDOW)
    }

    @Test
    fun calculatesFunctions() {
        val sqrt = CustomEngine.calculateExpressionAtEnd("sqrt(9)")
        assertNotNull(sqrt)
        assertEquals(3.0, sqrt!!.value, 0.0001)

        val mixed = CustomEngine.calculateExpressionAtEnd("1+sin(0)+1")
        assertNotNull(mixed)
        assertEquals(2.0, mixed!!.value, 0.0001)
    }

    @Test
    fun prefilterChecksLastCharOnly() {
        assertTrue(CustomEngine.isExpressionTail("1"))
        assertTrue(CustomEngine.isExpressionTail("("))
        assertTrue(CustomEngine.isExpressionTail("t")) // 函数名用到的字母
        assertTrue(CustomEngine.isExpressionTail("=")) // 已打出等号仍要给出结果
        assertTrue(CustomEngine.isExpressionTail("１")) // 全角数字与旧正则的 \d 行为保持一致
        assertFalse(CustomEngine.isExpressionTail("中"))
        assertFalse(CustomEngine.isExpressionTail("k")) // 函数名用不到的字母
        assertFalse(CustomEngine.isExpressionTail(" "))
        assertFalse(CustomEngine.isExpressionTail(""))
    }

    @Test
    fun keepsDecimalCandidateText() {
        val expression = CustomEngine.calculateExpressionAtEnd("1/4")
        assertNotNull(expression)
        val candidates = CustomEngine.expressionCalculator("1/4", expression)
        assertTrue(candidates.contains("=0.25"))
        assertTrue(candidates.contains("=25%"))
    }
}
