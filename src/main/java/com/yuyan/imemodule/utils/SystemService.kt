package com.yuyan.imemodule.utils

import android.app.NotificationManager
import android.content.ClipboardManager
import android.content.Context
import android.media.AudioManager
import android.os.Vibrator
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService

val Context.audioManager
    get() = getSystemService<AudioManager>()!!

val Context.clipboardManager
    get() = getSystemService<ClipboardManager>()!!

val Context.inputMethodManager
    get() = getSystemService<InputMethodManager>()!!

val Context.notificationManager
    get() = getSystemService<NotificationManager>()!!

val Context.vibrator
    get() = getSystemService<Vibrator>()!!