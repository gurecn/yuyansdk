package com.yuyan.imemodule.libs.expression.operator

/**
 * Class representing operators that can be used in an expression
 */
abstract class Operator ( val symbol: String, val numOperands: Int, val isLeftAssociative: Boolean, val precedence: Int) {

    /**
     * Apply the operation on the given operands
     *
     * @param args the operands for the operation
     * @return the calculated result of the operation
     */
    abstract fun apply(vararg args: Double): Double

    companion object {
        /**
         * The precedence value for the addition operation
         */
        const val PRECEDENCE_ADDITION = 500

        /**
         * The precedence value for the multiplication operation
         */
        const val PRECEDENCE_MULTIPLICATION = 1000

        /**
         * The precedence value for the division operation
         */
        const val PRECEDENCE_DIVISION = PRECEDENCE_MULTIPLICATION

        /**
         * The precedence value for the modulo operation
         */
        const val PRECEDENCE_MODULO = PRECEDENCE_DIVISION

        /**
         * The precedence value for the power operation
         */
        const val PRECEDENCE_POWER = 10000

        /**
         * The precedence value for the unary minus operation
         */
        const val PRECEDENCE_UNARY_MINUS = 5000

        /**
         * The precedence value for the unary plus operation
         */
        const val PRECEDENCE_UNARY_PLUS = PRECEDENCE_UNARY_MINUS

        /**
         * The set of allowed operator chars
         */
        val ALLOWED_OPERATOR_CHARS = charArrayOf(
            '+', '-', '*', '/', '%', '^', '!', '#', '§',
            '$', '&', ';', ':', '~', '<', '>', '|', '=', '÷', '√', '∛', '⌈', '⌊'
        )

        /**
         * Check if a character is an allowed operator char
         *
         * @param ch the char to check
         * @return true if the char is allowed an an operator symbol, false otherwise
         */
        fun isAllowedOperatorChar(ch: Char): Boolean {
            for (allowed in ALLOWED_OPERATOR_CHARS) {
                if (ch == allowed) {
                    return true
                }
            }
            return false
        }
    }
}
