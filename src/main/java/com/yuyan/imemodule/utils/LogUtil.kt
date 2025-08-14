package com.yuyan.imemodule.utils

import android.util.Log
import com.yuyan.imemodule.BuildConfig

/**
 * 日志管理工具
 */
object LogUtil {
    private var DEBUG = BuildConfig.DEBUG
    private val LOGV = DEBUG
    private val LOGD = DEBUG
    private val LOGI = DEBUG
    private val LOGW = DEBUG
    private val LOGE = DEBUG
    private val BASE_TAG = LogUtil::class.java.getSimpleName()
    fun setOpenLog(isOpen: Boolean) {
        DEBUG = isOpen
    }

    fun v(TAG: String, method: String, msg: String) {
        if (LOGV) {
            Log.v(BASE_TAG, "$TAG.$method()-->$msg")
        }
    }

    @JvmStatic
    fun d(TAG: String, msg: String) {
        if (LOGD) {
            Log.d(BASE_TAG, "$TAG-->$msg")
        }
    }

    @JvmStatic
    fun d(TAG: String, method: String, msg: String) {
        if (LOGD) {
            Log.d(BASE_TAG, "$TAG.$method()-->$msg")
        }
    }

    fun i(TAG: String, method: String, msg: String) {
        if (LOGI) {
            Log.i(BASE_TAG, "$TAG.$method()-->$msg")
        }
    }

    fun w(TAG: String, method: String, msg: String) {
        if (LOGW) {
            Log.w(BASE_TAG, "$TAG.$method()-->$msg")
        }
    }

    @JvmStatic
    fun e(TAG: String, method: String, msg: String) {
        if (LOGE) {
            Log.e(BASE_TAG, "$TAG.$method()-->$msg")
        }
    }
}
