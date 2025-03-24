package com.yuyan.imemodule.utils

import androidx.annotation.StringRes
import com.yuyan.imemodule.application.ImeSdkApplication

inline fun <T : Throwable> errorT(cons: (String) -> T, @StringRes messageTemplate: Int, messageArg: String? = null): Nothing =
    throw cons(
        messageArg?.let {
            ImeSdkApplication.context.getString(messageTemplate, it)
        } ?: ImeSdkApplication.context.getString(
            messageTemplate
        )
    )

fun errorRuntime(@StringRes messageTemplate: Int, messageArg: String? = null): Nothing =
    errorT(::RuntimeException, messageTemplate, messageArg)