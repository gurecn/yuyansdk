package com.yuyan.imemodule.rime.sync

import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.imemodule.application.Launcher
import java.io.File
import java.util.UUID

/**
 * 职责只有：保证 installation.yaml 中
 * - installation_id 正确（每台设备独立）
 * - sync_dir 正确（指向应用私有 staging 目录）
 *
 * 不负责执行同步。
 */
object RimeInstallationManager {

    private const val INSTALLATION_YAML = "installation.yaml"

    /**
     * 按固定顺序执行：
     * 创建运行目录与 staging 目录 → 迁移/生成 installation id →
     * 原子写入 installation.yaml。
     *
     * 调用方（Launcher）必须先复制 assets，再调用本方法，
     * 绝不能反过来，否则 assets 覆盖会把独立 UUID 重新覆盖成公共 UUID。
     */
    fun ensureInstallationConfig() {
        val context = Launcher.instance.context
        val rimeDir = File(CustomConstant.RIME_DICT_PATH)
        rimeDir.mkdirs()
        File(CustomConstant.RIME_SYNC_STAGING_PATH).mkdirs()

        val store = RimeSyncStateStore(context)
        val yamlFile = File(rimeDir, INSTALLATION_YAML)
        val content = if (yamlFile.exists()) yamlFile.readText() else ""
        val yamlId = readTopLevelYamlString(content, "installation_id")
        val stateBefore = store.load()

        // 7.4 migration 规则
        val installationId = when {
            // 首次升级老用户：设备本地还没有状态时
            stateBefore == null -> when {
                // yaml 仍是 APK 打包的公共 ID → 新生成 UUID
                yamlId == null || yamlId == CustomConstant.LEGACY_BUNDLED_RIME_INSTALLATION_ID ->
                    UUID.randomUUID().toString()
                // yaml 已是独立 ID（老版本/用户自己配置）→ 优先保留
                else -> yamlId
            }
            // 设备本地已有状态：状态是权威身份来源（防止备份/恢复克隆）
            else -> stateBefore.installationId
        }

        // 让 noBackupFilesDir 状态与最终写入 yaml 的 ID 保持一致
        if (stateBefore == null || stateBefore.installationId != installationId) {
            store.update { it.copy(installationId = installationId) }
        }

        var updated = replaceTopLevelYamlString(content, "installation_id", installationId)
        updated = replaceTopLevelYamlString(updated, "sync_dir", CustomConstant.RIME_SYNC_STAGING_PATH)

        if (updated != content) {
            writeAtomically(yamlFile, updated)
        }
    }

    /**
     * 只替换/追加顶层字符串字段，第一阶段不引入完整 YAML 重写器。
     * 输出使用单引号，并对 ' 做 YAML 单引号转义（''）。
     */
    private fun replaceTopLevelYamlString(
        content: String,
        key: String,
        value: String
    ): String {
        val escaped = value.replace("'", "''")
        val replacement = "$key: '$escaped'"
        val lines = content.lines().toMutableList()
        for (index in lines.indices) {
            val line = lines[index]
            val trimmed = line.trimStart()
            if (trimmed.startsWith("$key:") || trimmed.startsWith("$key ")) {
                val indent = line.take(line.length - trimmed.length)
                lines[index] = indent + replacement
                return lines.joinToString("\n")
            }
        }
        lines.add(replacement)
        return lines.joinToString("\n")
    }

    private fun readTopLevelYamlString(content: String, key: String): String? {
        for (line in content.lines()) {
            val trimmed = line.trim()
            if (trimmed.startsWith("$key:") || trimmed.startsWith("$key ")) {
                return trimmed.substringAfter(":").trim().trim('\'', '"')
            }
        }
        return null
    }

    /**
     * 写 installation.yaml.tmp，写成功后再原子替换正式文件。
     */
    private fun writeAtomically(target: File, content: String) {
        val tmp = File(target.parentFile, target.name + ".tmp")
        tmp.writeText(content)
        if (!tmp.renameTo(target)) {
            tmp.copyTo(target, overwrite = true)
            tmp.delete()
        }
    }
}
