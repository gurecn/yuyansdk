package com.yuyan.inputmethod.util

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.SpannedString
import android.text.TextUtils
import android.text.style.UnderlineSpan

inline fun buildSpannedString(
    builderAction: SpannableStringBuilder.() -> Unit
): String {
    val builder = SpannableStringBuilder()
    builder.builderAction()
    return SpannedString(builder).toString()
}

inline fun SpannableStringBuilder.inSpans(
    span: Any,
    builderAction: SpannableStringBuilder.() -> Unit
): SpannableStringBuilder {
    val start = length
    builderAction()
    setSpan(span, start, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    return this
}