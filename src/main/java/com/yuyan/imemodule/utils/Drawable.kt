package com.yuyan.imemodule.utils

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.RippleDrawable
import androidx.annotation.ColorInt

fun rippleDrawable(@ColorInt color: Int) = RippleDrawable(ColorStateList.valueOf(color), null, ColorDrawable(Color.WHITE))
