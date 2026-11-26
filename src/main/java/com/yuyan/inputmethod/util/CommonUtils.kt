package com.yuyan.inputmethod.util

import android.text.SpannableStringBuilder
import android.text.SpannedString

inline fun buildSpannedString(
    builderAction: SpannableStringBuilder.() -> Unit
): String {
    val builder = SpannableStringBuilder()
    builder.builderAction()
    return SpannedString(builder).toString()
}