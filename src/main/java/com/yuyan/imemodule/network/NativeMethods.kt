package com.yuyan.imemodule.network

class NativeMethods {
    external fun nativeHttpPost(jsonRequest: String?): String?
    external fun nativeHttpInit()
    external fun nativeHttpStop()

    companion object {
        init {
            System.loadLibrary("yuyannet")
        }
    }
}
