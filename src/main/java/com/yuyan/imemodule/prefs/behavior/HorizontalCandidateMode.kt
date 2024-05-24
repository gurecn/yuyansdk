/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2021-2023 Fcitx5 for Android Contributors
 */
package com.yuyan.imemodule.prefs.behavior

import com.yuyan.imemodule.view.preference.ManagedPreference


enum class HorizontalCandidateMode {
    NeverFillWidth,
    AutoFillWidth,
    AlwaysFillWidth;

    companion object : ManagedPreference.StringLikeCodec<HorizontalCandidateMode> {
        override fun decode(raw: String): HorizontalCandidateMode = valueOf(raw)
    }
}