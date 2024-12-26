package com.yuyan.inputmethod.util

import com.yuyan.imemodule.database.DataBaseKT
import com.yuyan.imemodule.utils.StringUtils
import com.yuyan.imemodule.utils.TimeUtils
import com.yuyan.imemodule.utils.expression.ExpressionBuilder

object CustomEngine {
    fun parseExpressionAtEnd(input: String): String? {
        val expressionEndPattern = "((φ|π|pi|sin|cos|tan|cot|asin|acos|atan|sinh|cosh|tanh|abs|log|log1p|ceil|floor|sqrt|cbrt|pow|exp|expm|signum|csc|sec|csch|sech|coth|toradian|todegree|\\(|\\)|^|%|\\+|-|\\*|/|\\.|e|E|\\d)+)$".toRegex()
        return expressionEndPattern.find(input.removeSuffix("="))?.value
    }

    fun expressionCalculator(input: String, expression: String):Array<String>{
        val results = mutableListOf<String>()
        if(!StringUtils.isNumber(expression) && !StringUtils.isLetter(expression)){
            try {
                val evaluate = ExpressionBuilder(expression).build().evaluate()
                val  resultFloat = evaluate.toFloat()
                val  resultInt = evaluate.toInt()
                if(evaluate.compareTo(resultInt) == 0){
                    val resultIntStr = resultInt.toString()
                    results.add(resultIntStr)
                    if(!input.endsWith("=")) results.add("=".plus(resultIntStr))
                } else {
                    val resultFloatStr = resultFloat.toString()
                    results.add(resultFloatStr)
                    if(!input.endsWith("=")) results.add("=".plus(resultFloatStr))
                    if(resultFloat < 1 && resultFloat > 0){
                        results.add((evaluate * 100).toInt().toString() + "%")
                    }
                }
            } catch (_:Exception){ }
            results.addAll(arrayOf("=", "+", "-", "*", "/", "%", ".", ",", "'", "(", ")"))
        }
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

    fun processPhrase(text: String):List<String> {
        val phrases = mutableListOf<String>()
        phrases.addAll(DataBaseKT.instance.phraseDao().query(text).map { it.content })
        val suffixesDate = setOf("rq", "riqi", "7474", "77")
        if(suffixesDate.any { it == text }){
            phrases.addAll(TimeUtils.getData())
        }
        val suffixesTime = setOf("sj", "shijian", "75", "7445426")
        if(suffixesTime.any { it == text }){
            phrases.addAll(TimeUtils.getTime())
        }
        return phrases
    }
}