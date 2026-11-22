package com.yuyan.imemodule.stickers

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID

class StickerProvider : FileProvider()

class StickerImportActivity : Activity() {
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        if (state != null) {
            if (state.getBoolean("importing")) finish()
            return
        }
        val picker = if (Build.VERSION.SDK_INT >= 33)
            Intent(MediaStore.ACTION_PICK_IMAGES).setType("image/*")
        else Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("image/*")
        if (Build.VERSION.SDK_INT >= 33) picker.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, minOf(20, MediaStore.getPickImagesMaxLimit()))
        else picker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(picker, 1)
    }

    private var importing = false

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("importing", importing)
    }

    @Deprecated("Activity result callback")
    override fun onActivityResult(request: Int, result: Int, data: Intent?) {
        super.onActivityResult(request, result, data)
        val clip = data?.clipData
        val images = if (clip != null) (0 until minOf(20, clip.itemCount)).mapNotNull { clip.getItemAt(it).uri }
            else listOfNotNull(data?.data)
        if (result != RESULT_OK || images.isEmpty()) { finish(); return }
        importing = true
        Thread {
            val imported = images.count { runCatching { importImage(it) }.isSuccess }
            runOnUiThread {
                Toast.makeText(this, "已添加 $imported / ${images.size} 张表情", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.start()
    }

    private fun importImage(uri: Uri) {
        require(uri.scheme == "content")
        val bitmap = if (Build.VERSION.SDK_INT >= 28) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri)) { decoder, info, _ ->
                val longest = maxOf(info.size.width, info.size.height)
                decoder.setTargetSampleSize(maxOf(1, (longest + 1023) / 1024))
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            require(bounds.outWidth > 0 && bounds.outHeight > 0)
            val options = BitmapFactory.Options()
            while (maxOf(bounds.outWidth, bounds.outHeight) / options.inSampleSize.coerceAtLeast(1) > 1024)
                options.inSampleSize = options.inSampleSize.coerceAtLeast(1) * 2
            requireNotNull(contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) })
        }
        val dir = File(filesDir, "stickers").apply { mkdirs() }
        val temp = File.createTempFile("import", ".tmp", dir)
        try {
            temp.outputStream().use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
            check(temp.renameTo(File(dir, UUID.randomUUID().toString() + ".png")))
        } finally {
            temp.delete()
            bitmap.recycle()
        }
    }
}
