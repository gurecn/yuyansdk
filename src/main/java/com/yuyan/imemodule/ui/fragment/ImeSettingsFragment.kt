package com.yuyan.imemodule.ui.fragment

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.yuyan.imemodule.R
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
                    R.string.keyboard,
                    R.drawable.ic_baseline_keyboard_24,
                    R.id.action_settingsFragment_to_themeSettingsFragment
                )
                addDestinationPreference(
                    R.string.keyboard_feedback,
                    R.drawable.sdk_vector_menu_skb_touch,
                    R.id.action_settingsFragment_to_keyboardSettingsFragment
                )
                addDestinationPreference(
                    R.string.clipboard,
                    R.drawable.ic_clipboard,
                    R.id.action_settingsFragment_to_clipboardSettingsFragment
                )
            }
            addCategory(R.string.advanced) {
                isIconSpaceReserved = false
                addDestinationPreference(
                    R.string.about,
                    R.drawable.ic_baseline_more_horiz_24,
                    R.id.action_settingsFragment_to_aboutFragment
                )
            }
        }
    }
}