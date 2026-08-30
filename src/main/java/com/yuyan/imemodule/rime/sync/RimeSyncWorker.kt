package com.yuyan.imemodule.rime.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * 定时同步 Worker：只在 WebDAV 模式下执行完整同步。
 * 失败返回 retry，由 WorkManager 按退避策略重试。
 */
class RimeSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val state = RimeSyncStateStore(applicationContext).load()
            ?: return Result.success()
        if (state.syncMode != RIME_SYNC_MODE_WEBDAV) {
            return Result.success()
        }
        if (!state.webDavConsentGranted) {
            return Result.success()
        }
        return if (RimeSyncManager.synchronize().isSuccess) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}
