package com.yuyan.imemodule.ui.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yuyan.imemodule.R
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.ui.fragment.base.ManagedPreferenceFragment

class KeyboardFeedbacksFragment : ManagedPreferenceFragment(AppPrefs.getInstance().keyboardFeedback){
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.keyboard_feedback)
    }
}