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
import com.yuyan.imemodule.constant.CustomConstant
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
                    R.drawable.ic_baseline_language_24,
                    R.id.action_settingsFragment_to_inputSettingsFragment
                )
            }
            addCategory(R.string.keyboard) {
                isIconSpaceReserved = false
                addDestinationPreference(
                    R.string.theme,
                    R.drawable.baseline_theme_lens_24,
                    R.id.action_settingsFragment_to_themeSettingsFragment
                )
                addDestinationPreference(
                    R.string.keyboard_feedback,
                    R.drawable.sdk_vector_menu_skb_touch,
                    R.id.action_settingsFragment_to_keyboardFeedbackFragment
                )
                addDestinationPreference(
                    R.string.setting_ime_keyboard,
                    R.drawable.ic_baseline_keyboard_24,
                    R.id.action_settingsFragment_to_keyboardSettingFragment
                )
                addDestinationPreference(
                    R.string.clipboard,
                    R.drawable.ic_clipboard,
                    R.id.action_settingsFragment_to_clipboardSettingsFragment
                )
            }
            addCategory(R.string.advanced) {
                isIconSpaceReserved = false

                addPreference(R.string.feedback,"",
                    R.drawable.baseline_feedback_24,) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(CustomConstant.FEEDBACK_TXC_REPO)))
                }
                addDestinationPreference(
                    R.string.about,
                    R.drawable.ic_baseline_more_horiz_24,
                    R.id.action_settingsFragment_to_aboutFragment
                )
            }
        }
    }
}