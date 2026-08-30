package com.yuyan.imemodule.rime.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * 定时同步调度：
 * - WebDAV 模式且同步周期 > 0 时注册唯一周期任务
 * - 手动模式或非 WebDAV 模式时取消任务
 * 应用启动时调用 reschedule()，保证升级/重启后任务仍在。
 */
object RimeSyncScheduler {

    private const val UNIQUE_WORK_NAME = "rime-sync-periodic"

    fun reschedule(context: Context) {
        val state = RimeSyncStateStore(context).load() ?: return
        val hours = if (state.syncMode == RIME_SYNC_MODE_WEBDAV) {
            state.syncIntervalHours
        } else {
            0
        }
        schedule(context, hours)
    }

    fun schedule(context: Context, intervalHours: Int) {
        val workManager = WorkManager.getInstance(context)
        if (intervalHours <= 0) {
            workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
            return
        }
        val request = PeriodicWorkRequestBuilder<RimeSyncWorker>(
            intervalHours.toLong(),
            TimeUnit.HOURS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        workManager.enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
