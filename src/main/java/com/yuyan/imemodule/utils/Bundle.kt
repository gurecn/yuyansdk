/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2023 Fcitx5 for Android Contributors
 */

package com.yuyan.imemodule.utils

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

inline fun <reified T : Serializable> Bundle.serializable(key: String): T? {
    @Suppress("DEPRECATION")
    return getSerializable(key) as? T
}
