package com.yuyan.imemodule.utils.expression.operator;

public abstract class Operators {
    private static final int INDEX_EQUAL = 0;
    private static final int INDEX_ADDITION = 1;
    private static final int INDEX_SUBTRACTION = 2;
    private static final int INDEX_MULTIPLICATION = 3;
    private static final int INDEX_DIVISION = 4;
    private static final int INDEX_POWER = 5;
    private static final int INDEX_MODULO = 6;
    private static final int INDEX_UNARY_MINUS = 7;
    private static final int INDEX_UNARY_PLUS = 8;

    private static final Operator[] BUILT_IN_OPERATORS = new Operator[9];

    static {
        BUILT_IN_OPERATORS[INDEX_EQUAL] = new Operator("=", 1, true, Operator.PRECEDENCE_ADDITION) {
            @Override
            public double apply(final double... args) {
                return args[0];
            }
        };
        BUILT_IN_OPERATORS[INDEX_ADDITION] = new Operator("+", 2, true, Operator.PRECEDENCE_ADDITION) {
            @Override
            public double apply(final double... args) {
                return args[0] + args[1];
            }
        };
        BUILT_IN_OPERATORS[INDEX_SUBTRACTION] = new Operator("-", 2, true, Operator.PRECEDENCE_ADDITION) {
            @Override
            public double apply(final double... args) {
                return args[0] - args[1];
            }
        };
        BUILT_IN_OPERATORS[INDEX_UNARY_MINUS] = new Operator("-", 1, false, Operator.PRECEDENCE_UNARY_MINUS) {
            @Override
            public double apply(final double... args) {
                return -args[0];
            }
        };
        BUILT_IN_OPERATORS[INDEX_UNARY_PLUS] = new Operator("+", 1, false, Operator.PRECEDENCE_UNARY_PLUS) {
            @Override
            public double apply(final double... args) {
                return args[0];
            }
        };
        BUILT_IN_OPERATORS[INDEX_MULTIPLICATION] = new Operator("*", 2, true, Operator.PRECEDENCE_MULTIPLICATION) {
            @Override
            public double apply(final double... args) {
                return args[0] * args[1];
            }
        };
        BUILT_IN_OPERATORS[INDEX_DIVISION] = new Operator("/", 2, true, Operator.PRECEDENCE_DIVISION) {
            @Override
            public double apply(final double... args) {
                if (args[1] == 0d) {
                    throw new ArithmeticException("Division by zero!");
                }
                return args[0] / args[1];
            }
        };
        BUILT_IN_OPERATORS[INDEX_POWER] = new Operator("^", 2, false, Operator.PRECEDENCE_POWER) {
            @Override
            public double apply(final double... args) {
                return Math.pow(args[0], args[1]);
            }
        };
        BUILT_IN_OPERATORS[INDEX_MODULO] = new Operator("%", 1, true, Operator.PRECEDENCE_MODULO) {
            @Override
            public double apply(final double... args) {
                return args[0] / 100d;
            }
        };
    }

    public static Operator getBuiltinOperator(final char symbol, final int numArguments) {
        return switch (symbol) {
            case '+' -> {
                if (numArguments != 1) {
                    yield BUILT_IN_OPERATORS[INDEX_ADDITION];
                }
                yield BUILT_IN_OPERATORS[INDEX_UNARY_PLUS];
            }
            case '-' -> {
                if (numArguments != 1) {
                    yield BUILT_IN_OPERATORS[INDEX_SUBTRACTION];
                }
                yield BUILT_IN_OPERATORS[INDEX_UNARY_MINUS];
            }
            case '*' -> BUILT_IN_OPERATORS[INDEX_MULTIPLICATION];
            case '÷', '/' -> BUILT_IN_OPERATORS[INDEX_DIVISION];
            case '^' -> BUILT_IN_OPERATORS[INDEX_POWER];
            case '%' -> BUILT_IN_OPERATORS[INDEX_MODULO];
            case '=' -> BUILT_IN_OPERATORS[INDEX_EQUAL];
            default -> null;
        };
    }

}
