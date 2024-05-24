/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2023 Fcitx5 for Android Contributors
 */
package com.yuyan.imemodule.prefs.behavior

import com.yuyan.imemodule.view.preference.ManagedPreference


enum class LangSwitchBehavior {
    Enumerate,
    ToggleActivate,
    NextInputMethodApp;

    companion object : ManagedPreference.StringLikeCodec<LangSwitchBehavior> {
        override fun decode(raw: String): LangSwitchBehavior = valueOf(raw)
    }
}
