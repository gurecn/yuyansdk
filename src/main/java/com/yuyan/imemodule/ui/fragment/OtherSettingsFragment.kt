package com.yuyan.imemodule.ui.fragment

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.text.InputType
import android.widget.EditText
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
import com.yuyan.imemodule.rime.sync.RIME_SYNC_MODE_SAF
import com.yuyan.imemodule.rime.sync.RIME_SYNC_MODE_WEBDAV
import com.yuyan.imemodule.rime.sync.RimeSyncException
import com.yuyan.imemodule.rime.sync.RimeSyncManager
import com.yuyan.imemodule.rime.sync.RimeSyncStateStore
import com.yuyan.imemodule.rime.sync.RimeSyncStorageBridge
import com.yuyan.imemodule.rime.sync.RimeSyncScheduler
import com.yuyan.imemodule.rime.sync.WebDavSyncConfig
import com.yuyan.imemodule.rime.sync.WebDavSyncTransport
import com.yuyan.imemodule.ui.activity.LauncherActivity
import com.yuyan.imemodule.ui.fragment.base.ManagedPreferenceFragment
import com.yuyan.imemodule.utils.AppUtil
import com.yuyan.imemodule.utils.DevicesUtils
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
    private var syncClearPreference: Preference? = null
    private var syncModePreference: Preference? = null
    private var webDavUrlPreference: Preference? = null
    private var webDavAccountPreference: Preference? = null
    private var webDavPasswordPreference: Preference? = null
    private var webDavTestPreference: Preference? = null
    private var syncIntervalPreference: Preference? = null
    private var retentionPreference: Preference? = null

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
            isIconSpaceReserved = false
        }
        screen.addPreference(rimeSyncCategory)

        syncDirPreference = Preference(ctx).apply {
            title = getString(R.string.rime_sync_dir_pick)
            isSingleLineTitle = false
            isIconSpaceReserved = false
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
            isIconSpaceReserved = false
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

        syncClearPreference = Preference(ctx).apply {
            title = getString(R.string.rime_sync_clear)
            isSingleLineTitle = false
            isIconSpaceReserved = false
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
        rimeSyncCategory.addPreference(syncClearPreference!!)

        syncModePreference = Preference(ctx).apply {
            title = getString(R.string.rime_sync_mode)
            isSingleLineTitle = false
            isIconSpaceReserved = false
            isCopyingEnabled = true
            setOnPreferenceClickListener {
                val modes = arrayOf(
                    getString(R.string.rime_sync_mode_saf),
                    getString(R.string.rime_sync_mode_webdav)
                )
                val store = RimeSyncStateStore(ctx)
                val previousMode = store.loadOrCreate().syncMode
                val currentWebDav = previousMode == RIME_SYNC_MODE_WEBDAV
                AlertDialog.Builder(ctx)
                    .setTitle(R.string.rime_sync_mode)
                    .setSingleChoiceItems(modes, if (currentWebDav) 1 else 0) { _, which ->
                        store.setSyncMode(
                            if (which == 1) RIME_SYNC_MODE_WEBDAV else RIME_SYNC_MODE_SAF
                        )
                    }
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        val currentState = RimeSyncStateStore(ctx).loadOrCreate()
                        val selectedWebDav =
                            currentState.syncMode == RIME_SYNC_MODE_WEBDAV
                        if (selectedWebDav && !currentState.webDavConsentGranted) {
                            AlertDialog.Builder(ctx)
                                .setTitle(R.string.rime_sync_webdav_consent_title)
                                .setMessage(R.string.rime_sync_webdav_consent_message)
                                .setPositiveButton(
                                    R.string.rime_sync_webdav_consent_agree
                                ) { _, _ ->
                                    RimeSyncStateStore(ctx).setWebDavConsent(true)
                                    rescheduleSync(ctx)
                                    refreshSyncPreferences()
                                }
                                .setNegativeButton(
                                    R.string.rime_sync_webdav_consent_reject
                                ) { _, _ ->
                                    RimeSyncStateStore(ctx).setSyncMode(previousMode)
                                    rescheduleSync(ctx)
                                    refreshSyncPreferences()
                                }
                                .show()
                        } else {
                            rescheduleSync(ctx)
                            refreshSyncPreferences()
                        }
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->
                        store.setSyncMode(previousMode)
                        rescheduleSync(ctx)
                        refreshSyncPreferences()
                    }
                    .show()
                true
            }
        }
        rimeSyncCategory.addPreference(syncModePreference!!)

        webDavUrlPreference = Preference(ctx).apply {
            title = getString(R.string.rime_sync_webdav_url)
            isSingleLineTitle = false
            isIconSpaceReserved = false
            isCopyingEnabled = true
            setOnPreferenceClickListener {
                showTextInputDialog(
                    ctx,
                    R.string.rime_sync_webdav_url,
                    initial = RimeSyncStateStore(ctx).loadOrCreate().webDavUrl.orEmpty(),
                    hint = getString(R.string.rime_sync_webdav_url_hint)
                ) { text ->
                    updateWebDavConfig(ctx, url = text)
                }
                true
            }
        }
        rimeSyncCategory.addPreference(webDavUrlPreference!!)

        webDavAccountPreference = Preference(ctx).apply {
            title = getString(R.string.rime_sync_webdav_account)
            isSingleLineTitle = false
            isIconSpaceReserved = false
            isCopyingEnabled = true
            setOnPreferenceClickListener {
                showTextInputDialog(
                    ctx,
                    R.string.rime_sync_webdav_account,
                    initial = RimeSyncStateStore(ctx).loadOrCreate().webDavUsername.orEmpty()
                ) { text ->
                    updateWebDavConfig(ctx, username = text)
                }
                true
            }
        }
        rimeSyncCategory.addPreference(webDavAccountPreference!!)

        webDavPasswordPreference = Preference(ctx).apply {
            title = getString(R.string.rime_sync_webdav_password)
            isSingleLineTitle = false
            isIconSpaceReserved = false
            isCopyingEnabled = true
            setOnPreferenceClickListener {
                showTextInputDialog(
                    ctx,
                    R.string.rime_sync_webdav_password,
                    initial = RimeSyncStateStore(ctx).loadOrCreate().webDavPassword.orEmpty(),
                    isPassword = true
                ) { text ->
                    updateWebDavConfig(ctx, password = text)
                }
                true
            }
        }
        rimeSyncCategory.addPreference(webDavPasswordPreference!!)

        webDavTestPreference = Preference(ctx).apply {
            title = getString(R.string.rime_sync_webdav_save_test)
            isSingleLineTitle = false
            isIconSpaceReserved = false
            isCopyingEnabled = true
            setOnPreferenceClickListener {
                val config = WebDavSyncConfig.from(RimeSyncStateStore(ctx).loadOrCreate())
                if (config == null) {
                    Toast.makeText(ctx, R.string.rime_sync_webdav_incomplete, Toast.LENGTH_LONG).show()
                } else {
                    lifecycleScope.withLoadingDialog(ctx, R.string.rime_sync_webdav_save_test) {
                        val result = WebDavSyncTransport(config).testConnection()
                        result.onSuccess {
                            Toast.makeText(ctx, R.string.rime_sync_webdav_test_ok, Toast.LENGTH_SHORT).show()
                        }.onFailure { e ->
                            Toast.makeText(
                                ctx,
                                ctx.getString(R.string.rime_sync_webdav_test_fail) + ": " + syncErrorMessage(e),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
                true
            }
        }
        rimeSyncCategory.addPreference(webDavTestPreference!!)

        syncIntervalPreference = Preference(ctx).apply {
            title = getString(R.string.rime_sync_interval)
            isSingleLineTitle = false
            isIconSpaceReserved = false
            isCopyingEnabled = true
            setOnPreferenceClickListener {
                val labels = arrayOf(
                    getString(R.string.rime_sync_interval_manual),
                    getString(R.string.rime_sync_interval_6h),
                    getString(R.string.rime_sync_interval_12h),
                    getString(R.string.rime_sync_interval_24h)
                )
                val values = intArrayOf(0, 6, 12, 24)
                AlertDialog.Builder(ctx)
                    .setTitle(R.string.rime_sync_interval)
                    .setItems(labels) { _, which ->
                        RimeSyncStateStore(ctx).setSyncIntervalHours(values[which])
                        rescheduleSync(ctx)
                        refreshSyncPreferences()
                    }
                    .show()
                true
            }
        }
        rimeSyncCategory.addPreference(syncIntervalPreference!!)

        retentionPreference = Preference(ctx).apply {
            title = getString(R.string.rime_sync_retention)
            isSingleLineTitle = false
            isIconSpaceReserved = false
            isCopyingEnabled = true
            setOnPreferenceClickListener {
                val values = intArrayOf(0, 7, 30, 90, 180)
                val labels = values.map {
                    if (it <= 0) getString(R.string.rime_sync_retention_off)
                    else getString(R.string.rime_sync_retention_days, it)
                }.toTypedArray()
                AlertDialog.Builder(ctx)
                    .setTitle(R.string.rime_sync_retention)
                    .setItems(labels) { _, which ->
                        RimeSyncStateStore(ctx).setRetentionDays(values[which])
                        refreshSyncPreferences()
                    }
                    .show()
                true
            }
        }
        rimeSyncCategory.addPreference(retentionPreference!!)

        val backupCategory = PreferenceCategory(ctx).apply {
            title = getString(R.string.user_data_backup_category)
            isIconSpaceReserved = false
        }
        screen.addPreference(backupCategory)

        backupCategory.addPreference(R.string.export_user_data) {
            lifecycleScope.launch {
                exportTimestamp = System.currentTimeMillis()
                exportLauncher.launch("yuyanIme_${TimeUtils.iso8601UTCDateTime(exportTimestamp)}.zip")
            }
        }
        backupCategory.addPreference(R.string.import_user_data) {
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
        val mode = state?.syncMode ?: RIME_SYNC_MODE_SAF
        val isWebDav = mode == RIME_SYNC_MODE_WEBDAV
        syncModePreference?.summary = ctx.getString(
            if (isWebDav) R.string.rime_sync_mode_webdav else R.string.rime_sync_mode_saf
        )
        syncDirPreference?.isVisible = !isWebDav
        syncClearPreference?.isVisible = !isWebDav
        webDavUrlPreference?.isVisible = isWebDav
        webDavAccountPreference?.isVisible = isWebDav
        webDavPasswordPreference?.isVisible = isWebDav
        webDavTestPreference?.isVisible = isWebDav
        syncIntervalPreference?.isVisible = isWebDav
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
        webDavUrlPreference?.summary = state?.webDavUrl?.takeIf(String::isNotEmpty)
            ?: ctx.getString(R.string.rime_sync_webdav_not_set)
        webDavAccountPreference?.summary = state?.webDavUsername?.takeIf(String::isNotEmpty)
            ?: ctx.getString(R.string.rime_sync_webdav_not_set)
        webDavPasswordPreference?.summary = if (state?.webDavPassword.isNullOrEmpty()) {
            ctx.getString(R.string.rime_sync_webdav_password_unset)
        } else {
            ctx.getString(R.string.rime_sync_webdav_password_set)
        }
        syncIntervalPreference?.summary = intervalLabel(ctx, state?.syncIntervalHours ?: 0)
        retentionPreference?.summary = retentionLabel(ctx, state?.retentionDays ?: 0)
    }

    private fun updateWebDavConfig(
        ctx: Context,
        url: String? = null,
        username: String? = null,
        password: String? = null
    ) {
        val store = RimeSyncStateStore(ctx)
        val state = store.loadOrCreate()
        store.setWebDavConfig(
            url ?: state.webDavUrl,
            username ?: state.webDavUsername,
            password ?: state.webDavPassword
        )
        refreshSyncPreferences()
    }

    private fun rescheduleSync(ctx: Context) {
        val state = RimeSyncStateStore(ctx).loadOrCreate()
        val hours = if (state.syncMode == RIME_SYNC_MODE_WEBDAV) {
            state.syncIntervalHours
        } else {
            0
        }
        RimeSyncScheduler.schedule(ctx, hours)
    }

    private fun intervalLabel(ctx: Context, hours: Int): String = when (hours) {
        6 -> ctx.getString(R.string.rime_sync_interval_6h)
        12 -> ctx.getString(R.string.rime_sync_interval_12h)
        24 -> ctx.getString(R.string.rime_sync_interval_24h)
        else -> ctx.getString(R.string.rime_sync_interval_manual)
    }

    private fun retentionLabel(ctx: Context, days: Int): String =
        if (days <= 0) ctx.getString(R.string.rime_sync_retention_off)
        else ctx.getString(R.string.rime_sync_retention_days, days)

    private fun showTextInputDialog(
        ctx: Context,
        title: Int,
        initial: String,
        hint: String? = null,
        isPassword: Boolean = false,
        onInput: (String) -> Unit
    ) {
        val editText = EditText(ctx).apply {
            setText(initial)
            if (hint != null) this.hint = hint
            inputType = if (isPassword) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
            }
            isSingleLine = true
            setSelection(text.length)
        }
        val horizontalPadding = DevicesUtils.dip2px(24)
        val verticalPadding = DevicesUtils.dip2px(12)
        editText.setPadding(
            horizontalPadding,
            verticalPadding,
            horizontalPadding,
            verticalPadding
        )
        AlertDialog.Builder(ctx)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton(android.R.string.ok) { _, _ -> onInput(editText.text.toString()) }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
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
        is RimeSyncException.WebDavNotConfigured ->
            getString(R.string.rime_sync_webdav_incomplete)
        is RimeSyncException.WebDavConsentRequired ->
            getString(R.string.rime_sync_webdav_consent_required)
        is RimeSyncException.WebDavAuthFailed ->
            getString(R.string.rime_sync_webdav_auth_failed)
        is RimeSyncException.WebDavNetworkFailed ->
            e.message ?: getString(R.string.rime_sync_webdav_network_failed)
        is RimeSyncException.WebDavRemoteFailed ->
            e.message ?: getString(R.string.rime_sync_webdav_remote_failed)
        else -> e.message ?: e.javaClass.simpleName
    }
}
