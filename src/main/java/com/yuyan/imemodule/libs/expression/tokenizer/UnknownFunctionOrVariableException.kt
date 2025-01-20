package com.yuyan.imemodule.libs.expression.tokenizer

internal class UnknownFunctionOrVariableException( val expression: String, val position: Int, length: Int) : IllegalArgumentException() {
    override val message: String
    val token: String

    init {
        token = token(expression, position, length)
        message = "Unknown function or variable '$token' at pos $position in expression '$expression'"
    }

    companion object {
        /**
         * Serial version UID.
         */
        private const val serialVersionUID = 1L
        private fun token(expression: String, position: Int, length: Int): String {
            val len = expression.length
            var end = position + length - 1
            if (len < end) {
                end = len
            }
            return expression.substring(position, end)
        }
    }
}
