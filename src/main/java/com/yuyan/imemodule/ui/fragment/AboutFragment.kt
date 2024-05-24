package com.yuyan.imemodule.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import com.yuyan.imemodule.R
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.ui.utils.addCategory
import com.yuyan.imemodule.ui.utils.addPreference
import java.text.SimpleDateFormat
import java.util.Date

class AboutFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceScreen = preferenceManager.createPreferenceScreen(requireContext()).apply {
            addPreference(R.string.privacy_policy) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(CustomConstant.privacyPolicyUrl)))
            }
            addPreference(
                R.string.open_source_licenses,
                R.string.licenses_of_third_party_libraries
            ) {
                findNavController().navigate(R.id.action_aboutFragment_to_licensesFragment)
            }
            addPreference(R.string.source_code, R.string.github_repo) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(CustomConstant.githubRepo)))
            }
            addPreference(R.string.license, CustomConstant.licenseSpdxId) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(CustomConstant.licenseUrl)))
            }
            addCategory(R.string.version) {
                isIconSpaceReserved = false
                addPreference(R.string.current_version, "a8661f8e-debug")
                addPreference(R.string.build_git_hash, "a8661f8e21e0a0f45da853571a3a8c6a75399b8b") {
                    val commit = "a8661f8e21e0a0f45da853571a3a8c6a75399b8b".substringBefore('-')
                    val uri = Uri.parse("${CustomConstant.githubRepo}/commit/${commit}")
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                addPreference(R.string.build_time, SimpleDateFormat.getDateTimeInstance().format(
                    Date(1709106772785)
                ))
            }
        }


    }
}


