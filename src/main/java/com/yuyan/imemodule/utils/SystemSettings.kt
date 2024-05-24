/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2024 Fcitx5 for Android Contributors
 */

package com.yuyan.imemodule.utils

import android.provider.Settings
import com.yuyan.imemodule.application.ImeSdkApplication

fun getSecureSettings(name: String): String? {
    return Settings.Secure.getString(ImeSdkApplication.context.contentResolver, name)
}
