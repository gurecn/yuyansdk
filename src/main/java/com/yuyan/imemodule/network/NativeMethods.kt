package com.yuyan.imemodule.network

class NativeMethods {
    external fun nativeHttpPost(jsonRequest: String?): String?

    companion object {
        init {
            System.loadLibrary("yuyannet")
        }
    }
}
