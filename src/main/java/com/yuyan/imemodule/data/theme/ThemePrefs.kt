package com.yuyan.imemodule.data.theme

import android.content.SharedPreferences
import androidx.annotation.StringRes
import com.yuyan.imemodule.R
import com.yuyan.imemodule.prefs.ManagedPreferenceCategory
import com.yuyan.imemodule.prefs.behavior.KeyboardSymbolSlideUpMod
import com.yuyan.imemodule.prefs.behavior.SkbStyleMode
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
    val themeSetting = category(R.string.theme_select)
    /**
     * When [followSystemDayNightTheme] is disabled, this theme is used.
     * This is effectively an internal preference which does not need UI.
     */
    val normalModeTheme = ManagedThemePreference(
        sharedPreferences, "normal_mode_theme", ThemeManager.DefaultTheme
    ).also {
        it.register()
    }

    val skbStyleMode = list(
        R.string.keyboard_style_mod,
        "keyboard_style_mod",
        SkbStyleMode.Yuyan,
        SkbStyleMode,
        listOf(
            SkbStyleMode.Yuyan,
            SkbStyleMode.Google,
            SkbStyleMode.Samsung
        ),
        listOf(
            R.string.keyboard_style_mod_yuyan,
            R.string.keyboard_style_mod_google,
            R.string.keyboard_style_mod_samsung,
        )
    )

    val followSystemDayNightTheme = switch(
        R.string.follow_system_day_night_theme,
        "follow_system_dark_mode",
        true,
        summary = R.string.follow_system_day_night_theme_summary
    )

    val lightModeTheme = themePreference(
        R.string.light_mode_theme,
        "light_mode_theme",
        ThemePreset.MaterialLight,
        enableUiOn = {
            followSystemDayNightTheme.getValue()
        })

    val darkModeTheme = themePreference(
        R.string.dark_mode_theme,
        "dark_mode_theme",
        ThemePreset.MaterialDark,
        enableUiOn = {
            followSystemDayNightTheme.getValue()
        })

    val dayNightModePrefNames = setOf(
        followSystemDayNightTheme.key,
        lightModeTheme.key,
        darkModeTheme.key
    )

    // 键盘设置title
    val titleKeyboardSetting = category(R.string.setting_ime_keyboard_show)


    val keyboardFontBold =
        switch(R.string.keyboard_font_bold, "keyboard_font_bold_enable", true)

    val keyboardFontSize = int(
        R.string.keyboard_font_size,
        "keyboard_font_size",
        100,
        70,
        170,
        "%"
    )

    val keyboardSymbol =
        switch(R.string.keyboard_symbol_show, "keyboard_symbol_show_enable", true)

    val symbolSlideUpMod = list(
        R.string.keyboard_symbol_slide_up_mod,
        "keyboard_symbol_slide_up_mod",
        KeyboardSymbolSlideUpMod.MEDIUM,
        KeyboardSymbolSlideUpMod,
        listOf(
            KeyboardSymbolSlideUpMod.SHORT,
            KeyboardSymbolSlideUpMod.MEDIUM,
            KeyboardSymbolSlideUpMod.LONG
        ),
        listOf(
            R.string.keyboard_symbol_slide_up_short,
            R.string.keyboard_symbol_slide_up_medium,
            R.string.keyboard_symbol_slide_up_long,
        )
    ) {
        keyboardSymbol.getValue()
    }

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
}
