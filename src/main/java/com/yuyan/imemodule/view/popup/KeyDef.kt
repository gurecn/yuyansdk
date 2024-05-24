/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2021-2023 Fcitx5 for Android Contributors
 */
package com.yuyan.imemodule.view.popup

open class KeyDef(
    val popup: Array<Popup>? = null
) {

    sealed class Popup {
        class Keyboard(val label: String) : Popup()
    }
}