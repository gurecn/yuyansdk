package com.yuyan.imemodule.libs.expression.tokenizer

import com.yuyan.imemodule.libs.expression.function.Function

class FunctionToken(@JvmField val function: Function) : Token(TOKEN_FUNCTION)
