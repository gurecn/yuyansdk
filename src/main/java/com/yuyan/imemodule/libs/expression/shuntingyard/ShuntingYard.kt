package com.yuyan.imemodule.libs.expression.shuntingyard

import com.yuyan.imemodule.libs.expression.function.Function
import com.yuyan.imemodule.libs.expression.operator.Operator
import com.yuyan.imemodule.libs.expression.tokenizer.OperatorToken
import com.yuyan.imemodule.libs.expression.tokenizer.Token
import com.yuyan.imemodule.libs.expression.tokenizer.Tokenizer
import java.util.Stack

object ShuntingYard {

    fun convertToRPN(expression: String, userFunctions: Map<String, Function>, userOperators: Map<String, Operator>, variableNames: Set<String>, implicitMultiplication: Boolean): Array<Token> {
        val stack = Stack<Token>()
        val output: MutableList<Token> = ArrayList()
        val tokenizer = Tokenizer(
            expression,
            userFunctions,
            userOperators,
            variableNames,
            implicitMultiplication
        )
        while (tokenizer.hasNext()) {
            val token = tokenizer.nextToken()
            when (token.type) {
                Token.TOKEN_NUMBER, Token.TOKEN_VARIABLE -> output.add(token)
                Token.TOKEN_FUNCTION -> stack.add(token)
                Token.TOKEN_SEPARATOR -> {
                    while (!stack.empty() && stack.peek().type != Token.TOKEN_PARENTHESES_OPEN) {
                        output.add(stack.pop())
                    }
                    require(!(stack.empty() || stack.peek().type != Token.TOKEN_PARENTHESES_OPEN)) { "Misplaced function separator ',' or mismatched parentheses" }
                }

                Token.TOKEN_OPERATOR -> {
                    while (!stack.empty() && stack.peek().type == Token.TOKEN_OPERATOR) {
                        val o1 = token as OperatorToken
                        val o2 = stack.peek() as OperatorToken
                        if (o1.operator.numOperands == 1 && o2.operator.numOperands == 2) {
                            break
                        } else if (o1.operator.isLeftAssociative && o1.operator.precedence <= o2.operator.precedence || o1.operator.precedence < o2.operator.precedence) {
                            output.add(stack.pop())
                        } else {
                            break
                        }
                    }
                    stack.push(token)
                }

                Token.TOKEN_PARENTHESES_OPEN -> stack.push(token)
                Token.TOKEN_PARENTHESES_CLOSE -> {
                    while (stack.peek().type != Token.TOKEN_PARENTHESES_OPEN) {
                        output.add(stack.pop())
                    }
                    stack.pop()
                    if (!stack.isEmpty() && stack.peek().type == Token.TOKEN_FUNCTION) {
                        output.add(stack.pop())
                    }
                }
                else -> throw IllegalArgumentException("Unknown Token type encountered. This should not happen")
            }
        }
        while (!stack.empty()) {
            val t = stack.pop()
            require(!(t.type == Token.TOKEN_PARENTHESES_CLOSE || t.type == Token.TOKEN_PARENTHESES_OPEN)) { "Mismatched parentheses detected. Please check the expression" }
            output.add(t)
        }
        return output.toTypedArray<Token>()
    }
}
