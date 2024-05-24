
package com.yuyan.imemodule.prefs.behavior

import com.yuyan.imemodule.view.preference.ManagedPreference


enum class ExpandedCandidateStyle {
    Grid,
    Flexbox;

    companion object : ManagedPreference.StringLikeCodec<ExpandedCandidateStyle> {
        override fun decode(raw: String): ExpandedCandidateStyle = valueOf(raw)
    }
}