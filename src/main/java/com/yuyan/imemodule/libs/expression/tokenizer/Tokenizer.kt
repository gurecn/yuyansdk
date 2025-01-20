package com.yuyan.imemodule.libs.expression.tokenizer

import com.yuyan.imemodule.libs.expression.function.Function
import com.yuyan.imemodule.libs.expression.function.Functions.getBuiltinFunction
import com.yuyan.imemodule.libs.expression.operator.Operator
import com.yuyan.imemodule.libs.expression.operator.Operator.Companion.isAllowedOperatorChar
import com.yuyan.imemodule.libs.expression.operator.Operators

class Tokenizer(expression: String, private val userFunctions: Map<String, Function>?, private val userOperators: Map<String, Operator>?, private val variableNames: Set<String>?, private val implicitMultiplication: Boolean) {
    private val expression: CharArray
    private val expressionLength: Int
    private var pos = 0
    private var lastToken: Token? = null

    init {
        this.expression = expression.trim { it <= ' ' }.toCharArray()
        expressionLength = this.expression.size
    }

    operator fun hasNext(): Boolean {
        return expression.size > pos
    }

    fun nextToken(): Token {
        var ch = expression[pos]
        while (Character.isWhitespace(ch)) {
            ch = expression[++pos]
        }
        if (Character.isDigit(ch) || ch == '.') {
            if (lastToken != null) {
                require(lastToken!!.type != Token.TOKEN_NUMBER) { "Unable to parse char '" + ch + "' (Code:" + ch.code + ") at [" + pos + "]" }
                if (implicitMultiplication && lastToken!!.type != Token.TOKEN_OPERATOR && lastToken!!.type != Token.TOKEN_PARENTHESES_OPEN && lastToken!!.type != Token.TOKEN_FUNCTION && lastToken!!.type != Token.TOKEN_SEPARATOR) {
                    lastToken = OperatorToken(Operators.getBuiltinOperator('*', 2))
                    return lastToken!!
                }
            }
            return parseNumberToken(ch)
        } else if (isArgumentSeparator(ch)) {
            return parseArgumentSeparatorToken()
        } else if (isOpenParentheses(ch)) {
            if (lastToken != null && implicitMultiplication && lastToken!!.type != Token.TOKEN_OPERATOR && lastToken!!.type != Token.TOKEN_PARENTHESES_OPEN && lastToken!!.type != Token.TOKEN_FUNCTION && lastToken!!.type != Token.TOKEN_SEPARATOR) {
                lastToken = OperatorToken(Operators.getBuiltinOperator('*', 2))
                return lastToken!!
            }
            return parseParentheses(true)
        } else if (isCloseParentheses(ch)) {
            return parseParentheses(false)
        } else if (isAllowedOperatorChar(ch)) {
            return parseOperatorToken(ch)
        } else if (isAlphabetic(ch.code) || ch == '_') {
            if (lastToken != null && implicitMultiplication && lastToken!!.type != Token.TOKEN_OPERATOR && lastToken!!.type != Token.TOKEN_PARENTHESES_OPEN && lastToken!!.type != Token.TOKEN_FUNCTION && lastToken!!.type != Token.TOKEN_SEPARATOR) {
                lastToken = OperatorToken(Operators.getBuiltinOperator('*', 2))
                return lastToken!!
            }
            return parseFunctionOrVariable()
        }
        throw IllegalArgumentException("Unable to parse char '" + ch + "' (Code:" + ch.code + ") at [" + pos + "]")
    }

    private fun parseArgumentSeparatorToken(): Token {
        pos++
        lastToken = ArgumentSeparatorToken()
        return lastToken!!
    }

    private fun isArgumentSeparator(ch: Char): Boolean {
        return ch == ','
    }

    private fun parseParentheses(open: Boolean): Token {
        lastToken = if (open) {
            OpenParenthesesToken()
        } else {
            CloseParenthesesToken()
        }
        pos++
        return lastToken!!
    }

    private fun isOpenParentheses(ch: Char): Boolean {
        return ch == '(' || ch == '{' || ch == '['
    }

    private fun isCloseParentheses(ch: Char): Boolean {
        return ch == ')' || ch == '}' || ch == ']'
    }

