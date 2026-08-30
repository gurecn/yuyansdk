package com.yuyan.imemodule.ui.fragment

import android.content.ComponentName
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceScreen
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.Launcher
import com.yuyan.imemodule.manager.UserDataManager
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.rime.sync.RimeSyncException
import com.yuyan.imemodule.rime.sync.RimeSyncManager
import com.yuyan.imemodule.rime.sync.RimeSyncStateStore
import com.yuyan.imemodule.rime.sync.RimeSyncStorageBridge
import com.yuyan.imemodule.ui.activity.LauncherActivity
import com.yuyan.imemodule.ui.fragment.base.ManagedPreferenceFragment
import com.yuyan.imemodule.utils.AppUtil
import com.yuyan.imemodule.utils.addPreference
import com.yuyan.imemodule.utils.importErrorDialog
import com.yuyan.imemodule.utils.queryFileName
import com.yuyan.imemodule.utils.TimeUtils
import com.yuyan.imemodule.view.preference.ManagedPreference
import com.yuyan.imemodule.view.widget.withLoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable

private val imeHideIcon = AppPrefs.getInstance().other.imeHideIcon

private val switchKeyListener = ManagedPreference.OnChangeListener<Boolean> { _, value ->
    val componentName = ComponentName(Launcher.instance.context.packageName, LauncherActivity::class.java.name)
    Launcher.instance.context.packageManager.setComponentEnabledSetting(componentName, if(value) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
}

class OtherSettingsFragment: ManagedPreferenceFragment(AppPrefs.getInstance().other){

    private var exportTimestamp = System.currentTimeMillis()
    private lateinit var exportLauncher: ActivityResultLauncher<String>
    private lateinit var importLauncher: ActivityResultLauncher<String>
    private lateinit var syncDirLauncher: ActivityResultLauncher<Uri?>
    private var syncDirPreference: Preference? = null
    private var syncNowPreference: Preference? = null

    override fun onStart() {
        super.onStart()
        imeHideIcon.registerOnChangeListener(switchKeyListener)
    }

    override fun onStop() {
        super.onStop()
        imeHideIcon.unregisterOnChangeListener(switchKeyListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        importLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri == null) return@registerForActivityResult
                val ctx = requireContext()
                val cr = ctx.contentResolver
                lifecycleScope.withLoadingDialog(ctx) {
                    withContext(NonCancellable + Dispatchers.IO) {
                        val name = cr.queryFileName(uri) ?: return@withContext
                        if (!name.endsWith(".zip")) {
                            ctx.importErrorDialog(R.string.exception_user_data_filename, name)
                            return@withContext
                        }
                        try {
                            val inputStream = cr.openInputStream(uri)!!
                            UserDataManager.import(inputStream).getOrThrow()
                            lifecycleScope.launch(NonCancellable + Dispatchers.Main) {
                                delay(400L)
                                AppUtil.exit()
                            }
                            withContext(Dispatchers.Main) {
                                AppUtil.showRestartNotification(ctx)
                                Toast.makeText(ctx, R.string.user_data_imported, Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            ctx.importErrorDialog(e)
                        }
                    }
                }
            }
        exportLauncher =
            registerForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri ->
                if (uri == null) return@registerForActivityResult
                val ctx = requireContext()
                lifecycleScope.withLoadingDialog(requireContext()) {
                    withContext(NonCancellable + Dispatchers.IO) {
                        try {
                            val outputStream = ctx.contentResolver.openOutputStream(uri)!!
                            UserDataManager.export(outputStream).getOrThrow()
                        } catch (e: Exception) {
                            ctx.importErrorDialog(e)
                        }
                    }
                }
            }
        syncDirLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
                if (uri == null) return@registerForActivityResult
                val ctx = requireContext()
                try {
                    RimeSyncStorageBridge(ctx).persistTreePermission(uri)
                } catch (e: SecurityException) {
                    Toast.makeText(ctx, R.string.rime_sync_dir_invalid, Toast.LENGTH_LONG).show()
                    return@registerForActivityResult
                }
                RimeSyncStateStore(ctx).setTreeUri(uri)
                refreshSyncPreferences()
            }
    }

    override fun onPreferenceUiCreated(screen: PreferenceScreen) {
        val ctx = requireContext()
        val rimeSyncCategory = PreferenceCategory(ctx).apply {
            title = getString(R.string.rime_sync_category)
        }
        screen.addPreference(rimeSyncCategory)

        syncDirPreference = Preference(ctx).apply {
            title = getString(R.string.rime_sync_dir_pick)
            isSingleLineTitle = false
            isCopyingEnabled = true
            setOnPreferenceClickListener {
                AlertDialog.Builder(ctx)
                    .setTitle(R.string.rime_sync_dir_pick)
                    .setMessage(R.string.rime_sync_pick_hint)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        syncDirLauncher.launch(null)
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
                true
            }
        }
        rimeSyncCategory.addPreference(syncDirPreference!!)

        syncNowPreference = Preference(ctx).apply {
            title = getString(R.string.rime_sync_now)
            isSingleLineTitle = false
            isCopyingEnabled = true
            setOnPreferenceClickListener {
                lifecycleScope.withLoadingDialog(ctx, R.string.rime_sync_now) {
                    val result = RimeSyncManager.synchronize()
                    refreshSyncPreferences()
                    result.onSuccess {
                        Toast.makeText(ctx, R.string.rime_sync_success, Toast.LENGTH_SHORT).show()
                    }.onFailure { e ->
                        Toast.makeText(
                            ctx,
                            ctx.getString(R.string.rime_sync_failed) + ": " + syncErrorMessage(e),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                true
            }
        }
        rimeSyncCategory.addPreference(syncNowPreference!!)

        val syncClearPreference = Preference(ctx).apply {
            title = getString(R.string.rime_sync_clear)
            isSingleLineTitle = false
            isCopyingEnabled = true
            setOnPreferenceClickListener {
                AlertDialog.Builder(ctx)
                    .setTitle(R.string.rime_sync_clear)
                    .setMessage(R.string.rime_sync_clear_confirm)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        val store = RimeSyncStateStore(ctx)
                        store.getTreeUri()?.let {
                            RimeSyncStorageBridge(ctx).releaseTreePermission(it)
                        }
                        store.setTreeUri(null)
                        refreshSyncPreferences()
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
                true
            }
        }
        rimeSyncCategory.addPreference(syncClearPreference)

        screen.addPreference(R.string.export_user_data) {
            lifecycleScope.launch {
                exportTimestamp = System.currentTimeMillis()
                exportLauncher.launch("yuyanIme_${TimeUtils.iso8601UTCDateTime(exportTimestamp)}.zip")
            }
        }
        screen.addPreference(R.string.import_user_data) {
            AlertDialog.Builder(ctx)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.import_user_data)
                .setMessage(R.string.confirm_import_user_data)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    importLauncher.launch("application/zip")
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }

        refreshSyncPreferences()
    }

    private fun refreshSyncPreferences() {
        val ctx = requireContext()
        val state = RimeSyncStateStore(ctx).load()
        val treeUri = RimeSyncStateStore(ctx).getTreeUri()
        syncDirPreference?.summary = when {
            treeUri == null -> ctx.getString(R.string.rime_sync_dir_not_set)
            !RimeSyncManager.hasValidTreePermission() ->
                ctx.getString(R.string.rime_sync_dir_invalid)
            else -> treeUri.toString()
        }
        syncNowPreference?.summary = when {
            state != null && state.lastError != null ->
                ctx.getString(R.string.rime_sync_last_error, state.lastError)
            state == null || state.lastSuccessTime == 0L ->
                ctx.getString(R.string.rime_sync_never)
            else ->
                ctx.getString(
                    R.string.rime_sync_last_success,
                    TimeUtils.iso8601UTCDateTime(state.lastSuccessTime)
                )
        }
    }

    /** UI 不解析 Exception.message 判断类型，只按异常类型分支。 */
    private fun syncErrorMessage(e: Throwable): String = when (e) {
        is RimeSyncException.SyncDirectoryNotConfigured ->
            getString(R.string.rime_sync_dir_not_set)
        is RimeSyncException.SyncDirectoryPermissionLost ->
            getString(R.string.rime_sync_dir_invalid)
        is RimeSyncException.SyncDirectoryReadOnly ->
            getString(R.string.rime_sync_read_only)
        is RimeSyncException.NativeSyncUnavailable ->
            getString(R.string.rime_sync_native_unavailable)
        is RimeSyncException.NativeSyncFailed ->
            getString(R.string.rime_sync_native_failed)
        is RimeSyncException.ExternalReadFailed ->
            getString(R.string.rime_sync_read_failed)
        is RimeSyncException.ExternalWriteFailed ->
            getString(R.string.rime_sync_write_failed)
        else -> e.message ?: e.javaClass.simpleName
    }
}
