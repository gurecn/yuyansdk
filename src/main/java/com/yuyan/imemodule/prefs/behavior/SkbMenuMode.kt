package com.yuyan.imemodule.prefs.behavior

import com.yuyan.imemodule.view.preference.ManagedPreference

enum class SkbMenuMode {
    SwitchKeyboard,
    KeyboardHeight,
    DarkTheme,
    Feedback,
    NumberRow,
    JianFan,
    LockEnglish,
    SymbolShow,
    Mnemonic,
    FlowerTypeface,
    EmojiInput,
    Handwriting,
    Custom,
    Settings,
    FloatKeyboard,
    OneHanded,
    PinyinT9,
    Pinyin26Jian,
    PinyinLx17,
    PinyinHandWriting,
    Pinyin26Double,
    PinyinStroke,
    ClipBoard,
    ClearClipBoard,
    Phrases,
    AddPhrases,
    CloseSKB,
    Emojicon,
    Emoticon;

    companion object : ManagedPreference.StringLikeCodec<SkbMenuMode> {
        override fun decode(raw: String): SkbMenuMode =
            SkbMenuMode.valueOf(raw)
    }
}