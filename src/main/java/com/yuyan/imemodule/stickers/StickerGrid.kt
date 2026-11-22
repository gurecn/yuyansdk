package com.yuyan.imemodule.stickers

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import kotlinx.coroutines.*
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import java.io.File
import splitties.dimensions.dp
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme

class StickerGrid(context: Context, private val insert: (File) -> Boolean) : GridView(context) {
    private val imageJobs = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val previews = object : LruCache<String, Bitmap>(4 * 1024 * 1024) {
        override fun sizeOf(key: String, value: Bitmap) = value.byteCount
    }
    private val directory = File(context.filesDir, "stickers").apply { mkdirs() }
    @Suppress("DEPRECATION")
    private val observer = object : android.os.FileObserver(directory.path, android.os.FileObserver.MOVED_TO or android.os.FileObserver.DELETE) {
        override fun onEvent(event: Int, path: String?) {
            if (path?.endsWith(".png") == true) post { refresh() }
        }
    }
    private var files = emptyList<File>()
    private val tiles = object : BaseAdapter() {
        override fun getCount() = files.size + 1
        override fun getItem(position: Int) = files.getOrNull(position - 1)
        override fun getItemId(position: Int) = position.toLong()
        override fun getView(position: Int, recycled: View?, parent: ViewGroup): View {
            val file = getItem(position)
            val tile = if (file == null) TextView(context).apply {
                text = "＋ 添加"
                gravity = android.view.Gravity.CENTER
                setTextColor(activeTheme.keyTextColor)
            } else (recycled as? ImageView ?: ImageView(context)).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                contentDescription = "表情 $position，长按分享"
                (tag as? Job)?.cancel()
                val preview = previews.get(file.name)
                setImageBitmap(preview)
                if (preview == null) tag = imageJobs.launch {
                    val bitmap = withContext(Dispatchers.IO) {
                        BitmapFactory.decodeFile(file.path, BitmapFactory.Options().apply { inSampleSize = 4 })
                    } ?: return@launch
                    previews.put(file.name, bitmap)
                    setImageBitmap(bitmap)
                }
            }
            tile.layoutParams = AbsListView.LayoutParams(-1, dp(60))
            return tile
        }
    }

    init {
        numColumns = AUTO_FIT
        columnWidth = dp(68)
        adapter = tiles
        setOnItemClickListener { _, _, position, _ ->
            val file = tiles.getItem(position)
            if (file == null) context.startActivity(Intent(context, StickerImportActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            else if (!runCatching { insert(file) }.getOrDefault(false))
                Toast.makeText(context, "当前输入框未接受图片，可长按表情分享", Toast.LENGTH_LONG).show()
        }
        setOnItemLongClickListener { _, tile, position, _ ->
            val file = tiles.getItem(position) ?: return@setOnItemLongClickListener false
            PopupMenu(context, tile).apply {
                menu.add("分享").setOnMenuItemClickListener { share(file); true }
                menu.add("移除收藏").setOnMenuItemClickListener {
                    if (file.delete()) refresh()
                    else Toast.makeText(context, "移除失败", Toast.LENGTH_SHORT).show()
                    true
                }
                show()
            }
            true
        }
    }

    private fun share(file: File) {
            val uri = FileProvider.getUriForFile(context, context.packageName + ".stickers", file)
            val share = Intent(Intent.ACTION_SEND).setType("image/png").putExtra(Intent.EXTRA_STREAM, uri)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(Intent.createChooser(share, "分享表情").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun refresh() {
        files = directory.listFiles { f -> f.extension == "png" }
            ?.sortedByDescending { it.lastModified() }.orEmpty()
        tiles.notifyDataSetChanged()
    }

    override fun onDetachedFromWindow() {
        observer.stopWatching()
        imageJobs.coroutineContext.cancelChildren()
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        observer.startWatching()
        if (isShown) refresh()
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus && isShown) refresh()
    }
}
