package com.yuyan.imemodule.ui.fragment

import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.ui.fragment.base.ManagedPreferenceFragment
import com.yuyan.inputmethod.core.Kernel
import com.yuyan.imemodule.view.preference.ManagedPreference

class InputSettingsFragment: ManagedPreferenceFragment(AppPrefs.getInstance().input){

    private val chineseFanTi = AppPrefs.getInstance().input.chineseFanTi
    private val emojiInput = AppPrefs.getInstance().input.emojiInput

    private val switchKeyListener = ManagedPreference.OnChangeListener<Boolean> { key, v ->
        Kernel.nativeUpdateImeOption()
    }

    override fun onStart() {
        super.onStart()
        chineseFanTi.registerOnChangeListener(switchKeyListener)
        emojiInput.registerOnChangeListener(switchKeyListener)
    }

    override fun onStop() {
        super.onStop()
        chineseFanTi.unregisterOnChangeListener(switchKeyListener)
        emojiInput.unregisterOnChangeListener(switchKeyListener)
    }
}