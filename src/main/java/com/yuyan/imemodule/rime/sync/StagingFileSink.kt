package com.yuyan.imemodule.rime.sync

import com.yuyan.imemodule.application.CustomConstant
import java.io.File
import java.io.InputStream

/**
 * staging 本地文件写入与清理的共享实现。
 * 所有远端拉取（SAF / WebDAV）写入 staging 时统一走这里，
 * 保证 tmp + rename 原子写与路径逃逸防护只实现一份。
 */
internal object StagingFileSink {

    private const val TEMP_SUFFIX = ".yuyan.tmp"
    private const val TEMP_GRACE_MILLIS = 24L * 60 * 60 * 1000

    val root: File
        get() = File(CustomConstant.RIME_SYNC_STAGING_PATH)

    /**
     * 把 relativePath 映射到 staging 内的目标文件，并防御路径逃逸。
     */
    fun targetFor(relativePath: String): File {
        require(relativePath.isNotEmpty() && relativePath != ".") { "empty relative path" }
        require(!relativePath.split('/', '\\').any { it == ".." }) { "Illegal path escape: $relativePath" }
        val rootCanonical = root.canonicalFile
        val target = File(rootCanonical, relativePath).canonicalFile
        require(target.path.startsWith(rootCanonical.path + File.separator)) {
            "Illegal path escape: $relativePath"
        }
        return target
    }

    /**
     * 先写 xxx.tmp，完整写完再 rename 为正式文件，
     * 防止同步中途被杀留下半个 userdb.txt。
     */
    fun writeAtomically(relativePath: String, input: InputStream) {
        val target = targetFor(relativePath)
        target.parentFile?.mkdirs()
        val tmp = File(target.parentFile, target.name + ".tmp")
        tmp.outputStream().use { output -> input.copyTo(output) }
        if (!tmp.renameTo(target)) {
            tmp.copyTo(target, overwrite = true)
            tmp.delete()
        }
    }

    /**
     * 清理 staging 中的旧同步文件：
     * - .yuyan.tmp/.tmp 残留超过 24 小时即删除
     * - 其余文件最后修改时间超过 retentionDays 即删除
     * 返回删除的文件数。
     */
    fun cleanupOldFiles(retentionDays: Int, now: Long = System.currentTimeMillis()): Int {
        if (retentionDays <= 0) return 0
        val retentionMillis = retentionDays.toLong() * 24 * 60 * 60 * 1000
        var cleaned = 0
        root.walkTopDown().filter { it.isFile }.forEach { file ->
            val age = now - file.lastModified()
            val isTemp = file.name.endsWith(TEMP_SUFFIX) || file.name.endsWith(".tmp")
            val expired = if (isTemp) age > TEMP_GRACE_MILLIS else age > retentionMillis
            if (expired && file.delete()) {
                cleaned++
            }
        }
        // 删除变空的目录
        root.walkBottomUp().filter { it.isDirectory && it != root }.forEach { dir ->
            dir.delete()
        }
        return cleaned
    }
}
