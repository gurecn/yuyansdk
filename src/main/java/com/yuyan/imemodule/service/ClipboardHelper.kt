package com.yuyan.imemodule.service

import android.content.ClipboardManager.OnPrimaryClipChangedListener
import com.yuyan.imemodule.application.Launcher
import com.yuyan.imemodule.database.DataBaseKT
import com.yuyan.imemodule.database.entry.Clipboard
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.utils.clipboardManager
import kotlin.math.max

/**
 * 剪切板监听
 * 移除使用广播监听方式，解决部分手机后台无法启动监听服务异常(API level 31)。
 */
object ClipboardHelper : OnPrimaryClipChangedListener {

    fun init() {
        Launcher.instance.context.clipboardManager.addPrimaryClipChangedListener(this)
    }

    override fun onPrimaryClipChanged() {
        val isClipboardListening = AppPrefs.getInstance().clipboard.clipboardListening.getValue()
        if(isClipboardListening) {
           val item = Launcher.instance.context.clipboardManager.primaryClip?.getItemAt(0)
            item?.takeIf { it.text?.isNotBlank() == true }?.let {
                    val data = it.text.toString().take(20000)
                    DataBaseKT.instance.clipboardDao().insert(Clipboard(content = data))
                    val num = max(DataBaseKT.instance.clipboardDao().getCount() - AppPrefs.getInstance().clipboard.clipboardHistoryLimit.getValue(), 0)
                    DataBaseKT.instance.clipboardDao().deleteOldest(num)
                    if (AppPrefs.getInstance().clipboard.clipboardSuggestion.getValue()) {
                        AppPrefs.getInstance().internal.clipboardUpdateTime.setValue(System.currentTimeMillis())
                        AppPrefs.getInstance().internal.clipboardUpdateContent.setValue(data)
                    }
                }
        }
    }
}
