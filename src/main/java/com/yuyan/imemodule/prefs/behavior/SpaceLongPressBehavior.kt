
package com.yuyan.imemodule.prefs.behavior

import com.yuyan.imemodule.view.preference.ManagedPreference


enum class SpaceLongPressBehavior {
    None,
    Enumerate,
    ToggleActivate,
    ShowPicker;

    companion object : ManagedPreference.StringLikeCodec<SpaceLongPressBehavior> {
        override fun decode(raw: String): SpaceLongPressBehavior = valueOf(raw)
    }
}
