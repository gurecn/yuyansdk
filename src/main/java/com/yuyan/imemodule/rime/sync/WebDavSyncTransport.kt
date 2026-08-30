package com.yuyan.imemodule.rime.sync

import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Locale
import javax.xml.parsers.DocumentBuilderFactory

/**
 * WebDAV 同步配置（推荐坚果云：https://dav.jianguoyun.com/dav/<目录>，
 * 使用坚果云「应用密码」，不是登录密码）。
 */
data class WebDavSyncConfig(
    val baseUrl: String,
    val username: String,
    val password: String
) {
    companion object {
        fun from(state: RimeSyncDeviceState): WebDavSyncConfig? {
            val url = state.webDavUrl?.trim()?.trimEnd('/')
            val user = state.webDavUsername
            val pass = state.webDavPassword
            if (url.isNullOrEmpty() || user.isNullOrEmpty() || pass.isNullOrEmpty()) {
                return null
            }
            return WebDavSyncConfig(url, user, pass)
        }
    }
}

internal data class DavEntry(
    val relativePath: String,
    val isDirectory: Boolean,
    val lastModified: Long
)

/**
 * WebDAV 同步通道。
 *
 * 与 SAF 桥保持同样的数据层边界：
 * - pullAll：拉取远端全部设备 snapshot 到 staging（merge/overwrite）
 * - pushInstallation：只推送 staging/<installationId>
 * - cleanupOldFiles：按保留天数清理远端旧同步文件（跳过本机目录）
 */
