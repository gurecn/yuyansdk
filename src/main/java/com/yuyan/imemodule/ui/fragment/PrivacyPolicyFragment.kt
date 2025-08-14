
package com.yuyan.imemodule.ui.fragment

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceFragmentCompat
import com.yuyan.imemodule.R
import com.yuyan.imemodule.entity.PrivacyPolicy
import com.yuyan.imemodule.utils.addPreference
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class PrivacyPolicyFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        lifecycleScope.launch {
            preferenceScreen = preferenceManager.createPreferenceScreen(requireContext()).apply {
                val jsonString = resources.openRawResource(R.raw.privacypolicy)
                    .bufferedReader()
                    .use { it.readText() }
                Json.decodeFromString<List<PrivacyPolicy>>(jsonString).forEach {
                    addPreference(
                        title = it.name,
                        summary = it.description
                    )
                }
            }
        }
    }
}