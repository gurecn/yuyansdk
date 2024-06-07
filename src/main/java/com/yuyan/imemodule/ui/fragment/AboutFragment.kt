package com.yuyan.imemodule.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import com.yuyan.imemodule.BuildConfig
import com.yuyan.imemodule.R
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.ui.utils.addCategory
import com.yuyan.imemodule.ui.utils.addPreference

class AboutFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceScreen = preferenceManager.createPreferenceScreen(requireContext()).apply {
            addPreference(R.string.privacy_policy) {
                findNavController().navigate(R.id.action_aboutFragment_to_privacyPolicyFragment)
            }
            addPreference(
                R.string.open_source_licenses,
                R.string.licenses_of_third_party_libraries
            ) {
                findNavController().navigate(R.id.action_aboutFragment_to_licensesFragment)
            }
            addPreference(R.string.source_code, R.string.github_repo) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(CustomConstant.YuyanIMERepo)))
            }
            addPreference(R.string.license, CustomConstant.licenseSpdxId) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(CustomConstant.licenseUrl)))
            }
            addCategory(R.string.version) {
                isIconSpaceReserved = false
                addPreference(R.string.app_version, BuildConfig.AppCommitId)
                addPreference(R.string.app_build_git_hash, BuildConfig.AppCommitHead) {
                    val commit = BuildConfig.AppCommitHead.substringBefore('-')
                    val uri = Uri.parse("${CustomConstant.YuyanSDKRepo}/commit/${commit}")
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                addPreference(R.string.app_build_time, BuildConfig.AppBuildTime)
                addPreference(R.string.sdk_version, BuildConfig.SdkCommitId)
            }
        }


    }
}


