
package com.yuyan.imemodule.ui.utils

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavDeepLinkBuilder
import com.yuyan.imemodule.R
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance
import com.yuyan.imemodule.ui.activity.SettingsActivity
import kotlin.system.exitProcess

object AppUtil {

    fun launchSettings(context: Context) {
        context.startActivity<SettingsActivity> {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        }
    }

    private fun launchMainToDest(context: Context, @IdRes dest: Int, arguments: Bundle? = null) {
        NavDeepLinkBuilder(context)
            .setComponentName(SettingsActivity::class.java)
            .setGraph(R.navigation.settings_nav)
            .setDestination(dest)
            .setArguments(arguments)
            .createTaskStackBuilder()
            /**
             * [androidx.core.app.TaskStackBuilder.getIntents] would add unwanted flags
             * [Intent.FLAG_ACTIVITY_CLEAR_TASK] and [Intent.FLAG_ACTIVITY_TASK_ON_HOME]
             * so we must launch the Intent by ourselves
             */
            .editIntentAt(0)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                context.startActivity(this)
            }
    }

    fun launchSettingsToHandwriting(context: Context) =
        launchMainToDest(context, R.id.handwritingSettingsFragment)


    fun launchSettingsToKeyboard(context: Context) =
        launchMainToDest(context, R.id.keyboardSettingsFragment)


    fun launchMainToThemeList(context: Context) =
        launchMainToDest(context, R.id.themeSettingsFragment)

    fun exit() {
        exitProcess(0)
    }
}