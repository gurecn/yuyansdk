package com.yuyan.imemodule.prefs.behavior

import com.yuyan.imemodule.view.preference.ManagedPreference

enum class HalfWidthSymbolsMode {
    All,
    OnlyUsed,
    None;

    companion object : ManagedPreference.StringLikeCodec<HalfWidthSymbolsMode> {
        override fun decode(raw: String): HalfWidthSymbolsMode =
            HalfWidthSymbolsMode.valueOf(raw)
    }
}