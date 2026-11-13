package com.yuyan.inputmethod

import com.yuyan.imemodule.database.DataBaseKT
import com.yuyan.imemodule.utils.StringUtils
import com.yuyan.imemodule.utils.TimeUtils
import com.yuyan.imemodule.libs.expression.ExpressionBuilder
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance
import kotlin.math.roundToInt

object CustomEngine {
    /** 首次读取的尾部窗口长度，覆盖绝大多数短算式 */
    const val INITIAL_WINDOW = 64
    /** 扩窗上限，与镜像缓存窗口等长 */
    const val MAX_WINDOW = 4096
    /** 从光标前文本尾部解析出的表达式及其求值结果 */
    data class Expression(val text: String, val value: Double)

    /** 尾部扫描结果 */
    sealed interface Scan {
        class Found(val expression: Expression) : Scan
        object None : Scan
        object NeedMore : Scan
    }

    private val expressionNames = listOf(
        "φ", "π", "pi", "sin", "cos", "tan", "cot", "asin", "acos", "atan",
        "sinh", "cosh", "tanh", "abs", "log", "log1p", "ceil", "floor", "sqrt",
        "cbrt", "pow", "exp", "expm", "signum", "csc", "sec", "csch", "sech",
        "coth", "toradian", "todegree", "e", "E",
    )

    /** 表达式可能出现的 ASCII 字符查表：数字、运算符、括号，以及函数名用到的字母 */
    private val expressionAsciiChars = BooleanArray(128).apply {
        "0123456789.+-*/%()".forEach { this[it.code] = true }
        expressionNames.forEach { name -> name.forEach { if (it.code < 128) this[it.code] = true } }
    }

    /** 表达式里的非 ASCII 字符（φ、π），查表覆盖不到，单独保存 */
    private val expressionWideChars: Set<Char> =
        expressionNames.flatMap { it.toList() }.filter { it.code >= 128 }.toSet()

    private fun isExpressionChar(c: Char): Boolean = when {
        c.code < 128 -> expressionAsciiChars[c.code]
        c.isDigit() -> true // 与旧正则的 \d 一致：ICU 正则同样接受全角等 Unicode 数字
        else -> expressionWideChars.contains(c)
    }

    /**
     * 从 [window] 个字符的尾部文本里扫描可计算的表达式。
     */
    fun scanExpressionAtEnd(text: String, window: Int, caret: Int = -1): Scan {
        val content = text.removeSuffix("=")
        val tail = tailExpression(content) ?: return Scan.None
        if (StringUtils.isNumber(tail)) return Scan.None
        if (tail.length == content.length) {
            if (text.length >= window) return Scan.NeedMore
            if (caret >= 0 && text.length < caret) return Scan.None
        }
        return calculate(tail)?.let { Scan.Found(it) } ?: Scan.None
    }

    fun calculateExpressionAtEnd(input: String): Expression? =
        (scanExpressionAtEnd(input, Int.MAX_VALUE) as? Scan.Found)?.expression

    fun nextWindow(window: Int): Int? =
        if (window >= MAX_WINDOW) null else (window * 4).coerceAtMost(MAX_WINDOW)

    fun isExpressionTail(last: Char): Boolean = last == '=' || isExpressionChar(last)

    fun isExpressionTail(text: String): Boolean {
        val last = text.lastOrNull() ?: return false
        return last == '=' || isExpressionChar(last)
    }

    private fun calculate(text: String): Expression? {
        if (StringUtils.isNumber(text) || StringUtils.isLetter(text)) return null
        val value = evaluate(text) ?: return null
        return Expression(text, value)
    }

    private fun tailExpression(input: String): String? {
        var start = input.length
        while (start > 0 && isExpressionChar(input[start - 1])) start--
        return if (start == input.length) null else input.substring(start)
    }

    private fun evaluate(expression: String): Double? = try {
        ExpressionBuilder(expression).build().evaluate().takeIf { it.isFinite() }
    } catch (_: Exception) {
        null
    }

    fun expressionCalculator(input: String, expression: Expression?):Array<String>{
        val results = mutableListOf<String>()
        if(expression != null){
            val evaluate = expression.value
            val  resultFloat = evaluate.toFloat()
            val  resultInt = evaluate.roundToInt()
            if(evaluate.compareTo(resultInt) == 0){
                if(!input.endsWith("=")) results.add("=${resultInt}")
                else results.add(resultInt.toString())
            } else {
                val resultPercentage = "${(evaluate * 100).roundToInt()}%"
                if(!input.endsWith("=")) {
                    results.add("=".plus(resultFloat))
                    results.add("≈".plus(resultInt))
                    if(evaluate < 10 && evaluate > 0.005) results.add("=${resultPercentage}")
                } else {
                    results.add(resultFloat.toString())
                    if(evaluate < 10 && evaluate > 0.005) results.add(resultPercentage)
                }
            }
        }
        results.addAll(arrayOf("=", "+", "-", "*", "/", "%", ".", ",", "'", "(", ")"))
        return results.toTypedArray()
    }

    fun predictAssociationWordsChinese(text: String):MutableList<String> {
        val associations = mutableListOf("，", "。")
        val suffixesDays = setOf("大前天", "前天", "昨天", "今天", "明天", "大后天", "后天")
        val suffixesExclamation = setOf("啊", "呀", "呐", "啦", "噢", "哇", "吧", "呗", "了")
        val suffixesQuestion = setOf("吗", "啊", "呢", "吧", "谁", "何", "什么", "哪", "几", "多少", "怎", "难道", "岂", "不")
        val days = suffixesDays.firstOrNull{ text.endsWith(it) }
        if(days != null)associations.addAll(0, TimeUtils.getData(days))
        else if(suffixesExclamation.any{ text.endsWith(it) }){ associations.add(0, "！") }
        else if(suffixesQuestion.any{ text.contains(it) }){ associations.add(0, "？") }
        return associations
    }

    fun processPhrase(text: String):MutableList<String> {
        val phrases = mutableListOf<String>()
        val chinesePredictionDate = getInstance().input.chinesePredictionDate.getValue()
        if(chinesePredictionDate) {
            phrases.addAll(DataBaseKT.instance.phraseDao().query(text).map { it.content })
            val suffixesDate = setOf("rq", "riqi", "PGPG", "PP")
            if (suffixesDate.any { it == text }) {
                phrases.addAll(TimeUtils.getData())
            }
            val suffixesTime = setOf("sj", "shijian", "PJ", "PGGJGAM")
            if (suffixesTime.any { it == text }) {
                phrases.addAll(TimeUtils.getTime())
            }
        }
        return phrases
    }
}