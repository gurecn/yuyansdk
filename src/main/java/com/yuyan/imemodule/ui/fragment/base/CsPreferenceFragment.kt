
package com.yuyan.imemodule.ui.fragment.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.yuyan.imemodule.view.widget.applyNavBarInsetsBottomPadding

abstract class CsPreferenceFragment : PreferenceFragmentCompat() {

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = super.onCreateView(inflater, container, savedInstanceState).apply {
        listView.applyNavBarInsetsBottomPadding()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("RestrictedApi")
    override fun onDisplayPreferenceDialog(preference: Preference) {

        var handled = false
        if (callbackFragment is OnPreferenceDisplayDialogCallback) {
            handled =
                (callbackFragment as OnPreferenceDisplayDialogCallback).onPreferenceDisplayDialog(
                    this,
                    preference
                )
        }

        var callbackFragment: Fragment? = this
        while (!handled && callbackFragment != null) {
            if (callbackFragment is OnPreferenceDisplayDialogCallback) {
                handled = (callbackFragment as OnPreferenceDisplayDialogCallback)
                    .onPreferenceDisplayDialog(this, preference)
            }
            callbackFragment = callbackFragment.parentFragment
        }
        if (!handled && context is OnPreferenceDisplayDialogCallback) {
            handled = (context as OnPreferenceDisplayDialogCallback)
                .onPreferenceDisplayDialog(this, preference)
        }
        if (!handled && activity is OnPreferenceDisplayDialogCallback) {
            handled = (activity as OnPreferenceDisplayDialogCallback)
                .onPreferenceDisplayDialog(this, preference)
        }

        if (handled) {
            return
        }

        if (parentFragmentManager.findFragmentByTag(javaClass.name) != null) {
            return
        }

        val f: DialogFragment = when (preference) {
            is EditTextPreference -> {
                CsEditTextPreferenceDialogFragment.newInstance(preference.getKey())
            }
            is ListPreference -> {
                CsListPreferenceDialogFragment.newInstance(preference.getKey())
            }
            else -> {
                throw IllegalArgumentException(
                    "Cannot display dialog for an unknown Preference type: ${preference.javaClass.name}"
                )
            }
        }
        f.setTargetFragment(this, 0)
        f.show(parentFragmentManager, javaClass.name)
    }
}