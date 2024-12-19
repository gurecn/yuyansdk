package com.yuyan.imemodule.utils.expression.tokenizer;

/**
 * Represents an argument separator in functions i.e: ','
 */
class ArgumentSeparatorToken extends Token {
    /**
     * Create a new instance
     */
    ArgumentSeparatorToken() {
        super(Token.TOKEN_SEPARATOR);
    }
}
