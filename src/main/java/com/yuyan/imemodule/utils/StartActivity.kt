package com.yuyan.imemodule.utils

import android.app.Activity
import android.content.Context
import android.content.Intent

inline fun <reified T : Activity> Context.startActivity(setupIntent: Intent.() -> Unit = {}) {
    startActivity(Intent(this, T::class.java).apply(setupIntent))
}
