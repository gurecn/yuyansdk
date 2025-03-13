package com.yuyan.imemodule.utils

import android.provider.Settings
import com.yuyan.imemodule.application.ImeSdkApplication

fun errInvalidType(cls: Class<*>): Nothing {
    throw IllegalArgumentException("Invalid settings type ${cls.name}")
}

inline fun <reified T> getGlobalSettings(name: String): T {
    return when (T::class.java) {
        String::class.java -> Settings.Global.getString(ImeSdkApplication.context.contentResolver, name)
        Float::class.javaObjectType -> Settings.Global.getFloat(ImeSdkApplication.context.contentResolver, name, 0f)
        Long::class.javaObjectType -> Settings.Global.getLong(ImeSdkApplication.context.contentResolver, name, 0L)
        Int::class.javaObjectType -> Settings.Global.getInt(ImeSdkApplication.context.contentResolver, name, 0)
        else -> errInvalidType(T::class.java)
    } as T
}