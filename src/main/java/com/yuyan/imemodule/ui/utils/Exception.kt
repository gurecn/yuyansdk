/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2021-2023 Fcitx5 for Android Contributors
 */
package com.yuyan.imemodule.ui.utils

import androidx.annotation.StringRes
import com.yuyan.imemodule.application.ImeSdkApplication

inline fun <T : Throwable> errorT(
    cons: (String) -> T,
    @StringRes messageTemplate: Int,
    messageArg: String? = null
): Nothing =
    throw cons(
        messageArg?.let {
            ImeSdkApplication.context.getString(messageTemplate, it)
        } ?: ImeSdkApplication.context.getString(
            messageTemplate
        )
    )

fun errorState(@StringRes messageTemplate: Int, messageArg: String? = null): Nothing =
    errorT(::IllegalStateException, messageTemplate, messageArg)

fun errorArg(@StringRes messageTemplate: Int, messageArg: String? = null): Nothing =
    errorT(::IllegalArgumentException, messageTemplate, messageArg)

fun errorRuntime(@StringRes messageTemplate: Int, messageArg: String? = null): Nothing =
    errorT(::RuntimeException, messageTemplate, messageArg)