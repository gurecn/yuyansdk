
package com.yuyan.imemodule.prefs

import androidx.annotation.Keep
import com.yuyan.imemodule.view.preference.ManagedPreference

class ManagedPreferenceVisibilityEvaluator(
    private vararg val providers: ManagedPreferenceProvider,
    private val onVisibilityChanged: (Map<String, Boolean>) -> Unit
) {

    private val visibility = mutableMapOf<String, Boolean>()

    // it would be better to declare the dependency relationship, rather than reevaluating on each value changed
    @Keep
    private val onValueChangeListener = ManagedPreference.OnChangeListener<Any> { _, _ ->
        evaluateVisibility()
    }

    init {
        for (provider in providers) {
            provider.managedPreferences.forEach { (_, pref) ->
                pref.registerOnChangeListener(onValueChangeListener)
            }
        }
    }

    fun evaluateVisibility() {
        val changed = mutableMapOf<String, Boolean>()
        for (provider in providers) {
            provider.managedPreferencesUi.forEach { ui ->
                val old = visibility[ui.key]
                val new = ui.isEnabled()
                if (old != null && old != new) {
                    changed[ui.key] = new
                }
                visibility[ui.key] = new
            }
        }
        if (changed.isNotEmpty())
            onVisibilityChanged(changed)
    }

    fun destroy() {
        for (provider in providers) {
            provider.managedPreferences.forEach { (_, pref) ->
                pref.unregisterOnChangeListener(onValueChangeListener)
            }
        }
    }

}