package com.yuyan.imemodule.utils.expression.tokenizer;

/**
 * Abstract class for tokens used by exp4j to tokenize expressions
 */
public abstract class Token {
    public static final short TOKEN_NUMBER = 1;
    public static final short TOKEN_OPERATOR = 2;
    public static final short TOKEN_FUNCTION = 3;
    public static final short TOKEN_PARENTHESES_OPEN = 4;
    public static final short TOKEN_PARENTHESES_CLOSE = 5;
    public static final short TOKEN_VARIABLE = 6;
    public static final short TOKEN_SEPARATOR = 7;

    private final int type;

    Token(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}
