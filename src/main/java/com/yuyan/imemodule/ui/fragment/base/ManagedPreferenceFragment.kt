
package com.yuyan.imemodule.ui.fragment.base

import android.os.Build
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.ManagedPreferenceProvider
import com.yuyan.imemodule.prefs.ManagedPreferenceVisibilityEvaluator
import kotlinx.coroutines.launch

abstract class ManagedPreferenceFragment(private vararg val preferenceProvider: ManagedPreferenceProvider) :
    CsPreferenceFragment() {

    private val evaluator = ManagedPreferenceVisibilityEvaluator(*preferenceProvider) {
        lifecycleScope.launch {
            it.forEach { (key, enable) ->
                findPreference<Preference>(key)?.isEnabled = enable
            }
        }
    }

    open fun onPreferenceUiCreated(screen: PreferenceScreen) {}

    @CallSuper
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        evaluator.evaluateVisibility()
        preferenceScreen =
            preferenceManager.createPreferenceScreen(preferenceManager.context).also { screen ->
                for (provider in preferenceProvider) {
                    provider.createUi(screen)
                }
                onPreferenceUiCreated(screen)
            }
    }

    override fun onStop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            AppPrefs.getInstance().syncToDeviceEncryptedStorage()
        }
        super.onStop()
    }

    override fun onDestroy() {
        evaluator.destroy()
        super.onDestroy()
    }
}