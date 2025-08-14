package com.yuyan.imemodule.ui.fragment.theme

import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.keyboard.KeyboardManager
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.SkbStyleMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.ui.fragment.base.ManagedPreferenceFragment
import com.yuyan.imemodule.utils.KeyboardLoaderUtil
import com.yuyan.imemodule.view.preference.ManagedPreference


private val skbStyleMode = ThemeManager.prefs.skbStyleMode

private val skbStyleModeListener = ManagedPreference.OnChangeListener<SkbStyleMode> { _, value ->
   when(value){
       SkbStyleMode.Samsung ->{
           AppPrefs.getInstance().keyboardSetting.abcNumberLine.setValue(true)
       }
       else ->{
           AppPrefs.getInstance().keyboardSetting.abcNumberLine.setValue(false)
       }
   }
    EnvironmentSingleton.instance.initData()
    KeyboardLoaderUtil.instance.clearKeyboardMap()
    KeyboardManager.instance.clearKeyboard()
}

class ThemeSettingsFragment : ManagedPreferenceFragment(ThemeManager.prefs){

    override fun onStart() {
        super.onStart()
        skbStyleMode.registerOnChangeListener(skbStyleModeListener)
    }

    override fun onStop() {
        super.onStop()
        skbStyleMode.unregisterOnChangeListener(skbStyleModeListener)
    }
}