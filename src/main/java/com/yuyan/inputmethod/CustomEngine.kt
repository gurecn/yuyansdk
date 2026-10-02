package com.yuyan.inputmethod

import com.yuyan.imemodule.database.DataBaseKT
import com.yuyan.imemodule.utils.StringUtils
import com.yuyan.imemodule.utils.TimeUtils
import com.yuyan.imemodule.libs.expression.ExpressionBuilder
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance
import kotlin.math.roundToInt

object CustomEngine {
    fun parseExpressionAtEnd(input: String): String? {
        val expressionEndPattern = "((φ|π|pi|sin|cos|tan|cot|asin|acos|atan|sinh|cosh|tanh|abs|log|log1p|ceil|floor|sqrt|cbrt|pow|exp|expm|signum|csc|sec|csch|sech|coth|toradian|todegree|\\(|\\)|^|%|\\+|-|\\*|/|\\.|e|E|\\d)+)$".toRegex()
        return expressionEndPattern.find(input.removeSuffix("="))?.value
    }

    fun expressionCalculator(input: String, expression: String?):Array<String>{
        val results = mutableListOf<String>()
        if(!expression.isNullOrBlank() && expression.length < 500 && !StringUtils.isNumber(expression) && !StringUtils.isLetter(expression)){
            try {
                val evaluate = ExpressionBuilder(expression).build().evaluate()
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
            } catch (_:Exception){ }
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

    // 内置常见邮箱域名
    val EMAIL_DOMAINS = arrayOf(
        "qq.com", "163.com", "126.com", "gmail.com", "outlook.com", "foxmail.com",
        "sina.com", "sohu.com", "139.com", "189.cn", "aliyun.com", "hotmail.com"
    )

    // 邮箱后缀匹配正则：本地部分（任意字符，可为空，支持中文）@域名部分（域名可含字母数字.-）
    private val emailEndPattern = "(.*)@([A-Za-z0-9.-]*)\\z".toRegex()

    /**
     * 解析文本末尾的邮箱输入
     * @return Pair(localPart, partialDomain) 或 null（无@或域名部分超长）
     */
    fun parseEmailAtEnd(text: String): Pair<String, String>? {
        val match = emailEndPattern.find(text) ?: return null
        val partial = match.groupValues[2]
        if (partial.length >= EMAIL_DOMAINS.maxOf { it.length }) return null // 已超最长域名，无需联想
        return Pair(match.groupValues[1], partial)
    }

    /**
     * 获取邮箱域名联想列表：严格长于已输入部分的前缀匹配（忽略大小写）
     */
    fun emailSuggestions(text: String): List<String> {
        val (_, partial) = parseEmailAtEnd(text) ?: return emptyList()
        return EMAIL_DOMAINS.filter { it.length > partial.length && it.startsWith(partial, ignoreCase = true) }
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