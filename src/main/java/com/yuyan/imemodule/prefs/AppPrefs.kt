
package com.yuyan.imemodule.prefs

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.data.allSkbFuns
import com.yuyan.imemodule.data.commonSkbFuns
import com.yuyan.imemodule.handwriting.HdManager
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.InputFeedbacks.InputFeedbackMode
import com.yuyan.imemodule.prefs.behavior.DoublePinyinSchemaMode
import com.yuyan.imemodule.prefs.behavior.KeyboardOneHandedMod
import com.yuyan.imemodule.utils.DevicesUtils
import com.yuyan.imemodule.view.preference.ManagedPreference


class AppPrefs(private val sharedPreferences: SharedPreferences) {

    inner class Internal : ManagedPreferenceInternal(sharedPreferences) {
        val pinyinModeRime = string("input_method_pinyin_mode_rime", CustomConstant.SCHEMA_ZH_T9) //拼音输入模式记录，记录引擎
        val inputDefaultMode = int("input_default_method_mode", InputModeSwitcherManager.MODE_T9_CHINESE)   //默认输入法类型
        val inputMethodPinyinMode = int("input_method_pinyin_mode", InputModeSwitcherManager.MODE_T9_CHINESE)  // 保存中文输入法类型
        val dataDictVersion = int("rime_dict_data_version", 0)  //缓存rime词库版本号,用于校验是否覆盖词库文件
        val keyboardHeightRatio = float("keyboard_height_ratio", 0.3f)     //键盘高度比例
        val keyboardHolderWidthRatio = float("keyboard_holder_width_ratio", 0.2f)     //键盘占位宽度比例，单手模式
        val keyboardBottomPaddingFloat = int("keyboard_padding_bottom", DevicesUtils.dip2px(100))     //竖屏悬浮模式底边距
        val keyboardRightPaddingFloat = int("keyboard_padding_right", DevicesUtils.dip2px(20))     //竖屏悬浮模式右边距
        val keyboardBottomPaddingLandscapeFloat = int("keyboard_padding_bottom_landscape", DevicesUtils.dip2px(50))     //横屏悬浮模式底边距
        val keyboardRightPaddingLandscapeFloat = int("keyboard_padding_right_landscape", DevicesUtils.dip2px(20))     //横屏悬浮模式右边距
        val keyboardBottomPadding = int("keyboard_padding_bottom_normal", DevicesUtils.dip2px(0))     //竖屏非悬浮底边距
        val keyboardRightPadding = int("keyboard_padding_right_normal", DevicesUtils.dip2px(0))     //竖屏非悬浮右边距
//        val keyboardBottomPaddingLandscape = int("keyboard_padding_bottom_landscape_normal", DevicesUtils.dip2px(0))     //横屏非悬浮底边距
//        val keyboardRightPaddingLandscape = int("keyboard_padding_right_landscape_normal", DevicesUtils.dip2px(0))     //横屏竖屏非悬浮右边距

        val keyboardBarMenuCommon = string("keyboard_bar_menu_common", commonSkbFuns.joinToString())     //缓存候选词菜单栏
        val keyboardSettingMenuAll = string("keyboard_bar_menu_all", allSkbFuns.joinToString())     //缓存候键盘设置菜单
    }

    inner class Input : ManagedPreferenceCategory(R.string.setting_ime_input, sharedPreferences) {

        val titleChinese = category(R.string.chinese_input_setting)

        val chineseFanTi = switch(
            R.string.setting_jian_fan, "chinese_jian_fan_enable", false
        )

        val doublePYSchemaMode =
            list(
                R.string.double_pinyin_schema_mode,
                "double_pinyin_schema_mode",
                DoublePinyinSchemaMode.flypy,
                DoublePinyinSchemaMode,
                listOf(
                    DoublePinyinSchemaMode.flypy,
                    DoublePinyinSchemaMode.natural,
                    DoublePinyinSchemaMode.abc,
                    DoublePinyinSchemaMode.mspy,
                    DoublePinyinSchemaMode.sogou,
                    DoublePinyinSchemaMode.ziguang,
                ),
                listOf(
                    R.string.double_pinyin_flypy_plus,
                    R.string.double_pinyin_natural,
                    R.string.double_pinyin_abc,
                    R.string.double_pinyin_mspy,
                    R.string.double_pinyin_sougou,
                    R.string.double_pinyin_ziguang,
                )
            )

        val titleEnglish = category(R.string.EnglishInput)

        //输出英文单词:英文补全
        val abcSearchEnglishCell = switch(
            R.string.search_english_cell, "search_english_cell_enable", true
        )

        val abcSpaceAuto = switch(
            R.string.space_auto, "abc_space_auto_enable", false
        ){
            abcSearchEnglishCell.getValue()
        }

        val titleEmoji = category(R.string.emoji_setting)
        val emojiInput = switch(
            R.string.emoji_input, "emoji_input_enable", false
        )
    }

