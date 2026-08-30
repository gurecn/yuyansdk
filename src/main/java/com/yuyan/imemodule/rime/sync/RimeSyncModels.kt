package com.yuyan.imemodule.rime.sync

/**
 * SAF 与 staging 之间一次文件交换的结果。
 */
data class SyncCopyReport(
    val copiedFiles: Int,
    val skippedFiles: Int,
    val bytes: Long
)

/**
 * 一次完整 Rime 用户数据同步的结果。
 */
data class RimeSyncReport(
    val installationId: String,
    val pulledFiles: Int,
    val pushedFiles: Int,
    val cleanedFiles: Int = 0,
    val startTime: Long,
    val endTime: Long
)

/** 同步方式：SAF 系统目录选择器。 */
const val RIME_SYNC_MODE_SAF = "saf"

/** 同步方式：WebDAV（推荐坚果云）。 */
const val RIME_SYNC_MODE_WEBDAV = "webdav"

/**
 * Rime 同步异常基类。
 *
 * UI 不得解析 Exception.message 判断类型，必须使用异常类型分支。
 */
sealed class RimeSyncException(message: String) : Exception(message) {
    class SyncDirectoryNotConfigured :
        RimeSyncException("未选择 Rime 同步目录")

    class SyncDirectoryPermissionLost :
        RimeSyncException("同步目录授权已失效，请重新选择")

    class SyncDirectoryReadOnly :
        RimeSyncException("同步目录不可读或不可写")

    class NativeSyncUnavailable(message: String = "当前版本缺少 Rime 原生同步接口") :
        RimeSyncException(message)

    class NativeSyncFailed :
        RimeSyncException("Rime 原生同步失败")

    class ExternalReadFailed(cause: Throwable? = null) :
        RimeSyncException("读取外部同步目录失败") {
        init {
            cause?.let(::initCause)
        }
    }

    class ExternalWriteFailed(cause: Throwable? = null) :
        RimeSyncException("写入外部同步目录失败") {
        init {
            cause?.let(::initCause)
        }
    }

    class WebDavNotConfigured :
        RimeSyncException("WebDAV 同步未配置")

    class WebDavConsentRequired :
        RimeSyncException("WebDAV 联网未获授权，请重新选择同步方式并同意联网说明")

    class WebDavAuthFailed :
        RimeSyncException("WebDAV 账号或应用密码错误")

    class WebDavNetworkFailed(cause: Throwable? = null) :
        RimeSyncException(
            "WebDAV 网络请求失败" +
                (cause?.message?.let { "：" + it } ?: "")
        ) {
        init {
            cause?.let(::initCause)
        }
    }

    class WebDavRemoteFailed(message: String) :
        RimeSyncException(message)
}
