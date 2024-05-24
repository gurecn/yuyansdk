/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2024 Fcitx5 for Android Contributors
 */

package com.yuyan.imemodule.ui.utils

import android.widget.EditText

inline val EditText.str: String
    get() = editableText.toString()
