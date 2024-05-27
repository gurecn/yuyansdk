

package com.yuyan.imemodule.utils

import android.provider.Settings
import com.yuyan.imemodule.application.ImeSdkApplication

fun getSecureSettings(name: String): String? {
    return Settings.Secure.getString(ImeSdkApplication.context.contentResolver, name)
}
