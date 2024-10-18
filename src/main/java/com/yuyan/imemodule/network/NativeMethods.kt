package com.yuyan.imemodule.network

import android.content.Context

class NativeMethods {
    external fun nativeHttpPost(jsonRequest: String?): String?
    external fun nativeHttpInit(context: Context?, mod: Int)
    external fun nativeHttpStop()

    companion object {
        init {
            System.loadLibrary("yuyannet")
        }
    }
}
