package com.yuyan.imemodule.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.yuyan.imemodule.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Context.toast(string: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, string, duration).show()
}

fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, resId, duration).show()
}

fun Context.toast(t: Throwable, duration: Int = Toast.LENGTH_SHORT) {
    toast(t.localizedMessage ?: t.stackTraceToString(), duration)
}

suspend fun <T> Context.toast(result: Result<T>, duration: Int = Toast.LENGTH_SHORT) {
    withContext(Dispatchers.Main.immediate) {
        result
            .onSuccess { toast(R.string.done, duration) }
            .onFailure { toast(it, duration) }
    }
}
