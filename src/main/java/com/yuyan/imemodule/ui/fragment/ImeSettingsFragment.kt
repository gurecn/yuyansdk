package com.yuyan.imemodule.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.yuyan.imemodule.BuildConfig
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.imemodule.utils.addCategory
import com.yuyan.imemodule.utils.addPreference
import androidx.core.net.toUri
import com.yuyan.imemodule.ui.setup.SetupActivity

class ImeSettingsFragment : PreferenceFragmentCompat() {

    private fun PreferenceCategory.addDestinationPreference(
        @StringRes title: Int,
        @DrawableRes icon: Int,
        @IdRes destination: Int
    ) {
        addPreference(title, icon = icon) {
            findNavController().navigate(destination)
        }
    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceScreen = preferenceManager.createPreferenceScreen(requireContext()).apply {
            addCategory(R.string.input_methods) {
                isIconSpaceReserved = false

                if (SetupActivity.shouldShowUp()) {
                    addDestinationPreference(
                        R.string.enable_ime,
                        R.drawable.ic_menu_language,
                        R.id.action_settingsFragment_to_setupActivity
                    )
                }
                addDestinationPreference(
                    R.string.setting_ime_input,
                    R.drawable.ic_menu_language,
                    R.id.action_settingsFragment_to_inputSettingsFragment
                )
                addDestinationPreference(
                    R.string.ime_settings_handwriting,
                    R.drawable.ic_menu_handwriting,
                    R.id.action_settingsFragment_to_handwritingSettingsFragment
                )
            }
            addCategory(R.string.keyboard) {
                isIconSpaceReserved = false
                addDestinationPreference(
                    R.string.theme,
                    R.drawable.ic_menu_theme,
                    R.id.action_settingsFragment_to_themeSettingsFragment
                )
                addDestinationPreference(
                    R.string.keyboard_feedback,
                    R.drawable.ic_menu_touch,
                    R.id.action_settingsFragment_to_keyboardFeedbackFragment
                )
                addDestinationPreference(
                    R.string.setting_ime_keyboard,
                    R.drawable.ic_menu_keyboard,
                    R.id.action_settingsFragment_to_keyboardSettingFragment
                )
                addDestinationPreference(
                    R.string.clipboard,
                    R.drawable.ic_menu_clipboard,
                    R.id.action_settingsFragment_to_clipboardSettingsFragment
                )

                addDestinationPreference(
                    R.string.full_display_keyboard,
                    R.drawable.ic_menu_keyboard_full,
                    R.id.action_settingsFragment_to_fullDisplayKeyboardFragment
                )
            }
            addCategory(R.string.advanced) {
                isIconSpaceReserved = false
                addDestinationPreference(
                    R.string.setting_ime_other,
                    R.drawable.ic_menu_more_horiz,
                    R.id.action_settingsFragment_to_otherSettingsFragment
                )
                addPreference(R.string.feedback,"",
                    R.drawable.ic_menu_edit,) {
                    startActivity(Intent(Intent.ACTION_VIEW, CustomConstant.FEEDBACK_TXC_REPO.toUri()))
                }
                addDestinationPreference(
                    R.string.about,
                    R.drawable.ic_menu_feedback,
                    R.id.action_settingsFragment_to_aboutFragment
                )
            }
        }
    }
}