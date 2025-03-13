package com.yuyan.imemodule.utils

import android.content.pm.PackageInfo
import android.os.Build

val PackageInfo.versionCodeCompat: Long
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        longVersionCode
    } else {
        @Suppress("DEPRECATION")
        versionCode.toLong()
    }