    private fun parseFunctionOrVariable(): Token {
        val offset = pos
        var testPos: Int
        var lastValidLen = 1
        var lastValidToken: Token? = null
        var len = 1
        if (isEndOfExpression(offset)) {
            pos++
        }
        testPos = offset + len - 1
        while (!isEndOfExpression(testPos) && isVariableOrFunctionCharacter(expression[testPos].code)) {
            val name = String(expression, offset, len)
            if (variableNames != null && variableNames.contains(name)) {
                lastValidLen = len
                lastValidToken = VariableToken(name)
            } else {
                val f = getFunction(name)
                if (f != null) {
                    lastValidLen = len
                    lastValidToken = FunctionToken(f)
                }
            }
            len++
            testPos = offset + len - 1
        }
        if (lastValidToken == null) {
            throw UnknownFunctionOrVariableException(String(expression), pos, len)
        }
        pos += lastValidLen
        lastToken = lastValidToken
        return lastToken!!
    }

    private fun getFunction(name: String): Function? {
        var f: Function? = null
        if (userFunctions != null) {
            f = userFunctions[name]
        }
        if (f == null) {
            f = getBuiltinFunction(name)
        }
        return f
    }

    private fun parseOperatorToken(firstChar: Char): Token {
        val offset = pos
        var len = 1
        val symbol = StringBuilder()
        var lastValid: Operator? = null
        symbol.append(firstChar)
        while (!isEndOfExpression(offset + len) && isAllowedOperatorChar(expression[offset + len])) {
            symbol.append(expression[offset + len++])
        }
        while (symbol.isNotEmpty()) {
            val op = getOperator(symbol.toString())
            if (op == null) {
                symbol.setLength(symbol.length - 1)
            } else {
                lastValid = op
                break
            }
        }
        pos += symbol.length
        lastToken = OperatorToken(lastValid)
        return lastToken!!
    }

    private fun getOperator(symbol: String): Operator? {
        var op: Operator? = null
        if (userOperators != null) {
            op = userOperators[symbol]
        }
        if (op == null && symbol.length == 1) {
            var argc = 2
            if (lastToken == null) {
                argc = 1
            } else {
                val lastTokenType = lastToken!!.type.toInt()
                if (lastTokenType == Token.TOKEN_PARENTHESES_OPEN.toInt() || lastTokenType == Token.TOKEN_SEPARATOR.toInt()) {
                    argc = 1
                } else if (lastTokenType == Token.TOKEN_OPERATOR.toInt()) {
                    val lastOp = (lastToken as OperatorToken).operator
                    if (lastOp.numOperands == 2 || lastOp.numOperands == 1 && !lastOp.isLeftAssociative) {
                        argc = 1
                    }
                }
            }
            op = Operators.getBuiltinOperator(symbol[0], argc)
        }
        return op
    }

    private fun parseNumberToken(firstChar: Char): Token {
        val offset = pos
        var len = 1
        pos++
        if (isEndOfExpression(offset + len)) {
            lastToken = NumberToken(firstChar.toString().toDouble())
            return lastToken!!
        }
        while (!isEndOfExpression(offset + len) &&
            isNumeric(
                expression[offset + len], expression[offset + len - 1] == 'e' ||
                        expression[offset + len - 1] == 'E'
            )
        ) {
            len++
            pos++
        }
        if (expression[offset + len - 1] == 'e' || expression[offset + len - 1] == 'E') {
            len--
            pos--
        }
        lastToken = NumberToken(expression, offset, len)
        return lastToken!!
    }

    private fun isEndOfExpression(offset: Int): Boolean {
        return expressionLength <= offset
    }

    companion object {
        private fun isNumeric(ch: Char, lastCharE: Boolean): Boolean {
            return Character.isDigit(ch) || ch == '.' || ch == 'e' || ch == 'E' || lastCharE && (ch == '-' || ch == '+')
        }

        fun isAlphabetic(codePoint: Int): Boolean {
            return Character.isLetter(codePoint)
        }

        fun isVariableOrFunctionCharacter(codePoint: Int): Boolean {
            return isAlphabetic(codePoint) || Character.isDigit(codePoint) || codePoint == '_'.code || codePoint == '.'.code
        }
    }
}
