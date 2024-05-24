
package com.yuyan.imemodule.prefs

import android.content.SharedPreferences
import com.yuyan.imemodule.view.preference.ManagedPreference

abstract class ManagedPreferenceInternal(private val sharedPreferences: SharedPreferences) :
    ManagedPreferenceProvider() {

    protected fun int(key: String, defaultValue: Int) =
        ManagedPreference.PInt(sharedPreferences, key, defaultValue).apply { register() }

    protected fun string(key: String, defaultValue: String) =
        ManagedPreference.PString(sharedPreferences, key, defaultValue).apply { register() }


    protected fun long(key: String, defaultValue: Long) =
        ManagedPreference.PLong(sharedPreferences, key, defaultValue).apply { register() }

    protected fun <T : Any> stringLike(
        key: String,
        codec: ManagedPreference.StringLikeCodec<T>,
        defaultValue: T
    ) = ManagedPreference.PStringLike(sharedPreferences, key, defaultValue, codec)
        .apply { register() }

    protected fun bool(key: String, defaultValue: Boolean) =
        ManagedPreference.PBool(sharedPreferences, key, defaultValue).apply { register() }

    protected fun float(key: String, defaultValue: Float) =
        ManagedPreference.PFloat(sharedPreferences, key, defaultValue).apply { register() }
}