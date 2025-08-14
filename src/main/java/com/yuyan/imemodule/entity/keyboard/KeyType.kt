package com.yuyan.imemodule.entity.keyboard

import com.yuyan.imemodule.view.preference.ManagedPreference

enum class KeyType {
    AccentKey,
    Function,
    Normal;

    companion object : ManagedPreference.StringLikeCodec<KeyType> {
        override fun decode(raw: String): KeyType =
            KeyType.valueOf(raw)
    }
}