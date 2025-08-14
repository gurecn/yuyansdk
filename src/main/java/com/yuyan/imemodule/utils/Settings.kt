package com.yuyan.imemodule.utils

import android.provider.Settings
import com.yuyan.imemodule.application.Launcher

fun errInvalidType(cls: Class<*>): Nothing {
    throw IllegalArgumentException("Invalid settings type ${cls.name}")
}

inline fun <reified T> getGlobalSettings(name: String): T {
    return when (T::class.java) {
        String::class.java -> Settings.Global.getString(Launcher.instance.context.contentResolver, name)
        Float::class.javaObjectType -> Settings.Global.getFloat(Launcher.instance.context.contentResolver, name, 0f)
        Long::class.javaObjectType -> Settings.Global.getLong(Launcher.instance.context.contentResolver, name, 0L)
        Int::class.javaObjectType -> Settings.Global.getInt(Launcher.instance.context.contentResolver, name, 0)
        else -> errInvalidType(T::class.java)
    } as T
}