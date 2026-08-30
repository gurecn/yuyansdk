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
     *   → 验证 SAF URI 与持久化授权
     *   → ensureInstallationConfig()
     *   → SAF → staging（拉取所有设备 snapshot）
     *   → 释放/暂停 Rime session
     *   → librime sync_user_data() + join maintenance
     *   → 重新初始化 Rime
     *   → staging/<installationId> → SAF/<installationId>
     *   → 记录 lastSuccess
     * Mutex unlock
     *
     * 顺序绝不能反：必须 pull → librime 合并 → push，
     * 否则 Windows 新词不会进入本次 Rime merge。
     */
    suspend fun synchronize(): Result<RimeSyncReport> = syncMutex.withLock {
        val stateStore = store()
        val storageBridge = bridge()
        val startTime = System.currentTimeMillis()
        stateStore.loadOrCreate()
        try {
            val treeUri: Uri = stateStore.getTreeUri()
                ?: return@withLock recordFailure(
                    stateStore,
                    RimeSyncException.SyncDirectoryNotConfigured()
                )
            if (!storageBridge.hasValidTreePermission(treeUri)) {
                return@withLock recordFailure(
                    stateStore,
                    RimeSyncException.SyncDirectoryPermissionLost()
                )
            }

            RimeInstallationManager.ensureInstallationConfig()
            val installationId = stateStore.loadOrCreate().installationId

            // 1. 先拉取远端全部设备 snapshot（Windows 新词必须先进 staging）
            val pullReport = storageBridge.pullFromExternal(treeUri)

            // 2. 释放输入状态 → native 清理 session → sync → join maintenance
            var engineDisturbed = false
            try {
                RimeEngine.prepareForUserDataSync()
                engineDisturbed = true
                engine.synchronize().getOrThrow()
            } finally {
                // 无论成败都必须恢复输入引擎
                if (engineDisturbed) {
                    runCatching { Kernel.resetIme() }
                }
            }

            // 3. 只推送本机 installation id 的快照
            val pushReport =
                storageBridge.pushCurrentDeviceToExternal(treeUri, installationId)
            val endTime = System.currentTimeMillis()
            stateStore.updateSuccess(endTime)
            Result.success(
                RimeSyncReport(
                    installationId = installationId,
                    pulledFiles = pullReport.copiedFiles,
                    pushedFiles = pushReport.copiedFiles,
                    startTime = startTime,
                    endTime = endTime
                )
            )
        } catch (e: Exception) {
            recordFailure(stateStore, e)
        }
    }

    private fun recordFailure(
        stateStore: RimeSyncStateStore,
        e: Throwable
    ): Result<RimeSyncReport> {
        stateStore.updateError(e.message ?: e.javaClass.simpleName)
        return Result.failure(e)
    }
}
