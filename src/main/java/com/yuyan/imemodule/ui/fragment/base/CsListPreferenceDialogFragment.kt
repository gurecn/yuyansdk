
package com.yuyan.imemodule.ui.fragment.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.preference.ListPreference
import androidx.preference.ListPreferenceDialogFragmentCompat
import com.yuyan.imemodule.R
import com.yuyan.imemodule.prefs.fixDialogMargin
import com.yuyan.imemodule.prefs.restore

class CsListPreferenceDialogFragment : ListPreferenceDialogFragmentCompat() {
    override fun onPrepareDialogBuilder(builder: AlertDialog.Builder) {
        val p = preference as ListPreference
        builder.setNeutralButton(R.string.default_) { _, _ ->
            p.restore()
        }
        super.onPrepareDialogBuilder(builder)
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        fixDialogMargin(view)
    }

    companion object {
        fun newInstance(key: String): CsListPreferenceDialogFragment {
            val fragment = CsListPreferenceDialogFragment()
            val b = Bundle(1)
            b.putString(ARG_KEY, key)
            fragment.arguments = b
            return fragment
        }
    }
}