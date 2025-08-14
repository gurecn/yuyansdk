package com.yuyan.imemodule.libs.expression.tokenizer

/**
 * Abstract class for tokens used by exp4j to tokenize expressions
 */
abstract class Token internal constructor(@JvmField val type: Short) {
    companion object {
        const val TOKEN_NUMBER: Short = 1
        const val TOKEN_OPERATOR: Short = 2
        const val TOKEN_FUNCTION: Short = 3
        const val TOKEN_PARENTHESES_OPEN: Short = 4
        const val TOKEN_PARENTHESES_CLOSE: Short = 5
        const val TOKEN_VARIABLE: Short = 6
        const val TOKEN_SEPARATOR: Short = 7
    }
}
