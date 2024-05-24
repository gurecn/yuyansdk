
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