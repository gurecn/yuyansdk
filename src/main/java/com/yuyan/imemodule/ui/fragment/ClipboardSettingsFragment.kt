package com.yuyan.imemodule.ui.fragment

import android.content.Intent
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.service.ClipBoardService
import com.yuyan.imemodule.ui.fragment.base.ManagedPreferenceFragment
import com.yuyan.imemodule.view.preference.ManagedPreference

class ClipboardSettingsFragment: ManagedPreferenceFragment(AppPrefs.getInstance().clipboard){

    private val clipboardListening = AppPrefs.getInstance().clipboard.clipboardListening
    private val clipboardSwitchKeyListener = ManagedPreference.OnChangeListener<Boolean> { _, v ->
        val intent = Intent(context, ClipBoardService::class.java)
        if(!v) context?.stopService(intent)
        else context?.startService(intent)
    }

    override fun onStart() {
        super.onStart()
        clipboardListening.registerOnChangeListener(clipboardSwitchKeyListener)
        AppPrefs.getInstance().input
    }

    override fun onStop() {
        super.onStop()
        clipboardListening.unregisterOnChangeListener(clipboardSwitchKeyListener)
    }
}