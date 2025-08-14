package com.yuyan.imemodule.utils

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

object AssetUtils {
    @JvmStatic
    fun copyFileOrDir(context: Context, parent: String, path: String, destParent: String, overwrite: Boolean) {
        val assetManager = context.assets
        try {
            val assetPath = File(parent, path).path
            val assets = assetManager.list(assetPath)
            if (assets.isNullOrEmpty()) {
                // Files
                copyFile(context, parent, path, destParent, overwrite)
            } else {
                // Dirs
                val dir = File(destParent, path)
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                for (asset in assets) {
                    val subPath = File(path, asset).path
                    copyFileOrDir(context, parent, subPath, destParent, overwrite)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun copyFile(context: Context, parentAssetPath: String, filename: String, destParent: String, overwrite: Boolean) {
        val assetManager = context.assets
        val inputStream: InputStream?
        val out: OutputStream?
        try {
            val assetPath = File(parentAssetPath, filename).path
            inputStream = assetManager.open(assetPath)
            val newFile = File(destParent, filename)
            if (newFile.exists() && !overwrite) return
            out = FileOutputStream(newFile)
            val buffer = ByteArray(1024)
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
            inputStream.close()
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
