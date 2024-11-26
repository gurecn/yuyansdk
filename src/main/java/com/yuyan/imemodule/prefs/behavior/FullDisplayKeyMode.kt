package com.yuyan.imemodule.prefs.behavior

import com.yuyan.imemodule.view.preference.ManagedPreference

enum class FullDisplayKeyMode {
    SwitchIme,
    SwitchLanguage,
    Clipboard,
    None;

    companion object : ManagedPreference.StringLikeCodec<FullDisplayKeyMode> {
        override fun decode(raw: String): FullDisplayKeyMode =
            FullDisplayKeyMode.valueOf(raw)
    }
}