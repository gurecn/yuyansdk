/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2021-2023 Fcitx5 for Android Contributors
 */
package com.yuyan.imemodule.data.theme

import android.content.Context
import androidx.annotation.StringRes
import com.yuyan.imemodule.prefs.ManagedPreferenceUi
import com.yuyan.imemodule.view.preference.ThemeSelectPreference

class ManagedThemePreferenceUi(
    @StringRes
    val title: Int,
    key: String,
    val defaultValue: Theme,
    @StringRes
    val summary: Int? = null,
    enableUiOn: (() -> Boolean)? = null
) : ManagedPreferenceUi<ThemeSelectPreference>(key, enableUiOn) {
    override fun createUi(context: Context) = ThemeSelectPreference(context, defaultValue).apply {
        key = this@ManagedThemePreferenceUi.key
        isIconSpaceReserved = false
        isSingleLineTitle = false
        if (this@ManagedThemePreferenceUi.summary != null)
            setSummary(this@ManagedThemePreferenceUi.summary)
        setTitle(this@ManagedThemePreferenceUi.title)
    }
}