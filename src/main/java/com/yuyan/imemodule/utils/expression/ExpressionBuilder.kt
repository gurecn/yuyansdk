package com.yuyan.imemodule.utils.expression

import com.yuyan.imemodule.utils.expression.function.Function
import com.yuyan.imemodule.utils.expression.function.Functions
import com.yuyan.imemodule.utils.expression.operator.Operator
import com.yuyan.imemodule.utils.expression.shuntingyard.ShuntingYard

/**
 * Factory class for [Expression] instances. This class is the main API entrypoint. Users should create new
 * [Expression] instances using this factory class.
 */
class ExpressionBuilder(expression: String?) {
    private val expression: String
    private val userFunctions: Map<String, Function>
    private val userOperators: Map<String, Operator>
    private val variableNames: MutableSet<String>
    private val implicitMultiplication = true

    init {
        require(!(expression == null || expression.trim { it <= ' ' }.isEmpty())) { "Expression can not be empty" }
        this.expression = expression
        userOperators = HashMap(4)
        userFunctions = HashMap(4)
        variableNames = HashSet(4)
    }

    /**
     * Build the [Expression] instance using the custom operators and functions set.
     *
     * @return an [Expression] instance which can be used to evaluate the result of the expression
     */
    fun build(): Expression {
        require(expression.isNotEmpty()) { "The expression can not be empty" }
        /* set the constants' varibale names */variableNames.add("pi")
        variableNames.add("π")
        variableNames.add("e")
        variableNames.add("φ")

        /* Check if there are duplicate vars/functions */for (`var` in variableNames) {
            require(!(Functions.getBuiltinFunction(`var`) != null || userFunctions.containsKey(`var`))) { "A variable can not have the same name as a function [$`var`]" }
        }
        return Expression(
            ShuntingYard.convertToRPN(
                expression, userFunctions, userOperators,
                variableNames, implicitMultiplication
            ), userFunctions.keys
        )
    }
}
