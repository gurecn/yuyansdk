/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2024 Fcitx5 for Android Contributors
 */

package com.yuyan.imemodule.ui.utils

import com.yuyan.imemodule.application.ImeSdkApplication
import java.io.File

inline fun <T> withTempDir(block: (File) -> T): T {
    val dir = ImeSdkApplication.context.cacheDir.resolve(System.currentTimeMillis().toString()).also {
        it.mkdirs()
    }
    try {
        return block(dir)
    } finally {
        dir.deleteRecursively()
    }
}