    inner class KeyboardFeedback : ManagedPreferenceCategory(R.string.keyboard_feedback, sharedPreferences) {

        val hapticOnKeyPress =
            list(
                R.string.button_haptic_feedback,
                "haptic_on_keypress",
                InputFeedbackMode.Enabled,
                InputFeedbackMode,
                listOf(
                    InputFeedbackMode.Enabled,
                    InputFeedbackMode.Disabled
                ),
                listOf(
                    R.string.enabled,
                    R.string.disabled
                )
            )

        val buttonPressVibrationAmplitude = int(
            R.string.button_vibration_amplitude,
            "button_vibration_press_amplitude",
            10,
            0,
            100,
            "%",
            defaultLabel = R.string.system_default
        ) {
            hapticOnKeyPress.getValue() != InputFeedbackMode.Disabled
        }



        val soundOnKeyPress = list(
            R.string.button_sound,
            "sound_on_keypress",
            InputFeedbackMode.Enabled,
            InputFeedbackMode,
            listOf(
                InputFeedbackMode.Enabled,
                InputFeedbackMode.Disabled
            ),
            listOf(
                R.string.enabled,
                R.string.disabled
            )
        )
        val soundOnKeyPressVolume = int(
            R.string.button_sound_volume,
            "button_sound_volume",
            0,
            0,
            100,
            "%",
            defaultLabel = R.string.system_default
        ) {
            soundOnKeyPress.getValue() != InputFeedbackMode.Disabled
        }

    }

    inner class KeyboardSetting : ManagedPreferenceCategory(R.string.setting_ime_keyboard, sharedPreferences) {

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
            -20,
            40,
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

        val keyboardDoubleInputKey =
            switch(R.string.keyboard_double_input_key, "keyboard_double_input_key_enable", false)

        val keyboardSymbol =
            switch(R.string.keyboard_symbol_show, "keyboard_symbol_show_enable", true)

        val spaceSwipeMoveCursor =
            switch(R.string.space_swipe_move_cursor, "space_swipe_move_cursor", true)

        val deleteLocationTop =
            switch(R.string.keyboard_delete_location_top, "keyboard_delete_location_top", true)

        // 锁定英语键盘:锁定后，切换到英语键盘，下次弹出键盘使用英语模式
        val keyboardLockEnglish =
            switch(R.string.keyboard_menu_lock_english, "keyboard_menu_lock_english_enable", false)

        val switchIMEKey =
            switch(R.string.keyboard_key_switch_ime, "keyboard_key_switch_ime_enable", true)

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

    inner class Voice : ManagedPreferenceCategory(R.string.setting_ime_input, sharedPreferences) {
//        val titleChinese = category(R.string.chinese_input_setting)
    }

    inner class Handwriting : ManagedPreferenceCategory(R.string.setting_ime_input, sharedPreferences) {


        val handWritingWidth = int(
            R.string.paint_thickness,
            "hand_writing_width",
            35,
            0,
            100,
            "%",
            defaultLabel = R.string.system_default
        )

        val handWritingSpeed = int(
            R.string.discern_sensitive,
            "hand_writing_speed",
            500,
            300,
            1300,
            "毫秒",
            100,
            defaultLabel = R.string.number_500_ms
        )
    }

    inner class Clipboard : ManagedPreferenceCategory(R.string.clipboard, sharedPreferences) {
        val clipboardListening = switch(R.string.clipboard_listening, "clipboard_enable", true)
        val clipboardHistoryLimit = int(
            R.string.clipboard_limit,
            "clipboard_limit",
            10,
            1,
            100,
            "条",
            defaultLabel = R.string.system_default
        ) { clipboardListening.getValue() }
        val clipboardSuggestion = switch(
            R.string.clipboard_suggestion, "clipboard_suggestion", true
        ) { clipboardListening.getValue() }
        val clipboardItemTimeout = int(
            R.string.clipboard_suggestion_timeout,
            "clipboard_item_timeout",
            30,
            10,
            200,
            "秒"
        ) { clipboardListening.getValue() && clipboardSuggestion.getValue() }
    }

    private val providers = mutableListOf<ManagedPreferenceProvider>()

    fun <T : ManagedPreferenceProvider> registerProvider(
        providerF: (SharedPreferences) -> T
    ): T {
        val provider = providerF(sharedPreferences)
        providers.add(provider)
        return provider
    }

    private fun <T : ManagedPreferenceProvider> T.register() = this.apply {
        registerProvider { this }
    }


    val internal = Internal().register()
    val voice = Voice().register()
    val handwriting = Handwriting().register()
    val input = Input().register()
    val keyboardFeedback = KeyboardFeedback().register()
    val clipboard = Clipboard().register()
    val keyboardSetting = KeyboardSetting().register()


    private val onSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            providers.forEach {
                it.managedPreferences[key]?.fireChange()
            }
        }

    @RequiresApi(Build.VERSION_CODES.N)
    fun syncToDeviceEncryptedStorage() {
        val ctx = ImeSdkApplication.context.createDeviceProtectedStorageContext()
        val sp = PreferenceManager.getDefaultSharedPreferences(ctx)
        sp.edit {
            internal.managedPreferences.forEach {
                it.value.putValueTo(this@edit)
            }

            input.managedPreferences.forEach {
                it.value.putValueTo(this@edit)
            }
            voice.managedPreferences.forEach {
                it.value.putValueTo(this@edit)
            }
            handwriting.managedPreferences.forEach {
                it.value.putValueTo(this@edit)
                HdManager.instance?.hciHwrRelease()
            }
            keyboardFeedback.managedPreferences.forEach {
                it.value.putValueTo(this@edit)
            }
            clipboard.managedPreferences.forEach {
                it.value.putValueTo(this@edit)
            }
        }
    }
    companion object {
        private var instance: AppPrefs? = null

        /**
         * MUST call before use
         */
        fun init(sharedPreferences: SharedPreferences) {
            if (instance != null)
                return
            instance = AppPrefs(sharedPreferences)
            sharedPreferences.registerOnSharedPreferenceChangeListener(AppPrefs.getInstance().onSharedPreferenceChangeListener)
        }

        fun getInstance() = instance!!
    }
}