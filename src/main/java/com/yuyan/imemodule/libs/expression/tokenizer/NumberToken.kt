package com.yuyan.imemodule.libs.expression.tokenizer

/**
 * Represents a number in the expression
 */
class NumberToken ( @JvmField val value: Double) : Token(TOKEN_NUMBER) {
    internal constructor(expression: CharArray?, offset: Int, len: Int) : this(String(expression!!, offset, len).toDouble())
}
