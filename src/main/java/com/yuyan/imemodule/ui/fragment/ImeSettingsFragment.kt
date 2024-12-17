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
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.imemodule.ui.utils.addCategory
import com.yuyan.imemodule.ui.utils.addPreference

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
                    R.drawable.baseline_keyboard_full_24,
                    R.id.action_settingsFragment_to_fullDisplayKeyboardFragment
                )
            }
            addCategory(R.string.advanced) {
                isIconSpaceReserved = false

                addPreference(R.string.feedback,"",
                    R.drawable.ic_menu_feedback,) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(CustomConstant.FEEDBACK_TXC_REPO)))
                }
                addDestinationPreference(
                    R.string.about,
                    R.drawable.ic_menu_more_horiz,
                    R.id.action_settingsFragment_to_aboutFragment
                )
            }
        }
    }
}