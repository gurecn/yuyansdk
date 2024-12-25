package com.yuyan.imemodule.ui.fragment.theme

import android.content.ComponentName
import android.content.pm.PackageManager
import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.ui.activity.LauncherActivity
import com.yuyan.imemodule.ui.fragment.base.ManagedPreferenceFragment
import com.yuyan.imemodule.view.preference.ManagedPreference


private val imeShowIcon = AppPrefs.getInstance().other.imeShowIcon

private val switchKeyListener = ManagedPreference.OnChangeListener<Boolean> { _, value ->
    val componentName = ComponentName(ImeSdkApplication.context.packageName, LauncherActivity::class.java.name)
    ImeSdkApplication.context.packageManager.setComponentEnabledSetting(componentName, if(value)PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
}
class OtherSettingsFragment: ManagedPreferenceFragment(AppPrefs.getInstance().other){
    override fun onStart() {
        super.onStart()
        imeShowIcon.registerOnChangeListener(switchKeyListener)
    }

    override fun onStop() {
        super.onStop()
        imeShowIcon.unregisterOnChangeListener(switchKeyListener)
    }
}