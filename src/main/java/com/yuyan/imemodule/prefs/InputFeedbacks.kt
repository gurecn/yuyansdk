package com.yuyan.imemodule.prefs

import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.view.HapticFeedbackConstants
import android.view.View
import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.utils.audioManager
import com.yuyan.imemodule.utils.vibrator
import com.yuyan.imemodule.view.preference.ManagedPreference

object InputFeedbacks {

    enum class InputFeedbackMode {
        Enabled, Disabled;

        companion object : ManagedPreference.StringLikeCodec<InputFeedbackMode> {
            override fun decode(raw: String) = InputFeedbackMode.valueOf(raw)
        }
    }

    private val soundOnKeyPress by AppPrefs.getInstance().keyboardFeedback.soundOnKeyPress
    private val soundOnKeyPressVolume by AppPrefs.getInstance().keyboardFeedback.soundOnKeyPressVolume
    private val hapticOnKeyPress by AppPrefs.getInstance().keyboardFeedback.hapticOnKeyPress
    private val buttonPressVibrationAmplitude by AppPrefs.getInstance().keyboardFeedback.buttonPressVibrationAmplitude

    private val vibrator = ImeSdkApplication.context.vibrator

    private val hasAmplitudeControl =
        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) && vibrator.hasAmplitudeControl()

    private val audioManager = ImeSdkApplication.context.audioManager

    fun hapticFeedback(view: View) {
        when (hapticOnKeyPress) {
            InputFeedbackMode.Enabled -> {}
            InputFeedbackMode.Disabled -> return
        }
        val duration = buttonPressVibrationAmplitude.toLong()
        if (duration != 0L) {
            if (hasAmplitudeControl) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(duration)
            }
        } else {
            @Suppress("DEPRECATION")
            val flags = HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING or HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP, flags)
        }
    }

    enum class SoundEffect {
        Standard, SpaceBar, Delete, Return
    }

    fun soundEffect(effect: SoundEffect) {
        when (soundOnKeyPress) {
            InputFeedbackMode.Enabled -> {}
            InputFeedbackMode.Disabled -> return
        }
        val fx = when (effect) {
            SoundEffect.Standard -> AudioManager.FX_KEYPRESS_STANDARD
            SoundEffect.SpaceBar -> AudioManager.FX_KEYPRESS_SPACEBAR
            SoundEffect.Delete -> AudioManager.FX_KEYPRESS_DELETE
            SoundEffect.Return -> AudioManager.FX_KEYPRESS_RETURN
        }
        val volume = soundOnKeyPressVolume
        if (volume == 0) {
            audioManager.playSoundEffect(fx, -1f)
        } else {
            audioManager.playSoundEffect(fx, volume / 100f)
        }
    }

}