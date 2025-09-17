
package com.yuyan.imemodule.ui.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.yuyan.imemodule.R
import com.yuyan.imemodule.entity.PrivacyPolicy
import com.yuyan.imemodule.utils.addPreference
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class PrivacyPolicyFragment : PreferenceFragmentCompat() {

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.privacy_policy)
    }

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
                addPreference(Preference(context).apply {
                    layoutResource = R.layout.layout_privacy_policy
                    setOnPreferenceClickListener { preference -> true }
                })
            }
        }
    }
}