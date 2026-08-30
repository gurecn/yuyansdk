package com.yuyan.imemodule.rime.sync

/**
 * Rime 用户数据同步引擎抽象。
 *
 * SAF 数据层不直接依赖 JNI 实现：
 * native 暂时无法接通时，installation config、SAF pull/push、
 * 目录权限与文件一致性仍然可以独立测试。
 */
interface RimeSyncEngine {

    suspend fun synchronize(): Result<Unit>
}