class WebDavSyncTransport(
    private val config: WebDavSyncConfig
) {
    private val authHeader: String
        get() = "Basic " + Base64.encodeToString(
            ("${config.username}:${config.password}").toByteArray(Charsets.UTF_8),
            Base64.NO_WRAP
        )

    /**
     * 测试连接：PROPFIND 成功即通过；404 表示远端目录还没创建，同样视为可用。
     */
    suspend fun testConnection(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val code = propfindStatusCode(config.baseUrl)
            when (code) {
                HttpURLConnection.HTTP_UNAUTHORIZED ->
                    Result.failure(RimeSyncException.WebDavAuthFailed())
                in 200..299, HttpURLConnection.HTTP_NOT_FOUND ->
                    Result.success(Unit)
                else ->
                    Result.failure(RimeSyncException.WebDavRemoteFailed("HTTP $code"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "webdav test failed: " + config.baseUrl, e)
            Result.failure(RimeSyncException.WebDavNetworkFailed(e))
        }
    }

    suspend fun pullAll(): SyncCopyReport = withContext(Dispatchers.IO) {
        val report = MutableSyncCopyReport()
        val rootEntries = try {
            propfind(config.baseUrl)
        } catch (e: RimeSyncException.WebDavRemoteFailed) {
            if (e.message?.startsWith("HTTP 404") == true) {
                return@withContext SyncCopyReport(0, 0, 0)
            }
            throw e
        }
        StagingFileSink.root.mkdirs()
        for (entry in rootEntries) {
            if (entry.relativePath.isEmpty()) continue
            if (entry.isDirectory) {
                val children = propfind(childUrl(entry.relativePath))
                for (child in children) {
                    if (child.isDirectory) continue
                    pullFile(child, report)
                }
            } else {
                pullFile(entry, report)
            }
        }
        report.toReport()
    }

    suspend fun pushInstallation(installationId: String): SyncCopyReport =
        withContext(Dispatchers.IO) {
            val report = MutableSyncCopyReport()
            mkcolQuiet(config.baseUrl)
            val deviceDirUrl = childUrl(installationId)
            mkcolQuiet(deviceDirUrl)
            val sourceDir = File(StagingFileSink.root, installationId)
            if (!sourceDir.isDirectory) {
                return@withContext SyncCopyReport(0, 0, 0)
            }
            val files = sourceDir.listFiles()?.sortedBy { it.name }.orEmpty()
            for (file in files) {
                if (!file.isFile || file.name.endsWith(TEMP_SUFFIX)) continue
                uploadWithTempReplace(deviceDirUrl, file)
                report.copiedFiles++
                report.bytes += file.length()
            }
            report.toReport()
        }

    /**
     * 清理远端旧同步文件：
     * - .yuyan.tmp/.tmp 残留超过 24 小时删除
     * - 其他文件最后修改时间超过 retentionDays 删除
     * - 跳过本机 installation 目录；删空的其他设备目录一并删除
     */
    suspend fun cleanupOldFiles(
        retentionDays: Int,
        ownInstallationId: String
    ): Int = withContext(Dispatchers.IO) {
        if (retentionDays <= 0) return@withContext 0
        val retentionMillis = retentionDays.toLong() * 24 * 60 * 60 * 1000
        val now = System.currentTimeMillis()
        var cleaned = 0
        val rootEntries = try {
            propfind(config.baseUrl)
        } catch (e: RimeSyncException.WebDavRemoteFailed) {
            if (e.message?.startsWith("HTTP 404") == true) {
                return@withContext 0
            }
            throw e
        }
        for (entry in rootEntries) {
            if (entry.relativePath.isEmpty()) continue
            if (entry.isDirectory) {
                if (entry.relativePath == ownInstallationId) continue
                val dirUrl = childUrl(entry.relativePath)
                val children = propfind(dirUrl)
                for (child in children) {
                    if (child.isDirectory) continue
                    val childUrl = childUrl(entry.relativePath, child.relativePath)
                    if (isExpired(child, now, retentionMillis) && deleteQuiet(childUrl)) {
                        cleaned++
                    }
                }
                if (propfind(dirUrl).none { it.relativePath.isNotEmpty() }) {
                    deleteQuiet(dirUrl)
                }
            } else {
                val fileUrl = childUrl(entry.relativePath)
                if (isExpired(entry, now, retentionMillis) && deleteQuiet(fileUrl)) {
                    cleaned++
                }
            }
        }
        cleaned
    }

    private fun isExpired(entry: DavEntry, now: Long, retentionMillis: Long): Boolean {
        if (entry.lastModified <= 0) return false
        val isTemp = entry.relativePath.endsWith(TEMP_SUFFIX) ||
            entry.relativePath.endsWith(".tmp")
        val threshold = if (isTemp) TEMP_GRACE_MILLIS else retentionMillis
        return now - entry.lastModified > threshold
    }

    private fun pullFile(entry: DavEntry, report: MutableSyncCopyReport) {
        if (entry.relativePath.endsWith(TEMP_SUFFIX)) {
            report.skippedFiles++
            return
        }
        val conn = openConnection(childUrl(entry.relativePath), "GET")
        try {
            val code = conn.responseCode
            if (code !in 200..299) {
                throw RimeSyncException.WebDavRemoteFailed("HTTP $code")
            }
            conn.inputStream.use { input ->
                StagingFileSink.writeAtomically(entry.relativePath, input)
            }
            val target = StagingFileSink.targetFor(entry.relativePath)
            report.copiedFiles++
            report.bytes += target.length()
        } catch (e: RimeSyncException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "webdav GET failed: " + entry.relativePath, e)
            throw RimeSyncException.WebDavNetworkFailed(e)
        } finally {
            conn.disconnect()
        }
    }

    /**
     * 优先 PUT 临时文件再 MOVE 覆盖正式文件；
     * 服务端不支持 MOVE 时 fallback：删除旧文件后直接 PUT 正式名。
     */
    private fun uploadWithTempReplace(dirUrl: String, source: File) {
        val finalName = source.name
        val tmpName = ".$finalName$TEMP_SUFFIX"
        val tmpUrl = "$dirUrl/" + encode(tmpName)
        putFile(tmpUrl, source)
        val moved = moveRemote(tmpUrl, dirUrl, finalName)
        if (!moved) {
            deleteQuiet("$dirUrl/" + encode(finalName))
            putDirect(dirUrl, finalName, source)
        }
    }

    private fun putFile(url: String, source: File) {
        val conn = openConnection(url, "PUT")
        try {
            conn.doOutput = true
            conn.setFixedLengthStreamingMode(source.length())
            conn.outputStream.use { output ->
                source.inputStream().use { input -> input.copyTo(output) }
            }
            val code = conn.responseCode
            if (code !in 200..299) {
                throw RimeSyncException.WebDavRemoteFailed("HTTP $code")
            }
        } catch (e: RimeSyncException) {
            throw e
        } catch (e: Exception) {
            throw RimeSyncException.WebDavNetworkFailed(e)
        } finally {
            conn.disconnect()
        }
    }

    private fun putDirect(dirUrl: String, name: String, source: File) {
        val conn = openConnection("$dirUrl/" + encode(name), "PUT")
        try {
            conn.doOutput = true
            conn.setFixedLengthStreamingMode(source.length())
            conn.outputStream.use { output ->
                source.inputStream().use { input -> input.copyTo(output) }
            }
            val code = conn.responseCode
            if (code !in 200..299) {
                throw RimeSyncException.WebDavRemoteFailed("HTTP $code")
            }
        } catch (e: RimeSyncException) {
            throw e
        } catch (e: Exception) {
            throw RimeSyncException.WebDavNetworkFailed(e)
        } finally {
            conn.disconnect()
        }
    }

    private fun moveRemote(sourceUrl: String, targetDirUrl: String, targetName: String): Boolean {
        val conn = openConnection(sourceUrl, "MOVE")
        return try {
            conn.setRequestProperty("Destination", "$targetDirUrl/" + encode(targetName))
            conn.setRequestProperty("Overwrite", "T")
            val code = conn.responseCode
            code in 200..299
        } catch (e: Exception) {
            false
        } finally {
            conn.disconnect()
        }
    }

    private fun deleteQuiet(url: String): Boolean {
        val conn = openConnection(url, "DELETE")
        return try {
            val code = conn.responseCode
            code in 200..299 || code == HttpURLConnection.HTTP_NOT_FOUND
        } catch (e: Exception) {
            false
        } finally {
            conn.disconnect()
        }
    }

    private fun mkcolQuiet(url: String) {
        val conn = openConnection(url, "MKCOL")
        try {
            val code = conn.responseCode
            if (code in 200..299 || code == 405 || code == 301) {
                return
            }
        } catch (_: Exception) {
        } finally {
            conn.disconnect()
        }
    }

    private fun propfindStatusCode(url: String): Int {
        val conn = openConnection(url, "PROPFIND")
        return try {
            conn.setRequestProperty("Depth", "0")
            conn.responseCode
        } finally {
            conn.disconnect()
        }
    }

    /**
     * PROPFIND Depth 1：返回目录下一级的条目（不含目录自身）。
     */
    private fun propfind(url: String): List<DavEntry> {
        val conn = openConnection(url, "PROPFIND")
        try {
            conn.setRequestProperty("Depth", "1")
            val code = conn.responseCode
            if (code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                throw RimeSyncException.WebDavAuthFailed()
            }
            if (code !in 200..299) {
                throw RimeSyncException.WebDavRemoteFailed("HTTP $code")
            }
            val xml = conn.inputStream.bufferedReader().readText()
            val basePath = URL(url).path.trimEnd('/')
            return parsePropfind(xml, basePath)
        } catch (e: RimeSyncException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "webdav PROPFIND failed: " + url, e)
            throw RimeSyncException.WebDavNetworkFailed(e)
        } finally {
            conn.disconnect()
        }
    }

    private fun parsePropfind(xml: String, basePath: String): List<DavEntry> {
        val factory = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
        }
        val document = factory.newDocumentBuilder()
            .parse(ByteArrayInputStream(xml.toByteArray(Charsets.UTF_8)))
        val entries = ArrayList<DavEntry>()
        val responses = document.getElementsByTagNameNS(DAV_NS, "response")
        for (index in 0 until responses.length) {
            val response = responses.item(index) as? Element ?: continue
            val href = childText(response, "href") ?: continue
            val isDirectory = hasChild(response, "resourcetype", "collection")
            val relative = toRelativePath(href, basePath) ?: continue
            if (relative.isEmpty()) continue
            entries.add(
                DavEntry(
                    relativePath = relative,
                    isDirectory = isDirectory,
                    lastModified = parseRfc1123(childText(response, "getlastmodified"))
                )
            )
        }
        return entries
    }

    private fun toRelativePath(href: String, basePath: String): String? {
        val path = try {
            URL(href).path
        } catch (_: Exception) {
            href.substringBefore('?')
        }
        val decoded = decodePath(path)
        val base = decodePath(basePath).trimEnd('/')
        if (!decoded.startsWith(base)) return null
        return decoded.removePrefix(base).trim('/')
    }

    private fun decodePath(path: String): String {
        return path.split('/').joinToString("/") { segment ->
            try {
                java.net.URLDecoder.decode(segment, "UTF-8")
            } catch (_: Exception) {
                segment
            }
        }
    }

    private fun childText(element: Element, localName: String): String? {
        val nodes = element.getElementsByTagNameNS(DAV_NS, localName)
        if (nodes.length == 0) return null
        return nodes.item(0)?.textContent?.trim()
    }

    private fun hasChild(element: Element, parentLocalName: String, childLocalName: String): Boolean {
        val parents = element.getElementsByTagNameNS(DAV_NS, parentLocalName)
        if (parents.length == 0) return false
        return (parents.item(0) as Element)
            .getElementsByTagNameNS(DAV_NS, childLocalName).length > 0
    }

    private fun parseRfc1123(value: String?): Long {
        if (value.isNullOrEmpty()) return 0L
        return try {
            RFC1123_FORMAT.parse(value)?.time ?: 0L
        } catch (_: Exception) {
            0L
        }
    }

    private fun childUrl(vararg names: String): String {
        return names.fold(config.baseUrl) { acc, name ->
            "$acc/" + encode(name)
        }
    }

    private fun encode(segment: String): String {
        return URLEncoder.encode(segment, "UTF-8").replace("+", "%20")
    }

    private fun openConnection(url: String, method: String): HttpURLConnection {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.requestMethod = method
        conn.connectTimeout = CONNECT_TIMEOUT_MILLIS
        conn.readTimeout = READ_TIMEOUT_MILLIS
        conn.setRequestProperty("Authorization", authHeader)
        conn.setRequestProperty("User-Agent", "YuyanIme-RimeSync/1.0")
        return conn
    }

    companion object {
        private const val TAG = "YuyanRimeSync"
        private const val DAV_NS = "DAV:"
        private const val TEMP_SUFFIX = ".yuyan.tmp"
        private const val TEMP_GRACE_MILLIS = 24L * 60 * 60 * 1000
        private const val CONNECT_TIMEOUT_MILLIS = 15_000
        private const val READ_TIMEOUT_MILLIS = 60_000
        private val RFC1123_FORMAT =
            SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US)
    }
}
