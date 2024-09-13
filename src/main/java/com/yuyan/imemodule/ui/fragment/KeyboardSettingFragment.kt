package com.yuyan.imemodule.ui.fragment

import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.ui.fragment.base.ManagedPreferenceFragment
import com.yuyan.imemodule.view.keyboard.KeyboardManager
import com.yuyan.imemodule.view.preference.ManagedPreference

class KeyboardSettingFragment : ManagedPreferenceFragment(AppPrefs.getInstance().keyboardSetting){

    private val switchIMEKey = AppPrefs.getInstance().keyboardSetting.switchIMEKey
    private val switchKeyListener = ManagedPreference.OnChangeListener<Boolean> { _, _ ->
        KeyboardManager.instance.clearKeyboard()
    }

    override fun onStart() {
        super.onStart()
        switchIMEKey.registerOnChangeListener(switchKeyListener)
    }

    override fun onStop() {
        super.onStop()
        switchIMEKey.unregisterOnChangeListener(switchKeyListener)
    }
}