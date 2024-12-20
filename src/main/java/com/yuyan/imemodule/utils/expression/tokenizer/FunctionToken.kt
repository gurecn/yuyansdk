package com.yuyan.imemodule.utils.expression.tokenizer

import com.yuyan.imemodule.utils.expression.function.Function

class FunctionToken(@JvmField val function: Function) : Token(TOKEN_FUNCTION)
