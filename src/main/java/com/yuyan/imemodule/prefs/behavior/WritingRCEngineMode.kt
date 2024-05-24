package com.yuyan.imemodule.prefs.behavior

import com.yuyan.imemodule.view.preference.ManagedPreference

enum class WritingRCMode {
    SENTENCE, OVERLAP, SENTENCE_OVERLAP;

    companion object : ManagedPreference.StringLikeCodec<WritingRCMode> {
        override fun decode(raw: String) = WritingRCMode.valueOf(raw)
    }
}