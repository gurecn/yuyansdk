package com.yuyan.imemodule.ui.fragment

import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.ui.fragment.base.ManagedPreferenceFragment
import com.yuyan.imemodule.utils.KeyboardLoaderUtil
import com.yuyan.imemodule.view.keyboard.KeyboardManager
import com.yuyan.imemodule.view.preference.ManagedPreference

class KeyboardSettingFragment : ManagedPreferenceFragment(AppPrefs.getInstance().keyboardSetting){

    override fun onStop() {
        super.onStop()
        EnvironmentSingleton.instance.initData()
        KeyboardLoaderUtil.instance.clearKeyboardMap()
        KeyboardManager.instance.clearKeyboard();
    }
}