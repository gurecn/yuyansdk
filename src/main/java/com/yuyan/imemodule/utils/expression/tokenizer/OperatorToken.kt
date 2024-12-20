package com.yuyan.imemodule.utils.expression.tokenizer

import com.yuyan.imemodule.utils.expression.operator.Operator

/**
 * Represents an operator used in expressions
 */
class OperatorToken(op: Operator?) : Token(TOKEN_OPERATOR) {

    @JvmField
    val operator: Operator

    init {
        requireNotNull(op) { "Operator is unknown for token." }
        operator = op
    }
}
