
package com.yuyan.imemodule.prefs

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.handwriting.HdManager
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.InputFeedbacks.InputFeedbackMode
import com.yuyan.imemodule.prefs.behavior.WritingRCMode
import com.yuyan.imemodule.utils.DevicesUtils
import com.yuyan.imemodule.utils.vibrator
import com.yuyan.imemodule.view.preference.ManagedPreference


class AppPrefs(private val sharedPreferences: SharedPreferences) {

    inner class Internal : ManagedPreferenceInternal(sharedPreferences) {
        val pinyinModeRime = string("input_method_pinyin_mode_rime", CustomConstant.SCHEMA_ZH_T9) //拼音输入模式记录，记录引擎
        val inputDefaultMode = int("input_default_method_mode", InputModeSwitcherManager.MODE_T9_CHINESE)   //默认输入法类型
        val inputMethodPinyinMode = int("input_method_pinyin_mode", InputModeSwitcherManager.MODE_T9_CHINESE)  // 保存中文输入法类型
        val dataDictVersion = int("rime_dict_data_version", 0)  //缓存rime词库版本号,用于校验是否覆盖词库文件
        val loginStatue = bool("login_statue", false)   // 是否登陆
        val openCandidatesEncryBtn = bool("open_candidates_encry_btn", false)     // 是否添加候选词加密按键
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
    }

    inner class Advanced : ManagedPreferenceCategory(R.string.setting_ime_input, sharedPreferences) {

        val titleChinese = category(R.string.chinese_input_setting)

        val chineseAssociation = switch(R.string.chinese_association, "chinese_association_enable", true)
        val spaceSelectAssociation = switch(
            R.string.space_lenovo_candidate, "space_chinese_association_enable", true,
        ) { chineseAssociation.getValue() }
        val chineseRecovery = switch(
            R.string.setting_recovery, "chinese_recovery_enable", false
        )
        val chineseFanTi = switch(
            R.string.setting_jian_fan, "chinese_jian_fan_enable", false
        )
        val titleEnglish = category(R.string.EnglishInput)
        val abcFirstCapital = switch(
            R.string.first_capital, "first_capital_enable", false
        )
        val abcKeyboardNumber = switch(
            R.string.engish_full_keyboard, "abc_keyboard_number_enable", false
        )

        val titleEmoji = category(R.string.emoji_setting)
        val emojiInput = switch(
            R.string.emoji_input, "emoji_input_enable", false
        )
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

        val handWritingRCMode =
            list(
                R.string.hand_writing_rc_mode,
                "hand_writing_rc_mode",
                WritingRCMode.SENTENCE,
                WritingRCMode,
                listOf(
                    WritingRCMode.SENTENCE,
                    WritingRCMode.OVERLAP,
                    WritingRCMode.SENTENCE_OVERLAP
                ),
                listOf(
                    R.string.hand_writing_rc_mode_sentence,
                    R.string.hand_writing_rc_mode_overlap,
                    R.string.hand_writing_rc_mode_sentence_overlap
                )
            )
    }

    inner class Input : ManagedPreferenceCategory(R.string.setting_ime_input, sharedPreferences) {

        val titleChinese = category(R.string.chinese_input_setting)

        val chineseFanTi = switch(
            R.string.setting_jian_fan, "chinese_jian_fan_enable", false
        )
        val titleEnglish = category(R.string.EnglishInput)

        val abcSpaceAuto = switch(
            R.string.space_auto, "abc_space_auto_enable", false
        )

        val titleEmoji = category(R.string.emoji_setting)
        val emojiInput = switch(
            R.string.emoji_input, "emoji_input_enable", false
        )
    }

    inner class Keyboard : ManagedPreferenceCategory(R.string.keyboard_feedback, sharedPreferences) {

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
        val buttonPressVibrationMilliseconds: ManagedPreference.PInt
        val buttonLongPressVibrationMilliseconds: ManagedPreference.PInt

        init {
            val (primary, secondary) = twinInt(
                R.string.button_vibration_milliseconds,
                R.string.button_press,
                "button_vibration_press_milliseconds",
                0,
                R.string.button_long_press,
                "button_vibration_long_press_milliseconds",
                0,
                0,
                100,
                "毫秒",
                defaultLabel = R.string.system_default
            ) { hapticOnKeyPress.getValue() != InputFeedbackMode.Disabled }
            buttonPressVibrationMilliseconds = primary
            buttonLongPressVibrationMilliseconds = secondary
        }

        val buttonPressVibrationAmplitude: ManagedPreference.PInt
        val buttonLongPressVibrationAmplitude: ManagedPreference.PInt

        init {
            val (primary, secondary) = twinInt(
                R.string.button_vibration_amplitude,
                R.string.button_press,
                "button_vibration_press_amplitude",
                0,
                R.string.button_long_press,
                "button_vibration_long_press_amplitude",
                0,
                0,
                255,
                defaultLabel = R.string.system_default
            ) {
                (hapticOnKeyPress.getValue() != InputFeedbackMode.Disabled)
                        // hide this if using default duration
                        && (buttonPressVibrationMilliseconds.getValue() != 0 || buttonLongPressVibrationMilliseconds.getValue() != 0)
                        && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && ImeSdkApplication.context.vibrator.hasAmplitudeControl())
            }
            buttonPressVibrationAmplitude = primary
            buttonLongPressVibrationAmplitude = secondary
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
    val advanced = Advanced().register()
    val voice = Voice().register()
    val handwriting = Handwriting().register()
    val input = Input().register()
    val keyboard = Keyboard().register()
    val clipboard = Clipboard().register()

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

            listOf(
                internal.pinyinModeRime,
                internal.inputMethodPinyinMode,
                internal.dataDictVersion,
                internal.loginStatue,
                internal.openCandidatesEncryBtn,
                internal.keyboardHeightRatio
            ).forEach {
                it.putValueTo(this@edit)
            }

            advanced.managedPreferences.forEach {
                it.value.putValueTo(this@edit)
            }
            voice.managedPreferences.forEach {
                it.value.putValueTo(this@edit)
            }
            handwriting.managedPreferences.forEach {
                it.value.putValueTo(this@edit)
                HdManager.instance?.hciHwrRelease()
            }
            input.managedPreferences.forEach {
                it.value.putValueTo(this@edit)
            }
            keyboard.managedPreferences.forEach {
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