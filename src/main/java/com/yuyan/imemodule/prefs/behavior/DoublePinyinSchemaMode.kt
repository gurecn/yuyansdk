package com.yuyan.imemodule.prefs.behavior

import com.yuyan.imemodule.view.preference.ManagedPreference

enum class DoublePinyinSchemaMode {
    natural,
    flypy,
    abc,
    mspy,
    sogou,
    ziguang;

    companion object : ManagedPreference.StringLikeCodec<DoublePinyinSchemaMode> {
        override fun decode(raw: String) = DoublePinyinSchemaMode.valueOf(raw)
    }
}