package com.yuyan.imemodule.libs.expression.function

import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cbrt
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.exp
import kotlin.math.expm1
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.ln1p
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.math.tanh

/**
 * Class representing the builtin functions available for use in expressions
 */
object Functions {
    private const val INDEX_SIN = 0
    private const val INDEX_COS = 1
    private const val INDEX_TAN = 2
    private const val INDEX_CSC = 3
    private const val INDEX_SEC = 4
    private const val INDEX_COT = 5
    private const val INDEX_SINH = 6
    private const val INDEX_COSH = 7
    private const val INDEX_TANH = 8
    private const val INDEX_CSCH = 9
    private const val INDEX_SECH = 10
    private const val INDEX_COTH = 11
    private const val INDEX_ASIN = 12
    private const val INDEX_ACOS = 13
    private const val INDEX_ATAN = 14
    private const val INDEX_SQRT = 15
    private const val INDEX_CBRT = 16
    private const val INDEX_ABS = 17
    private const val INDEX_CEIL = 18
    private const val INDEX_FLOOR = 19
    private const val INDEX_POW = 20
    private const val INDEX_EXP = 21
    private const val INDEX_EXPM1 = 22
    private const val INDEX_LOG10 = 23
    private const val INDEX_LOG2 = 24
    private const val INDEX_LOG = 25
    private const val INDEX_LOG1P = 26
    private const val INDEX_LOGB = 27
    private const val INDEX_SGN = 28
    private const val INDEX_TO_RADIAN = 29
    private const val INDEX_TO_DEGREE = 30
    private val BUILT_IN_FUNCTIONS = arrayOfNulls<Function>(31)

