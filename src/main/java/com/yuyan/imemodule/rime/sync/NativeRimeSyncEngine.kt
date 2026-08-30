package com.yuyan.imemodule.rime.sync

import com.yuyan.inputmethod.core.Rime

/**
 * 通过 libyuyansyncbridge.so 调用 libyuyanime.so 内
 * 同一个 librime 实例的 sync_user_data()。
 *
 * 禁止在 APK 里再复制一份 librime，否则会形成两个实例
 * 同时打开同一 userdb 的非法架构。
 *
 * 调用方必须保证在同一线程串行调度（RimeSyncManager 的 Mutex +
 * 调用线程上的输入引擎生命周期）。
 */
class NativeRimeSyncEngine : RimeSyncEngine {

    override suspend fun synchronize(): Result<Unit> {
        return try {
            if (Rime.syncRimeUserDataBlocking()) {
                Result.success(Unit)
            } else {
                Result.failure(RimeSyncException.NativeSyncFailed())
            }
        } catch (e: UnsatisfiedLinkError) {
            Result.failure(
                RimeSyncException.NativeSyncUnavailable(
                    e.message ?: "native sync bridge missing"
                )
            )
        }
    }
}
