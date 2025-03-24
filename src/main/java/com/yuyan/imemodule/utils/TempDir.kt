package com.yuyan.imemodule.utils

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
