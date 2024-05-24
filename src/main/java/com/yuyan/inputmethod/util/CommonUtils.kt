package com.yuyan.inputmethod.util

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.SpannedString
import android.text.TextUtils
import android.text.style.UnderlineSpan

inline fun buildSpannedString(
    builderAction: SpannableStringBuilder.() -> Unit
): SpannedString {
    val builder = SpannableStringBuilder()
    builder.builderAction()
    return SpannedString(builder)
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

inline fun SpannableStringBuilder.underline(
    builderAction: SpannableStringBuilder.() -> Unit
): SpannableStringBuilder = inSpans(UnderlineSpan(), builderAction = builderAction)

inline fun CharSequence.isDigitsOnly(): Boolean = TextUtils.isDigitsOnly(this)