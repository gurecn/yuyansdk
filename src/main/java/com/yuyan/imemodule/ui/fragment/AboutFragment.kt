package com.yuyan.imemodule.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import com.yuyan.imemodule.BuildConfig
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.imemodule.utils.addCategory
import com.yuyan.imemodule.utils.addPreference

class AboutFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceScreen = preferenceManager.createPreferenceScreen(requireContext()).apply {
            addPreference(R.string.privacy_policy) {
                findNavController().navigate(R.id.action_aboutFragment_to_privacyPolicyFragment)
            }
            addPreference(R.string.source_code, R.string.github_repo) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(CustomConstant.YUYAN_IME_REPO)))
            }
            addPreference(R.string.license, "GPL-3.0 license ") {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(CustomConstant.LICENSE_URL)))
            }
            addCategory(R.string.app_version) {
                isIconSpaceReserved = false
                addPreference(R.string.version, BuildConfig.versionName){
                    val uri = Uri.parse("${CustomConstant.YUYAN_IME_REPO}/releases/latest")
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                addPreference(R.string.build_git_hash, BuildConfig.AppCommitHead) {
                    val commit = BuildConfig.AppCommitHead.substringBefore('-')
                    val uri = Uri.parse("${CustomConstant.YUYAN_IME_REPO}/commit/${commit}")
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                addPreference(R.string.build_time, BuildConfig.AppBuildTime)

            }
//            addCategory(R.string.sdk_version) {
//                isIconSpaceReserved = false
//                addPreference(R.string.build_type, if(BuildConfig.offline)R.string.build_type_offline else R.string.build_type_online )
//            }
        }


    }
}


