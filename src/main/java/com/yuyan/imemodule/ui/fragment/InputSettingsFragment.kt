package com.yuyan.imemodule.ui.fragment

import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.DoublePinyinSchemaMode
import com.yuyan.imemodule.ui.fragment.base.ManagedPreferenceFragment
import com.yuyan.imemodule.utils.KeyboardLoaderUtil
import com.yuyan.imemodule.view.keyboard.KeyboardManager
import com.yuyan.inputmethod.core.Kernel
import com.yuyan.imemodule.view.preference.ManagedPreference

class InputSettingsFragment: ManagedPreferenceFragment(AppPrefs.getInstance().input){

    private val chineseFanTi = AppPrefs.getInstance().input.chineseFanTi
    private val emojiInput = AppPrefs.getInstance().input.emojiInput
    private val doublePYSchemaMode = AppPrefs.getInstance().input.doublePYSchemaMode

    private val switchKeyListener = ManagedPreference.OnChangeListener<Boolean> { _, _ ->
        Kernel.nativeUpdateImeOption()
    }
    private val schemaModeListener = ManagedPreference.OnChangeListener<DoublePinyinSchemaMode> { _, doublePYSchemaMode ->
        val doublePYSchema = CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY + doublePYSchemaMode
        val inputMode = 0x1000 or InputModeSwitcherManager.MASK_LANGUAGE_CN or InputModeSwitcherManager.MASK_CASE_UPPER
        AppPrefs.getInstance().internal.inputMethodPinyinMode.setValue(inputMode)
        AppPrefs.getInstance().internal.pinyinModeRime.setValue(doublePYSchema)
        KeyboardLoaderUtil.instance.clearKeyboardMap()
        KeyboardManager.instance.clearKeyboard()
        Kernel.initImeSchema(doublePYSchema)
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