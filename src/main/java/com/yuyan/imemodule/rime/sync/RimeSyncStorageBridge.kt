package com.yuyan.imemodule.rime.sync

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.yuyan.imemodule.application.CustomConstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 跨权限同步核心：
 * - pullFromExternal：SAF 共享目录 → Android 私有 staging（拉取全部设备 snapshot）
 * - pushCurrentDeviceToExternal：staging/<installationId> → SAF/<installationId>
 *
 * librime 只接受本地文件系统目录，因此 SAF content:// 永远不进 installation.yaml。
 */
class RimeSyncStorageBridge(
    private val context: Context
) {
    private val resolver
        get() = context.contentResolver

    private val stagingRoot
        get() = File(CustomConstant.RIME_SYNC_STAGING_PATH)

    /**
     * 启动时检查 SAF grant 是否仍有效：persistedUriPermissions 中必须同时存在读与写。
     */
    fun hasValidTreePermission(treeUri: Uri): Boolean {
        return resolver.persistedUriPermissions.any {
            it.uri == treeUri && it.isReadPermission && it.isWritePermission
        }
    }

    /**
     * 选择目录后持久化 SAF 权限，之后把 URI 交给 RimeSyncStateStore 保存。
     */
    fun persistTreePermission(treeUri: Uri) {
        resolver.takePersistableUriPermission(
            treeUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
    }

    /**
     * 清除目录时释放持久化权限。
     */
    fun releaseTreePermission(treeUri: Uri) {
        runCatching {
            resolver.releasePersistableUriPermission(
                treeUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }
    }

    /**
     * SAF → staging。
     *
     * 拉取全部设备目录（windows-pc/、android-xxx/…），因为 librime 执行
     * sync_user_data() 时需要扫描其他 installation id 的快照做多设备合并。
     * 只 merge/overwrite，不 exact mirror + delete。
     */
    suspend fun pullFromExternal(treeUri: Uri): SyncCopyReport = withContext(Dispatchers.IO) {
        val tree = DocumentFile.fromTreeUri(context, treeUri)
            ?: throw RimeSyncException.ExternalReadFailed()
        if (!tree.exists() || !tree.isDirectory || !tree.canRead()) {
            throw RimeSyncException.SyncDirectoryReadOnly()
        }
        stagingRoot.mkdirs()
        val accumulator = MutableSyncCopyReport()
        pullChildren(tree, stagingRoot, accumulator)
        accumulator.toReport()
    }

    /**
     * staging → SAF，只允许推送当前 Android installation id 的目录。
     *
     * 绝对不要回传其他设备（windows/、laptop/…）的快照，
     * 否则 Android 会把本地拉下来的旧 Windows 快照又 push 回去覆盖新数据。
     */
    suspend fun pushCurrentDeviceToExternal(
        treeUri: Uri,
        installationId: String
    ): SyncCopyReport = withContext(Dispatchers.IO) {
        val tree = DocumentFile.fromTreeUri(context, treeUri)
            ?: throw RimeSyncException.ExternalWriteFailed()
        if (!tree.exists() || !tree.isDirectory || !tree.canWrite()) {
            throw RimeSyncException.SyncDirectoryReadOnly()
        }
        val sourceDir = File(stagingRoot, installationId)
        if (!sourceDir.isDirectory) {
            return@withContext SyncCopyReport(0, 0, 0)
        }
        val deviceDir = childDirectory(tree, installationId)
        val accumulator = MutableSyncCopyReport()
        pushFiles(sourceDir, deviceDir, accumulator)
        accumulator.toReport()
    }

    /**
     * 清理 SAF 同步目录中的旧同步文件（规则与 WebDAV 通道一致）：
     * - .yuyan.tmp/.tmp 残留超过 24 小时删除
     * - 其他文件最后修改时间超过 retentionDays 删除
     * - 跳过本机 installation 目录；删空的其他设备目录一并删除
     */
    fun cleanupOldFiles(
        treeUri: Uri,
        retentionDays: Int,
        ownInstallationId: String
    ): Int {
        if (retentionDays <= 0) return 0
        val tree = DocumentFile.fromTreeUri(context, treeUri)
            ?: return 0
        if (!tree.exists() || !tree.isDirectory) return 0
        val retentionMillis = retentionDays.toLong() * 24 * 60 * 60 * 1000
        val now = System.currentTimeMillis()
        var cleaned = 0
        for (child in tree.listFiles()) {
            val name = child.name ?: continue
            if (child.isDirectory) {
                if (name == ownInstallationId) continue
                for (file in child.listFiles()) {
                    if (file.isFile && isExpired(file, now, retentionMillis) && file.delete()) {
                        cleaned++
                    }
                }
                if (child.listFiles().isEmpty() && child.delete()) {
                    // 已清空的旧设备目录一并删除
                }
            } else if (child.isFile && isExpired(child, now, retentionMillis) && child.delete()) {
                cleaned++
            }
        }
        return cleaned
    }

    private fun isExpired(
        file: DocumentFile,
        now: Long,
        retentionMillis: Long
    ): Boolean {
        val lastModified = file.lastModified()
        if (lastModified <= 0) return false
        val isTemp = file.name?.let {
            it.endsWith(TEMP_SUFFIX) || it.endsWith(".tmp")
        } == true
        val threshold = if (isTemp) TEMP_GRACE_MILLIS else retentionMillis
        return now - lastModified > threshold
    }

    private fun pullChildren(
        source: DocumentFile,
        targetDir: File,
        accumulator: MutableSyncCopyReport
    ) {
        for (child in source.listFiles()) {
            val name = child.name ?: continue
            if (name.endsWith(TEMP_SUFFIX)) {
                accumulator.skippedFiles++
                continue
            }
            val target = safeTarget(targetDir, name)
            if (child.isDirectory) {
                target.mkdirs()
                pullChildren(child, target, accumulator)
            } else if (child.isFile) {
                // 先写 xxx.tmp，完整写完再 rename，防止同步中断留下半个 userdb.txt
                val tmp = File(target.parentFile, target.name + ".tmp")
                resolver.openInputStream(child.uri)?.use { input ->
                    tmp.outputStream().use { output -> input.copyTo(output) }
                } ?: throw RimeSyncException.ExternalReadFailed()
                if (!tmp.renameTo(target)) {
                    tmp.copyTo(target, overwrite = true)
                    tmp.delete()
                }
                accumulator.copiedFiles++
                accumulator.bytes += target.length()
            }
        }
    }

    private fun pushFiles(
        sourceDir: File,
        targetDir: DocumentFile,
        accumulator: MutableSyncCopyReport
    ) {
        val children = sourceDir.listFiles()?.sortedBy { it.name }.orEmpty()
        for (file in children) {
            if (file.isDirectory) {
                pushFiles(file, childDirectory(targetDir, file.name), accumulator)
            } else if (file.isFile) {
                if (file.name.endsWith(TEMP_SUFFIX)) {
                    accumulator.skippedFiles++
                    continue
                }
                writeWithTempFallback(targetDir, file, accumulator)
            }
        }
    }

    /**
     * 优先 temp + rename（.yuyan.tmp），DocumentProvider 不支持 rename 时
     * fallback 为直接覆盖正式文件。
     */
    private fun writeWithTempFallback(
        dir: DocumentFile,
        source: File,
        accumulator: MutableSyncCopyReport
    ) {
        val finalName = source.name
        val tmpName = ".$finalName$TEMP_SUFFIX"
        val tmpDoc = dir.createFile(MIME_OCTET_STREAM, tmpName)
            ?: throw RimeSyncException.ExternalWriteFailed()
        try {
            resolver.openOutputStream(tmpDoc.uri)?.use { output ->
                source.inputStream().use { input -> input.copyTo(output) }
            } ?: throw RimeSyncException.ExternalWriteFailed()
        } catch (e: Exception) {
            runCatching { tmpDoc.delete() }
            throw RimeSyncException.ExternalWriteFailed(e)
        }

        val existing = dir.findFile(finalName)
        val renamed = when {
            existing == null -> tmpDoc.renameTo(finalName)
            existing.delete() -> tmpDoc.renameTo(finalName)
            else -> false
        }
        if (!renamed) {
            writeDirect(dir, finalName, source)
            runCatching { tmpDoc.delete() }
        }
        accumulator.copiedFiles++
        accumulator.bytes += source.length()
    }

    private fun writeDirect(dir: DocumentFile, name: String, source: File) {
        val existing = dir.findFile(name)
        if (existing != null && !existing.delete()) {
            throw RimeSyncException.ExternalWriteFailed()
        }
        val finalDoc = dir.createFile(MIME_OCTET_STREAM, name)
            ?: throw RimeSyncException.ExternalWriteFailed()
        try {
            resolver.openOutputStream(finalDoc.uri)?.use { output ->
                source.inputStream().use { input -> input.copyTo(output) }
            } ?: throw RimeSyncException.ExternalWriteFailed()
        } catch (e: Exception) {
            runCatching { finalDoc.delete() }
            throw RimeSyncException.ExternalWriteFailed(e)
        }
    }

    private fun childDirectory(parent: DocumentFile, name: String): DocumentFile {
        val existing = parent.findFile(name)
        if (existing != null && existing.isDirectory) {
            return existing
        }
        return parent.createDirectory(name)
            ?: throw RimeSyncException.ExternalWriteFailed()
    }

    /**
     * 路径逃逸防护：目标必须位于 staging 根目录之内。
     * SAF 正常 provider 不会给出 ../，但数据层仍必须防御。
     */
    private fun safeTarget(parent: File, name: String): File {
        val target = File(parent, name)
        val root = stagingRoot.canonicalFile
        val canonical = target.canonicalFile
        require(canonical.path.startsWith(root.path + File.separator)) {
            "Illegal path escape: $name"
        }
        return target
    }

    companion object {
        private const val TEMP_SUFFIX = ".yuyan.tmp"
        private const val TEMP_GRACE_MILLIS = 24L * 60 * 60 * 1000
        private const val MIME_OCTET_STREAM = "application/octet-stream"
    }
}

internal class MutableSyncCopyReport {
    var copiedFiles = 0
    var skippedFiles = 0
    var bytes = 0L

    fun toReport() = SyncCopyReport(copiedFiles, skippedFiles, bytes)
}
