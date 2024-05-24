/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2021-2023 Fcitx5 for Android Contributors
 */
package com.yuyan.imemodule.prefs

import androidx.preference.PreferenceScreen
import com.yuyan.imemodule.view.preference.ManagedPreference

abstract class ManagedPreferenceProvider {

    private val _managedPreferences: MutableMap<String, ManagedPreference<*>> = mutableMapOf()

    private val _managedPreferencesUi: MutableList<ManagedPreferenceUi<*>> = mutableListOf()

    val managedPreferences: Map<String, ManagedPreference<*>>
        get() = _managedPreferences

    val managedPreferencesUi: List<ManagedPreferenceUi<*>>
        get() = _managedPreferencesUi

    open fun createUi(screen: PreferenceScreen) {

    }

    fun ManagedPreferenceUi<*>.registerUi() {
        _managedPreferencesUi.add(this)
    }

    fun ManagedPreference<*>.register() {
        _managedPreferences[key] = this
    }

}