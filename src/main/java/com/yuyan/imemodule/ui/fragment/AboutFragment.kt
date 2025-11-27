package com.yuyan.imemodule.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import com.yuyan.imemodule.BuildConfig
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.imemodule.application.Launcher
import com.yuyan.imemodule.utils.TimeUtils
import com.yuyan.imemodule.utils.addCategory
import com.yuyan.imemodule.utils.addPreference
import com.yuyan.imemodule.view.widget.withLoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class AboutFragment : PreferenceFragmentCompat() {
    private var exportTimestamp = System.currentTimeMillis()
    private lateinit var exportLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exportLauncher =
            registerForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri ->
                if (uri == null) return@registerForActivityResult
                val ctx = requireContext()
                lifecycleScope.withLoadingDialog(requireContext()) {
                    withContext(NonCancellable + Dispatchers.IO) {
                        val outputStream = ctx.contentResolver.openOutputStream(uri)!!
                        export(outputStream).getOrThrow()
                    }
                }
            }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun export(dest: OutputStream) = runCatching {
        val outputDir = File(Launcher.instance.context.filesDir, "crash_logs")
        ZipOutputStream(dest.buffered()).use { zipStream ->
            writeFileTree(outputDir, zipStream)
            zipStream.closeEntry()
        }
    }

    private fun writeFileTree(srcDir: File, dest: ZipOutputStream) {
        srcDir.walkTopDown().forEach { f ->
            val related = f.relativeTo(srcDir)
            if (related.path != "") {
                if (f.isDirectory) {
                    dest.putNextEntry(ZipEntry("${related.path}/"))
                } else if (f.isFile) {
                    dest.putNextEntry(ZipEntry("${related.path}"))
                    f.inputStream().use { it.copyTo(dest) }
                }
            }
        }
    }

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

            addCategory(R.string.app_log) {
                isIconSpaceReserved = false
                addPreference(R.string.export_crash_log) {
                    lifecycleScope.launch {
                        exportTimestamp = System.currentTimeMillis()
                        exportLauncher.launch("yuyanIme_crash_log${TimeUtils.iso8601UTCDateTime(exportTimestamp)}.zip")
                    }
                }
            }
        }


    }
}


