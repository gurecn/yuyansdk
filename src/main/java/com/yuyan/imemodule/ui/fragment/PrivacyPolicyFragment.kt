
package com.yuyan.imemodule.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceFragmentCompat
import com.yuyan.imemodule.R
import com.yuyan.imemodule.ui.utils.addPreference
import com.mikepenz.aboutlibraries.entity.License
import kotlinx.coroutines.launch

class PrivacyPolicyFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        lifecycleScope.launch {
            preferenceScreen = preferenceManager.createPreferenceScreen(requireContext()).apply {
                val jsonString = resources.getString(R.string.privacy_policy_content)
                addPreference(
                    title = jsonString,
                )
            }
        }
    }
}