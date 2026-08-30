package com.yuyan.imemodule.rime.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yuyan.imemodule.service.ImeService

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
        if (ImeService.inputWindowShown) {
            // 正在输入：跳过本轮自动同步，避免重置输入引擎打断候选；
            // 完全静默，下一周期再同步。
            return Result.success()
        }
        return if (RimeSyncManager.synchronize().isSuccess) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}
