
package com.yuyan.imemodule.ui.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.inputmethod.InputMethodSubtype
import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.service.ImeService
import com.yuyan.imemodule.utils.getSecureSettings
import com.yuyan.imemodule.utils.inputMethodManager

object InputMethodUtil {

    @JvmField
    val serviceName: String = ImeService::class.java.name

    @JvmField
    val componentName: String =
        ComponentName(ImeSdkApplication.context, ImeService::class.java).flattenToShortString()

    fun isEnabled(): Boolean {
        return ImeSdkApplication.context.inputMethodManager.enabledInputMethodList.any {
            it.packageName == ImeSdkApplication.context.packageName && it.serviceName == serviceName
        }
    }

    fun isSelected(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ImeSdkApplication.context.inputMethodManager.currentInputMethodInfo?.let {
                it.packageName == ImeSdkApplication.context.packageName && it.serviceName == serviceName
            } ?: false
        } else {
            getSecureSettings(Settings.Secure.DEFAULT_INPUT_METHOD) == componentName
        }
    }

    fun startSettingsActivity(context: Context) =
        context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })

    fun showPicker() = ImeSdkApplication.context.inputMethodManager.showInputMethodPicker()

    fun firstVoiceInput(): Pair<String, InputMethodSubtype>? =
        ImeSdkApplication.context.inputMethodManager
            .shortcutInputMethodsAndSubtypes
            .firstNotNullOfOrNull {
                it.value.find { subType -> subType.mode.lowercase() == "voice" }
                    ?.let { subType -> it.key.id to subType }
            }

    fun switchInputMethod(
        service: ImeService,
        id: String,
        subtype: InputMethodSubtype
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            service.switchInputMethod(id, subtype)
        } else {
            @Suppress("DEPRECATION")
            ImeSdkApplication.context.inputMethodManager
                .setInputMethodAndSubtype(service.window.window!!.attributes.token, id, subtype)
        }
    }
}