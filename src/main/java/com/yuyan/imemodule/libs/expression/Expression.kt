package com.yuyan.imemodule.libs.expression

import com.yuyan.imemodule.libs.expression.tokenizer.FunctionToken
import com.yuyan.imemodule.libs.expression.tokenizer.NumberToken
import com.yuyan.imemodule.libs.expression.tokenizer.OperatorToken
import com.yuyan.imemodule.libs.expression.tokenizer.Token
import com.yuyan.imemodule.libs.expression.tokenizer.VariableToken

class Expression internal constructor(private val tokens: Array<Token>) {
    private val variables: Map<String, Double> = createDefaultVariables()
    fun evaluate(): Double {
        val output = ArrayStack()
        for (t in tokens) {
            if (t.type == Token.TOKEN_NUMBER) {
                output.push((t as NumberToken).value)
            } else if (t.type == Token.TOKEN_VARIABLE) {
                val name = (t as VariableToken).name
                val value = variables[name] ?: throw IllegalArgumentException("No value has been set for the setVariable '$name'.")
                output.push(value)
            } else if (t.type == Token.TOKEN_OPERATOR) {
                val op = t as OperatorToken
                require(output.size() >= op.operator.numOperands) { "Invalid number of operands available for '" + op.operator.symbol + "' operator" }
                if (op.operator.numOperands == 2) {
                    val rightArg = output.pop()
                    val leftArg = output.pop()
                    output.push(op.operator.apply(leftArg, rightArg))
                } else if (op.operator.numOperands == 1) {
                    val arg = output.pop()
                    output.push(op.operator.apply(arg))
                }
            } else if (t.type == Token.TOKEN_FUNCTION) {
                val func = t as FunctionToken
                val numArguments = func.function.numArguments
                if (output.size() < numArguments) {
                    throw IllegalArgumentException("Invalid number of arguments available for '" + func.function.name + "' function")
                }
                val args = DoubleArray(numArguments)
                for (j in numArguments - 1 downTo 0) {
                    args[j] = output.pop()
                }
                output.push(func.function.apply(*args))
            }
        }
        if (output.size() > 1) {
            throw IllegalArgumentException("Invalid number of items on the output queue. Might be caused by an invalid number of arguments for a function.")
        }
        return output.pop()
    }

    companion object {
        private fun createDefaultVariables(): Map<String, Double> {
            val vars: MutableMap<String, Double> = HashMap(4)
            vars["pi"] = Math.PI
            vars["π"] = Math.PI
            vars["φ"] = 1.61803398874
            vars["e"] = Math.E
            return vars
        }
    }
}