    init {
        BUILT_IN_FUNCTIONS[INDEX_SIN] = object : Function("sin") {
            override fun apply(vararg args: Double): Double {
                return sin(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_COS] = object : Function("cos") {
            override fun apply(vararg args: Double): Double {
                return cos(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_TAN] = object : Function("tan") {
            override fun apply(vararg args: Double): Double {
                return tan(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_COT] = object : Function("cot") {
            override fun apply(vararg args: Double): Double {
                val tan = tan(args[0])
                if (tan == 0.0) {
                    throw ArithmeticException("Division by zero in cotangent!")
                }
                return 1.0 / tan
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_LOG] = object : Function("log") {
            override fun apply(vararg args: Double): Double {
                return ln(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_LOG2] = object : Function("log2") {
            override fun apply(vararg args: Double): Double {
                return ln(args[0]) / ln(2.0)
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_LOG10] = object : Function("log10") {
            override fun apply(vararg args: Double): Double {
                return log10(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_LOG1P] = object : Function("log1p") {
            override fun apply(vararg args: Double): Double {
                return ln1p(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_ABS] = object : Function("abs") {
            override fun apply(vararg args: Double): Double {
                return abs(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_ACOS] = object : Function("acos") {
            override fun apply(vararg args: Double): Double {
                return acos(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_ASIN] = object : Function("asin") {
            override fun apply(vararg args: Double): Double {
                return asin(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_ATAN] = object : Function("atan") {
            override fun apply(vararg args: Double): Double {
                return atan(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_CBRT] = object : Function("cbrt") {
            override fun apply(vararg args: Double): Double {
                return cbrt(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_FLOOR] = object : Function("floor") {
            override fun apply(vararg args: Double): Double {
                return floor(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_SINH] = object : Function("sinh") {
            override fun apply(vararg args: Double): Double {
                return sinh(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_SQRT] = object : Function("sqrt") {
            override fun apply(vararg args: Double): Double {
                return sqrt(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_TANH] = object : Function("tanh") {
            override fun apply(vararg args: Double): Double {
                return tanh(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_COSH] = object : Function("cosh") {
            override fun apply(vararg args: Double): Double {
                return cosh(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_CEIL] = object : Function("ceil") {
            override fun apply(vararg args: Double): Double {
                return ceil(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_POW] = object : Function("pow", 2) {
            override fun apply(vararg args: Double): Double {
                return args[0].pow(args[1])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_EXP] = object : Function("exp", 1) {
            override fun apply(vararg args: Double): Double {
                return exp(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_EXPM1] = object : Function("expm1", 1) {
            override fun apply(vararg args: Double): Double {
                return expm1(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_SGN] = object : Function("signum", 1) {
            override fun apply(vararg args: Double): Double {
                return if (args[0] > 0) {
                    1.toDouble()
                } else if (args[0] < 0) {
                    (-1).toDouble()
                } else {
                    0.toDouble()
                }
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_CSC] = object : Function("csc") {
            override fun apply(vararg args: Double): Double {
                val sin = sin(args[0])
                if (sin == 0.0) {
                    throw ArithmeticException("Division by zero in cosecant!")
                }
                return 1.0 / sin
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_SEC] = object : Function("sec") {
            override fun apply(vararg args: Double): Double {
                val cos = cos(args[0])
                if (cos == 0.0) {
                    throw ArithmeticException("Division by zero in secant!")
                }
                return 1.0 / cos
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_CSCH] = object : Function("csch") {
            override fun apply(vararg args: Double): Double {
                //this would throw an ArithmeticException later as sinh(0) = 0
                return if (args[0] == 0.0) {
                    0.toDouble()
                } else 1.0 / sinh(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_SECH] = object : Function("sech") {
            override fun apply(vararg args: Double): Double {
                return 1.0 / cosh(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_COTH] = object : Function("coth") {
            override fun apply(vararg args: Double): Double {
                return cosh(args[0]) / sinh(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_LOGB] = object : Function("logb", 2) {
            override fun apply(vararg args: Double): Double {
                return ln(args[1]) / ln(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_TO_RADIAN] = object : Function("toradian") {
            override fun apply(vararg args: Double): Double {
                return Math.toRadians(args[0])
            }
        }
        BUILT_IN_FUNCTIONS[INDEX_TO_DEGREE] = object : Function("todegree") {
            override fun apply(vararg args: Double): Double {
                return Math.toDegrees(args[0])
            }
        }
    }

    /**
     * Get the builtin function for a given name
     *
     * @param name te name of the function
     * @return a Function instance
     */
    @JvmStatic
    fun getBuiltinFunction(name: String?): Function? {
        return when (name) {
            "sin" -> BUILT_IN_FUNCTIONS[INDEX_SIN]
            "cos" -> BUILT_IN_FUNCTIONS[INDEX_COS]
            "tan" -> BUILT_IN_FUNCTIONS[INDEX_TAN]
            "cot" -> BUILT_IN_FUNCTIONS[INDEX_COT]
            "asin" -> BUILT_IN_FUNCTIONS[INDEX_ASIN]
            "acos" -> BUILT_IN_FUNCTIONS[INDEX_ACOS]
            "atan" -> BUILT_IN_FUNCTIONS[INDEX_ATAN]
            "sinh" -> BUILT_IN_FUNCTIONS[INDEX_SINH]
            "cosh" -> BUILT_IN_FUNCTIONS[INDEX_COSH]
            "tanh" -> BUILT_IN_FUNCTIONS[INDEX_TANH]
            "abs" -> BUILT_IN_FUNCTIONS[INDEX_ABS]
            "log" -> BUILT_IN_FUNCTIONS[INDEX_LOG]
            "log10" -> BUILT_IN_FUNCTIONS[INDEX_LOG10]
            "log2" -> BUILT_IN_FUNCTIONS[INDEX_LOG2]
            "log1p" -> BUILT_IN_FUNCTIONS[INDEX_LOG1P]
            "ceil" -> BUILT_IN_FUNCTIONS[INDEX_CEIL]
            "floor" -> BUILT_IN_FUNCTIONS[INDEX_FLOOR]
            "sqrt" -> BUILT_IN_FUNCTIONS[INDEX_SQRT]
            "cbrt" -> BUILT_IN_FUNCTIONS[INDEX_CBRT]
            "pow" -> BUILT_IN_FUNCTIONS[INDEX_POW]
            "exp" -> BUILT_IN_FUNCTIONS[INDEX_EXP]
            "expm1" -> BUILT_IN_FUNCTIONS[INDEX_EXPM1]
            "signum" -> BUILT_IN_FUNCTIONS[INDEX_SGN]
            "csc" -> BUILT_IN_FUNCTIONS[INDEX_CSC]
            "sec" -> BUILT_IN_FUNCTIONS[INDEX_SEC]
            "csch" -> BUILT_IN_FUNCTIONS[INDEX_CSCH]
            "sech" -> BUILT_IN_FUNCTIONS[INDEX_SECH]
            "coth" -> BUILT_IN_FUNCTIONS[INDEX_COTH]
            "toradian" -> BUILT_IN_FUNCTIONS[INDEX_TO_RADIAN]
            "todegree" -> BUILT_IN_FUNCTIONS[INDEX_TO_DEGREE]
            else -> null
        }
    }
}
