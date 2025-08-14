
package com.yuyan.imemodule.ui.setup

import android.content.Context
import androidx.core.text.HtmlCompat
import com.yuyan.imemodule.R
import com.yuyan.imemodule.utils.InputMethodUtil

enum class SetupPage {
    Enable, Select;

    fun getHintText(context: Context) = context.getString(
        when (this) {
            Enable -> R.string.enable_ime_hint
            Select -> R.string.select_ime_hint
        }, context.getString(R.string.app_name)
    ).let { HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY) }

    fun getButtonText(context: Context) = context.getText(
        when (this) {
            Enable -> R.string.enable_ime
            Select -> R.string.select_ime
        }
    )

    fun getButtonAction(context: Context) = when (this) {
        Enable -> InputMethodUtil.startSettingsActivity(context)
        Select -> InputMethodUtil.showPicker()
    }

    fun isDone() = when (this) {
        Enable -> InputMethodUtil.isEnabled()
        Select -> InputMethodUtil.isSelected()
    }

    companion object {
        fun valueOf(value: Int) = entries[value]
        fun SetupPage.isLastPage() = this == entries.last()
        fun Int.isLastPage() = this == entries.size - 1
        fun hasUndonePage() = entries.any { !it.isDone() }
        fun firstUndonePage() = entries.firstOrNull { !it.isDone() }
    }
}