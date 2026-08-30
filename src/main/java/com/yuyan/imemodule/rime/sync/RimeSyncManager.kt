package com.yuyan.imemodule.rime.sync

import android.net.Uri
import com.yuyan.imemodule.application.Launcher
import com.yuyan.inputmethod.RimeEngine
import com.yuyan.inputmethod.core.Kernel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Rime 用户数据同步编排。
 *
 * UserDataManager = 语燕整应用备份；
 * RimeSyncManager = 跨 Rime 客户端用户词典同步。
 * 两个功能不能复用，不要把 synchronize() 塞进 export()/import()。
 */
object RimeSyncManager {

    private val syncMutex = Mutex()
    private val engine: RimeSyncEngine = NativeRimeSyncEngine()

    private fun store() = RimeSyncStateStore(Launcher.instance.context)
    private fun bridge() = RimeSyncStorageBridge(Launcher.instance.context)

    /** 当前设备同步状态（设置 UI 展示用）。 */
    fun currentState(): RimeSyncDeviceState? = store().load()

    /** 启动时检查 SAF grant 是否仍有效（读 + 写）。 */
    fun hasValidTreePermission(): Boolean {
        val uri = store().getTreeUri() ?: return false
        return bridge().hasValidTreePermission(uri)
    }

    /**
     * 完整同步时序，所有同步操作串行化：
     *
     * Mutex lock
     *   → 读取 RimeSyncState
     *   → 按 syncMode 选择通道（SAF / WebDAV）并验证配置
     *   → ensureInstallationConfig()
     *   → 远端全部设备 snapshot → staging
     *   → 释放/暂停 Rime session
     *   → librime sync_user_data() + join maintenance
     *   → 重新初始化 Rime
     *   → staging/<installationId> → 远端
     *   → 按保留天数清理远端与 staging 的旧同步文件
     *   → 记录 lastSuccess
     * Mutex unlock
     *
     * 顺序绝不能反：必须 pull → librime 合并 → push，
     * 否则 Windows 新词不会进入本次 Rime merge。
     */
    suspend fun synchronize(): Result<RimeSyncReport> = syncMutex.withLock {
        val stateStore = store()
        val startTime = System.currentTimeMillis()
        val state = stateStore.loadOrCreate()
        try {
            val installationId: String
            val pulledFiles: Int
            val pushedFiles: Int
            val cleanedFiles: Int
            when (state.syncMode) {
                RIME_SYNC_MODE_WEBDAV -> {
                    if (!stateStore.loadOrCreate().webDavConsentGranted) {
                        throw RimeSyncException.WebDavConsentRequired()
                    }
                    val config = WebDavSyncConfig.from(stateStore.loadOrCreate())
                        ?: throw RimeSyncException.WebDavNotConfigured()
                    val transport = WebDavSyncTransport(config)
                    RimeInstallationManager.ensureInstallationConfig()
                    installationId = stateStore.loadOrCreate().installationId
                    pulledFiles = transport.pullAll().copiedFiles
                    runEnginePhase()
                    pushedFiles = transport.pushInstallation(installationId).copiedFiles
                    val retention = stateStore.loadOrCreate().retentionDays
                    cleanedFiles = cleanupAfterSync(retention) {
                        transport.cleanupOldFiles(retention, installationId)
                    }
                }
                else -> {
                    val treeUri: Uri = stateStore.getTreeUri()
                        ?: throw RimeSyncException.SyncDirectoryNotConfigured()
                    if (!bridge().hasValidTreePermission(treeUri)) {
                        throw RimeSyncException.SyncDirectoryPermissionLost()
                    }
                    RimeInstallationManager.ensureInstallationConfig()
                    installationId = stateStore.loadOrCreate().installationId
                    pulledFiles = bridge().pullFromExternal(treeUri).copiedFiles
                    runEnginePhase()
                    pushedFiles =
                        bridge().pushCurrentDeviceToExternal(treeUri, installationId).copiedFiles
                    val retention = stateStore.loadOrCreate().retentionDays
                    cleanedFiles = cleanupAfterSync(retention) {
                        bridge().cleanupOldFiles(treeUri, retention, installationId)
                    }
                }
            }
            val endTime = System.currentTimeMillis()
            stateStore.updateSuccess(endTime)
            Result.success(
                RimeSyncReport(
                    installationId = installationId,
                    pulledFiles = pulledFiles,
                    pushedFiles = pushedFiles,
                    cleanedFiles = cleanedFiles,
                    startTime = startTime,
                    endTime = endTime
                )
            )
        } catch (e: Exception) {
            recordFailure(stateStore, e)
        }
    }

    /**
     * 释放输入状态 → native 清理 session → sync → join maintenance，
     * 无论成败都在 finally 中恢复输入引擎。
     */
    private suspend fun runEnginePhase() {
        var engineDisturbed = false
        try {
            RimeEngine.prepareForUserDataSync()
            engineDisturbed = true
            engine.synchronize().getOrThrow()
        } finally {
            if (engineDisturbed) {
                runCatching { Kernel.resetIme() }
            }
        }
    }

    /**
     * 同步成功后按保留天数清理旧同步文件。
     * 远端清理失败不影响同步结果；staging 缓存清理优先保证本地卫生。
     */
    private suspend fun cleanupAfterSync(
        retentionDays: Int,
        remoteCleanup: suspend () -> Int
    ): Int {
        if (retentionDays <= 0) return 0
        var cleaned = StagingFileSink.cleanupOldFiles(retentionDays)
        cleaned += runCatching { remoteCleanup() }.getOrDefault(0)
        return cleaned
    }

    private fun recordFailure(
        stateStore: RimeSyncStateStore,
        e: Throwable
    ): Result<RimeSyncReport> {
        stateStore.updateError(e.message ?: e.javaClass.simpleName)
        return Result.failure(e)
    }
}
