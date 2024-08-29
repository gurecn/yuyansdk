package com.yuyan.imemodule.data.theme

import android.content.SharedPreferences
import androidx.annotation.StringRes
import com.yuyan.imemodule.R
import com.yuyan.imemodule.prefs.ManagedPreferenceCategory
import com.yuyan.imemodule.prefs.behavior.KeyboardOneHandedMod
import com.yuyan.imemodule.view.preference.ManagedPreference

class ThemePrefs(sharedPreferences: SharedPreferences) :
    ManagedPreferenceCategory(R.string.theme, sharedPreferences) {

    private fun themePreference(
        @StringRes
        title: Int,
        key: String,
        defaultValue: Theme,
        @StringRes
        summary: Int? = null,
        enableUiOn: (() -> Boolean)? = null
    ): ManagedThemePreference {
        val pref = ManagedThemePreference(sharedPreferences, key, defaultValue)
        val ui = ManagedThemePreferenceUi(title, key, defaultValue, summary, enableUiOn)
        pref.register()
        ui.registerUi()
        return pref
    }

    // 主题设置title
    val themeSetting = category(R.string.theme)
    /**
     * When [followSystemDayNightTheme] is disabled, this theme is used.
     * This is effectively an internal preference which does not need UI.
     */
    val normalModeTheme = ManagedThemePreference(
        sharedPreferences, "normal_mode_theme", ThemeManager.DefaultTheme
    ).also {
        it.register()
    }

    val followSystemDayNightTheme = switch(
        R.string.follow_system_day_night_theme,
        "follow_system_dark_mode",
        true,
        summary = R.string.follow_system_day_night_theme_summary
    )

    val lightModeTheme = themePreference(
        R.string.light_mode_theme,
        "light_mode_theme",
        ThemePreset.CustomRed,
        enableUiOn = {
            followSystemDayNightTheme.getValue()
        })

    val darkModeTheme = themePreference(
        R.string.dark_mode_theme,
        "dark_mode_theme",
        ThemePreset.PixelDark,
        enableUiOn = {
            followSystemDayNightTheme.getValue()
        })

    val dayNightModePrefNames = setOf(
        followSystemDayNightTheme.key,
        lightModeTheme.key,
        darkModeTheme.key
    )

    // 键盘设置title
    val titleKeyboardSetting = category(R.string.setting_ime_keyboard)

    val keyBorder = switch(R.string.key_border, "key_border", true)

    val keyXMargin: ManagedPreference.PInt
    val keyYMargin: ManagedPreference.PInt

    init {
        val (primary, secondary) = twinInt(
            R.string.keyboard_key_margin,
            R.string.key_horizontal_margin,
            "keyboard_key_margin_x",
            10,
            R.string.key_vertical_margin,
            "keyboard_key_margin_y",
            20,
            0,
            100,
            "",
            defaultLabel = R.string.system_default
        ) {
            keyBorder.getValue()
        }
        keyXMargin = primary
        keyYMargin = secondary
    }

    val keyRadius = int(
        R.string.key_radius,
        "key_radius",
        20,
        0,
        60,
        "dp"
    ) {
        keyBorder.getValue()
    }

    val candidateTextSize = int(
        R.string.candidate_size_input_setting,
        "candidate_size_input_setting",
        10,
        -10,
        30,
        "%",
        defaultLabel = R.string.system_default
    )

    val keyboardBalloonShow =
        switch(R.string.keypopup_input_settings, "keyboard_balloon_show_enable", false)

    val longPressTimeout = int(
        R.string.long_press_timeout,
        "long_press_timeout",
        400,
        100,
        700,
        "毫秒",
        50,
        defaultLabel = R.string.number_400_ms
    )


    val abcNumberLine =
        switch(R.string.engish_full_keyboard, "keyboard_abc_number_line_enable", false)

    val keyboardMnemonic =
        switch(R.string.keyboard_mnemonic_show, "keyboard_mnemonic_show_enable", false)

    val keyboardSymbol =
        switch(R.string.keyboard_symbol_show, "keyboard_symbol_show_enable", true)

    val spaceSwipeMoveCursor =
        switch(R.string.space_swipe_move_cursor, "space_swipe_move_cursor", true)

    val deleteLocationTop =
        switch(R.string.keyboard_delete_location_top, "keyboard_delete_location_top", true)

    // 锁定英语键盘:锁定后，切换到英语键盘，下次弹出键盘使用英语模式
    val keyboardLockEnglish =
        switch(R.string.keyboard_menu_lock_english, "keyboard_menu_lock_english_enable", false)

    // 悬浮键盘
    val keyboardModeFloat =
        switch(R.string.keyboard_menu_float, "keyboard_mode_float", false)


    val oneHandedModSwitch = switch(R.string.keyboard_one_handed_mod, "keyboard_one_handed_mod_enable", false)

    val oneHandedMod = list(
        R.string.keyboard_one_handed_mod,
        "keyboard_one_handed_mod",
        KeyboardOneHandedMod.LEFT,
        KeyboardOneHandedMod,
        listOf(
            KeyboardOneHandedMod.LEFT,
            KeyboardOneHandedMod.RIGHT
        ),
        listOf(
            R.string.keyboard_one_handed_mod_left,
            R.string.keyboard_one_handed_mod_right
        )
    ) {
        oneHandedModSwitch.getValue()
    }
}
