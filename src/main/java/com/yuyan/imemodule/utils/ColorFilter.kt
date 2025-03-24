package com.yuyan.imemodule.utils

import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter

@Suppress("FunctionName")
fun DarkenColorFilter(percent: Int): ColorFilter {
    val value = percent * 255 / 100
    return PorterDuffColorFilter(Color.argb(value, 0, 0, 0), PorterDuff.Mode.SRC_OVER)
}
