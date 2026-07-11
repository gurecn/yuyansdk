package com.yuyan.imemodule.ui.fragment

import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.imemodule.manager.InputModeSwitcher
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.DoublePinyinSchemaMode
import com.yuyan.imemodule.service.InputDispatcher
import com.yuyan.imemodule.ui.fragment.base.ManagedPreferenceFragment
import com.yuyan.imemodule.view.preference.ManagedPreference

class InputSettingsFragment: ManagedPreferenceFragment(AppPrefs.getInstance().input){

    private val chineseFanTi = AppPrefs.getInstance().input.chineseFanTi
    private val emojiInput = AppPrefs.getInstance().input.emojiInput
    private val doublePYSchemaMode = AppPrefs.getInstance().input.doublePYSchemaMode

    private val switchKeyListener = ManagedPreference.OnChangeListener<Boolean> { _, _ ->
        InputDispatcher.nativeUpdateImeOption()
    }
    private val schemaModeListener = ManagedPreference.OnChangeListener<DoublePinyinSchemaMode> { _, doublePYSchemaMode ->
        InputModeSwitcher.switchModeForSetting(Pair(InputModeSwitcher.MASK_SKB_LAYOUT_QWERTY_PINYIN, CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY + doublePYSchemaMode))
    }

    override fun onStart() {
        super.onStart()
        chineseFanTi.registerOnChangeListener(switchKeyListener)
        emojiInput.registerOnChangeListener(switchKeyListener)
        doublePYSchemaMode.registerOnChangeListener(schemaModeListener)
    }

    override fun onStop() {
        super.onStop()
        chineseFanTi.unregisterOnChangeListener(switchKeyListener)
        emojiInput.unregisterOnChangeListener(switchKeyListener)
        doublePYSchemaMode.unregisterOnChangeListener(schemaModeListener)
    }
}