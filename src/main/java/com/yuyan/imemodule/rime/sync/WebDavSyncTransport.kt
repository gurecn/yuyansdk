package com.yuyan.imemodule.rime.sync

import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import java.io.File
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
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
 * WebDAV 同步通道（OkHttp 实现）。
 *
 * Android 系统自带 HttpURLConnection 只允许标准 HTTP 方法，
 * 会拒绝 PROPFIND/MKCOL/MOVE；OkHttp 4 原生支持这些 WebDAV 方法。
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

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    /**
     * 测试连接：PROPFIND 成功即通过；404 表示远端目录还没创建，同样视为可用。
     */
    suspend fun testConnection(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.newCall(
                baseRequest(config.baseUrl)
                    .method("PROPFIND", null)
                    .header("Depth", "0")
                    .build()
            ).execute().use { response ->
                when (response.code) {
                    401 -> Result.failure(RimeSyncException.WebDavAuthFailed())
                    in 200..299, 404 -> Result.success(Unit)
                    else -> Result.failure(
                        RimeSyncException.WebDavRemoteFailed(
                            "HTTP ${response.code} PROPFIND ${config.baseUrl}"
                        )
                    )
                }
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
                val children = try {
                    propfind(childUrl(entry.relativePath), entry.relativePath)
                } catch (e: RimeSyncException.WebDavRemoteFailed) {
                    // 目录在列目录与拉取之间被远端删除：跳过，不使整个同步失败
                    Log.w(TAG, "webdav skip dir on pull: " + entry.relativePath + ", " + e.message)
                    continue
                }
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
            ensureCollection(config.baseUrl)
            val deviceDirUrl = childUrl(installationId)
            ensureCollection(deviceDirUrl)
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
                val children = propfind(dirUrl, entry.relativePath)
                for (child in children) {
                    if (child.isDirectory) continue
                    val fileUrl = childUrl(entry.relativePath, child.relativePath)
                    if (isExpired(child, now, retentionMillis) && deleteQuiet(fileUrl)) {
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
        try {
            client.newCall(baseRequest(childUrl(entry.relativePath)).get().build())
                .execute().use { response ->
                    val code = response.code
                    if (code !in 200..299) {
                        if (code == 404) {
                            // 文件在列目录与下载之间消失：按跳过处理
                            Log.w(TAG, "webdav GET 404, skip: " + entry.relativePath)
                            report.skippedFiles++
                            return
                        }
                        Log.e(
                            TAG,
                            "webdav GET failed: " + code + " " + childUrl(entry.relativePath)
                        )
                        throw RimeSyncException.WebDavRemoteFailed(
                            "HTTP $code GET " + childUrl(entry.relativePath)
                        )
                    }
                    val stream = response.body?.byteStream()
                        ?: throw RimeSyncException.WebDavNetworkFailed()
                    stream.use { input ->
                        StagingFileSink.writeAtomically(entry.relativePath, input)
                    }
                }
        } catch (e: RimeSyncException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "webdav GET failed: " + entry.relativePath, e)
            throw RimeSyncException.WebDavNetworkFailed(e)
        }
        val target = StagingFileSink.targetFor(entry.relativePath)
        report.copiedFiles++
        report.bytes += target.length()
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
        val body = source.asRequestBody(OCTET_STREAM)
        try {
            client.newCall(baseRequest(url).method("PUT", body).build())
                .execute().use { response ->
                    val code = response.code
                    if (code !in 200..299) {
                        Log.e(TAG, "webdav PUT failed: " + code + " " + url)
                        throw RimeSyncException.WebDavRemoteFailed("HTTP $code PUT $url")
                    }
                }
        } catch (e: RimeSyncException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "webdav PUT failed: " + url, e)
            throw RimeSyncException.WebDavNetworkFailed(e)
        }
    }

    private fun putDirect(dirUrl: String, name: String, source: File) {
        putFile("$dirUrl/" + encode(name), source)
    }

    private fun moveRemote(sourceUrl: String, targetDirUrl: String, targetName: String): Boolean {
        return try {
            client.newCall(
                baseRequest(sourceUrl)
                    .method("MOVE", null)
                    .header("Destination", "$targetDirUrl/" + encode(targetName))
                    .header("Overwrite", "T")
                    .build()
            ).execute().use { it.code in 200..299 }
        } catch (e: Exception) {
            Log.w(TAG, "webdav MOVE failed", e)
            false
        }
    }

    private fun deleteQuiet(url: String): Boolean {
        return try {
            client.newCall(baseRequest(url).delete().build())
                .execute().use { it.code in 200..299 || it.code == 404 }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 确保远端目录存在：PROPFIND 探测，404 时 MKCOL 创建。
     * 创建失败不再静默忽略，直接抛出带状态码与 URL 的异常。
     */
    private fun ensureCollection(url: String) {
        val probe = propfindStatusCode(url)
        if (probe in 200..299) return
        if (probe == 404) {
            try {
                client.newCall(baseRequest(url).method("MKCOL", null).build())
                    .execute().use { response ->
                        val code = response.code
                        if (code in 200..299 || code == 405 || code == 301) {
                            return
                        }
                        Log.e(TAG, "webdav MKCOL failed: " + code + " " + url)
                        throw RimeSyncException.WebDavRemoteFailed(
                            "HTTP $code MKCOL $url"
                        )
                    }
            } catch (e: RimeSyncException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "webdav MKCOL failed: " + url, e)
                throw RimeSyncException.WebDavNetworkFailed(e)
            }
            return
        }
        throw RimeSyncException.WebDavRemoteFailed("HTTP $probe PROPFIND $url")
    }

    private fun propfindStatusCode(url: String): Int {
        return try {
            client.newCall(
                baseRequest(url)
                    .method("PROPFIND", null)
                    .header("Depth", "0")
                    .build()
            ).execute().use { it.code }
        } catch (e: Exception) {
            Log.w(TAG, "webdav PROPFIND probe failed: " + url, e)
            throw RimeSyncException.WebDavNetworkFailed(e)
        }
    }

    /**
     * PROPFIND Depth 1：返回目录下一级的条目（不含目录自身）。
     */
    private fun propfind(url: String, parentRelative: String? = null): List<DavEntry> {
        try {
            client.newCall(
                baseRequest(url)
                    .method("PROPFIND", null)
                    .header("Depth", "1")
                    .build()
            ).execute().use { response ->
                val code = response.code
                if (code == 401) {
                    Log.e(TAG, "webdav PROPFIND 401: " + url)
                    throw RimeSyncException.WebDavAuthFailed()
                }
                if (code !in 200..299) {
                    Log.e(TAG, "webdav PROPFIND failed: " + code + " " + url)
                    throw RimeSyncException.WebDavRemoteFailed("HTTP $code PROPFIND $url")
                }
                val xml = response.body?.string() ?: ""
                return parsePropfind(xml, parentRelative)
            }
        } catch (e: RimeSyncException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "webdav PROPFIND failed: " + url, e)
            throw RimeSyncException.WebDavNetworkFailed(e)
        }
    }

    /**
     * 解析 PROPFIND Depth 1 响应。
     *
     * 相对路径不再依赖「配置地址前缀」匹配：坚果云会把请求路径别名到
     * /dav/ 真实前缀（如配置 /rimesyc，响应 href 却是 /dav/rimesyc/...），
     * 前缀不匹配时旧实现会把全部条目静默丢弃，导致 pull 拉回 0 个文件，
     * Windows 快照永远无法合并进 Android。
     * 现在改用每条 href 的末段名称，并与父目录相对路径拼接，
     * 对服务端 href 前缀/别名完全免疫；路径逃逸仍由 StagingFileSink 兜底。
     */
    private fun parsePropfind(xml: String, parentRelative: String?): List<DavEntry> {
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
            val name = decodePath(hrefPath(href)).trimEnd('/').substringAfterLast('/')
            if (name.isEmpty()) continue // 集合自身
            val relative = if (parentRelative.isNullOrEmpty()) name else "$parentRelative/$name"
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

    private fun hrefPath(href: String): String {
        return try {
            URL(href).path
        } catch (_: Exception) {
            href.substringBefore('?')
        }
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

    private fun baseRequest(url: String): Request.Builder {
        return Request.Builder()
            .url(url)
            .header("Authorization", authHeader)
            .header("User-Agent", "YuyanIme-RimeSync/1.0")
    }

    companion object {
        private const val TAG = "YuyanRimeSync"
        private const val DAV_NS = "DAV:"
        private const val TEMP_SUFFIX = ".yuyan.tmp"
        private const val TEMP_GRACE_MILLIS = 24L * 60 * 60 * 1000
        private const val CONNECT_TIMEOUT_SECONDS = 15L
        private const val READ_TIMEOUT_SECONDS = 60L
        private val OCTET_STREAM = "application/octet-stream".toMediaType()
        private val RFC1123_FORMAT =
            SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US)
    }
}
