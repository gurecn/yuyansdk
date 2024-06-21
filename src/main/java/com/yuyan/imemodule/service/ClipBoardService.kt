package com.yuyan.imemodule.service

import android.app.Service
import android.content.ClipboardManager
import android.content.ClipboardManager.OnPrimaryClipChangedListener
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import com.yuyan.imemodule.application.LauncherModel
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance

/**
 * 监听粘贴版
 *
 * @author KongXR
 */
class ClipBoardService : Service() {
    private var mClipboardManager: ClipboardManager? = null
    private var primaryClipChangedListener: PrimaryClipChangedListener? = null
    override fun onCreate() {
        super.onCreate()
        val isClipboardListening = getInstance().clipboard.clipboardListening.getValue()
        if (!isClipboardListening) {
            this.stopSelf()
            return
        }
        mClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        if (mClipboardManager == null) return
        primaryClipChangedListener = PrimaryClipChangedListener()
        mClipboardManager!!.addPrimaryClipChangedListener(primaryClipChangedListener)
    }

    internal inner class PrimaryClipChangedListener : OnPrimaryClipChangedListener {
        override fun onPrimaryClipChanged() {
            val data = mClipboardManager!!.primaryClip ?: return
            val item = data.getItemAt(0)
            var itemStr = ""
            if (item != null && !TextUtils.isEmpty(item.text)) {
                itemStr = item.text.toString()
            }
            val isClipboardListening = getInstance().clipboard.clipboardListening.getValue()
            if (!isClipboardListening) {
                if (mClipboardManager != null) mClipboardManager!!.removePrimaryClipChangedListener(
                    primaryClipChangedListener
                )
                this@ClipBoardService.stopSelf()
                return
            }
            if (item != null && !TextUtils.isEmpty(item.text)) {
                if (itemStr.length > 5000) {
                    itemStr = itemStr.substring(0, 5000)
                }
                LauncherModel.instance?.mClipboardDao?.insertClopboard(itemStr)
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        if (mClipboardManager != null) mClipboardManager!!.removePrimaryClipChangedListener(
            primaryClipChangedListener
        )
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
