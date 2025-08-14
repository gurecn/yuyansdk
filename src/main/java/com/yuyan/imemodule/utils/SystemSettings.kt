

package com.yuyan.imemodule.utils

import android.provider.Settings
import com.yuyan.imemodule.application.Launcher

fun getSecureSettings(name: String): String? {
    return Settings.Secure.getString(Launcher.instance.context.contentResolver, name)
}
