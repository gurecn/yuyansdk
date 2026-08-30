package com.yuyan.imemodule.rime.sync

import android.content.Context
import android.net.Uri
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID

/**
 * Rime 同步的设备本地状态。
 */
@Serializable
data class RimeSyncDeviceState(
    val installationId: String,
    val syncTreeUri: String? = null,
    val syncMode: String = RIME_SYNC_MODE_SAF,
    val webDavConsentGranted: Boolean = false,
    val webDavUrl: String? = null,
    val webDavUsername: String? = null,
    val webDavPassword: String? = null,
    val syncIntervalHours: Int = 0,
    val retentionDays: Int = 0,
    val lastSuccessTime: Long = 0L,
    val lastError: String? = null
)

/**
 * 保存 Rime 同步相关状态，文件位于 noBackupFilesDir/rime-sync-state.json。
 *
 * 故意不使用默认 SharedPreferences：
 * UserDataManager.export() 会把 shared_prefs 打进 ZIP，
 * 若 installation id 放进默认 Preferences，整包备份恢复到另一台设备后
 * 会克隆出相同的 installation id，再次造成同步目录冲突。
 */
class RimeSyncStateStore(
    private val context: Context
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val stateFile: File
        get() = File(context.noBackupFilesDir, STATE_FILE_NAME)

    fun load(): RimeSyncDeviceState? {
        val file = stateFile
        if (!file.exists()) return null
        return try {
            json.decodeFromString<RimeSyncDeviceState>(file.readText())
        } catch (_: Exception) {
            null
        }
    }

    fun loadOrCreate(): RimeSyncDeviceState {
        load()?.let { return it }
        return RimeSyncDeviceState(installationId = UUID.randomUUID().toString())
            .also { save(it) }
    }

    fun update(transform: (RimeSyncDeviceState) -> RimeSyncDeviceState) {
        save(transform(loadOrCreate()))
    }

    fun setTreeUri(uri: Uri?) {
        update { it.copy(syncTreeUri = uri?.toString()) }
    }

    fun getTreeUri(): Uri? {
        val raw = load()?.syncTreeUri ?: return null
        return try {
            Uri.parse(raw)
        } catch (_: Exception) {
            null
        }
    }

    fun updateSuccess(timestamp: Long) {
        update { it.copy(lastSuccessTime = timestamp, lastError = null) }
    }

    fun updateError(message: String) {
        update { it.copy(lastError = message) }
    }

    fun setSyncMode(mode: String) {
        update { it.copy(syncMode = mode) }
    }

    fun setWebDavConsent(granted: Boolean) {
        update { it.copy(webDavConsentGranted = granted) }
    }

    fun setWebDavConfig(url: String?, username: String?, password: String?) {
        update {
            it.copy(
                webDavUrl = url?.trim()?.takeIf(String::isNotEmpty),
                webDavUsername = username?.trim()?.takeIf(String::isNotEmpty),
                webDavPassword = password
            )
        }
    }

    fun setSyncIntervalHours(hours: Int) {
        update { it.copy(syncIntervalHours = hours) }
    }

    fun setRetentionDays(days: Int) {
        update { it.copy(retentionDays = days) }
    }

    private fun save(state: RimeSyncDeviceState) {
        val file = stateFile
        file.parentFile?.mkdirs()
        val tmp = File(file.parentFile, file.name + ".tmp")
        tmp.writeText(json.encodeToString(RimeSyncDeviceState.serializer(), state))
        if (!tmp.renameTo(file)) {
            file.writeText(json.encodeToString(RimeSyncDeviceState.serializer(), state))
            tmp.delete()
        }
    }

    companion object {
        private const val STATE_FILE_NAME = "rime-sync-state.json"
    }
}
