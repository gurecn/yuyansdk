package com.yuyan.imemodule.libs.expression.operator

import kotlin.math.pow

object Operators {
    private const val INDEX_EQUAL = 0
    private const val INDEX_ADDITION = 1
    private const val INDEX_SUBTRACTION = 2
    private const val INDEX_MULTIPLICATION = 3
    private const val INDEX_DIVISION = 4
    private const val INDEX_POWER = 5
    private const val INDEX_MODULO = 6
    private const val INDEX_UNARY_MINUS = 7
    private const val INDEX_UNARY_PLUS = 8
    private val BUILT_IN_OPERATORS = arrayOfNulls<Operator>(9)

    init {
        BUILT_IN_OPERATORS[INDEX_EQUAL] = object : Operator("=", 1, true, PRECEDENCE_ADDITION) {
            override fun apply(vararg args: Double): Double {
                return args[0]
            }
        }
        BUILT_IN_OPERATORS[INDEX_ADDITION] = object : Operator("+", 2, true, PRECEDENCE_ADDITION) {
            override fun apply(vararg args: Double): Double {
                return args[0] + args[1]
            }
        }
        BUILT_IN_OPERATORS[INDEX_SUBTRACTION] =
            object : Operator("-", 2, true, PRECEDENCE_ADDITION) {
                override fun apply(vararg args: Double): Double {
                    return args[0] - args[1]
                }
            }
        BUILT_IN_OPERATORS[INDEX_UNARY_MINUS] =
            object : Operator("-", 1, false, PRECEDENCE_UNARY_MINUS) {
                override fun apply(vararg args: Double): Double {
                    return -args[0]
                }
            }
        BUILT_IN_OPERATORS[INDEX_UNARY_PLUS] =
            object : Operator("+", 1, false, PRECEDENCE_UNARY_PLUS) {
                override fun apply(vararg args: Double): Double {
                    return args[0]
                }
            }
        BUILT_IN_OPERATORS[INDEX_MULTIPLICATION] =
            object : Operator("*", 2, true, PRECEDENCE_MULTIPLICATION) {
                override fun apply(vararg args: Double): Double {
                    return args[0] * args[1]
                }
            }
        BUILT_IN_OPERATORS[INDEX_DIVISION] = object : Operator("/", 2, true, PRECEDENCE_DIVISION) {
            override fun apply(vararg args: Double): Double {
                if (args[1] == 0.0) {
                    throw ArithmeticException("Division by zero!")
                }
                return args[0] / args[1]
            }
        }
        BUILT_IN_OPERATORS[INDEX_POWER] = object : Operator("^", 2, false, PRECEDENCE_POWER) {
            override fun apply(vararg args: Double): Double {
                return args[0].pow(args[1])
            }
        }
        BUILT_IN_OPERATORS[INDEX_MODULO] = object : Operator("%", 1, true, PRECEDENCE_MODULO) {
            override fun apply(vararg args: Double): Double {
                return args[0] / 100.0
            }
        }
    }

    fun getBuiltinOperator(symbol: Char, numArguments: Int): Operator? {
        return when (symbol) {
            '+' -> BUILT_IN_OPERATORS[if (numArguments != 1)INDEX_ADDITION else INDEX_UNARY_PLUS]
            '-' -> BUILT_IN_OPERATORS[if (numArguments != 1)INDEX_SUBTRACTION else INDEX_UNARY_MINUS]
            '*' -> BUILT_IN_OPERATORS[INDEX_MULTIPLICATION]!!
            '÷', '/' -> BUILT_IN_OPERATORS[INDEX_DIVISION]!!
            '^' -> BUILT_IN_OPERATORS[INDEX_POWER]!!
            '%' -> BUILT_IN_OPERATORS[INDEX_MODULO]!!
            '=' -> BUILT_IN_OPERATORS[INDEX_EQUAL]!!
            else -> null
        }
    }
}
