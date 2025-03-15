
package com.yuyan.imemodule.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.ImeSdkApplication

class SettingsViewModel : ViewModel() {

    val toolbarTitle = MutableLiveData(ImeSdkApplication.context.getString(R.string.input_methods))

    val toolbarShadow = MutableLiveData(true)

    fun enableToolbarShadow() {
        toolbarShadow.value = true
    }

    fun disableToolbarShadow() {
        toolbarShadow.value = false
    }

    override fun onCleared() {
    }
}