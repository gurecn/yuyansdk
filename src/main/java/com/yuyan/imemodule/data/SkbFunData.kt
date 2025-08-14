package com.yuyan.imemodule.data

import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.Launcher
import com.yuyan.imemodule.entity.SkbFunItem
import com.yuyan.imemodule.prefs.behavior.SkbMenuMode

val menuSkbFunsPreset: Map<SkbMenuMode, SkbFunItem> = hashMapOf(
    SkbMenuMode.Emojicon to SkbFunItem(Launcher.instance.context.getString(R.string.emoji_setting), R.drawable.ic_menu_emoji, SkbMenuMode.Emojicon),
    SkbMenuMode.SwitchKeyboard to SkbFunItem(Launcher.instance.context.getString(R.string.changeKeyboard), R.drawable.ic_menu_keyboard, SkbMenuMode.SwitchKeyboard),
    SkbMenuMode.KeyboardHeight to SkbFunItem(Launcher.instance.context.getString(R.string.setting_ime_keyboard_height), R.drawable.ic_menu_height, SkbMenuMode.KeyboardHeight),
    SkbMenuMode.ClipBoard to SkbFunItem(Launcher.instance.context.getString(R.string.clipboard), R.drawable.ic_menu_clipboard, SkbMenuMode.ClipBoard),
    SkbMenuMode.Phrases to SkbFunItem(Launcher.instance.context.getString(R.string.phrases), R.drawable.ic_menu_phrases, SkbMenuMode.Phrases),
    SkbMenuMode.DarkTheme to SkbFunItem(Launcher.instance.context.getString(R.string.keyboard_theme_night), R.drawable.ic_menu_dark, SkbMenuMode.DarkTheme),
    SkbMenuMode.Feedback to SkbFunItem(Launcher.instance.context.getString(R.string.keyboard_feedback), R.drawable.ic_menu_touch, SkbMenuMode.Feedback),
    SkbMenuMode.OneHanded to SkbFunItem(Launcher.instance.context.getString(R.string.keyboard_one_handed_mod), R.drawable.ic_menu_one_hand, SkbMenuMode.OneHanded),
    SkbMenuMode.NumberRow to SkbFunItem(Launcher.instance.context.getString(R.string.engish_full_keyboard), R.drawable.ic_menu_shuzihang, SkbMenuMode.NumberRow),
    SkbMenuMode.JianFan to SkbFunItem(Launcher.instance.context.getString(R.string.setting_jian_fan), R.drawable.ic_menu_fanti, SkbMenuMode.JianFan),
    SkbMenuMode.Mnemonic to SkbFunItem(Launcher.instance.context.getString(R.string.keyboard_mnemonic_show), R.drawable.ic_menu_mnemonic, SkbMenuMode.Mnemonic),
    SkbMenuMode.FloatKeyboard to SkbFunItem(Launcher.instance.context.getString(R.string.keyboard_menu_float), R.drawable.ic_menu_float, SkbMenuMode.FloatKeyboard),
    SkbMenuMode.FlowerTypeface to SkbFunItem(Launcher.instance.context.getString(R.string.keyboard_flower_typeface), R.drawable.ic_menu_flower, SkbMenuMode.FlowerTypeface),
    SkbMenuMode.Custom to SkbFunItem(Launcher.instance.context.getString(R.string.skb_item_custom), R.drawable.ic_menu_custom, SkbMenuMode.Custom),
    SkbMenuMode.Settings to SkbFunItem(Launcher.instance.context.getString(R.string.skb_item_settings), R.drawable.ic_menu_setting, SkbMenuMode.Settings),
    SkbMenuMode.CloseSKB to SkbFunItem(Launcher.instance.context.getString(R.string.keyboard_iv_menu_close), R.drawable.ic_menu_arrow_down, SkbMenuMode.CloseSKB),
    SkbMenuMode.ClearClipBoard to SkbFunItem(Launcher.instance.context.getString(R.string.clipboard_clear), R.drawable.ic_menu_delete, SkbMenuMode.ClearClipBoard),
    SkbMenuMode.Emoticon to SkbFunItem(Launcher.instance.context.getString(R.string.emoticons), R.drawable.ic_menu_emoji_emoticons, SkbMenuMode.Emoticon),
    SkbMenuMode.AddPhrases to SkbFunItem(Launcher.instance.context.getString(R.string.add_phrases), R.drawable.ic_menu_plus, SkbMenuMode.AddPhrases),
    SkbMenuMode.LockClipBoard to SkbFunItem(Launcher.instance.context.getString(R.string.lock_view), R.drawable.icon_symbol_lock, SkbMenuMode.LockClipBoard),
    SkbMenuMode.TextEdit to SkbFunItem(Launcher.instance.context.getString(R.string.menu_text_edit), R.drawable.ic_menu_cursor_icon, SkbMenuMode.TextEdit),
    )