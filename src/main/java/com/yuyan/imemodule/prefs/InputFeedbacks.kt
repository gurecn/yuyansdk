package com.yuyan.imemodule.prefs

import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.View
import com.yuyan.imemodule.application.Launcher
import com.yuyan.imemodule.utils.audioManager
import com.yuyan.imemodule.utils.vibrator

object InputFeedbacks {

    private val soundOnKeyPress by AppPrefs.getInstance().internal.soundOnKeyPress
    private val vibrationAmplitude by AppPrefs.getInstance().internal.vibrationAmplitude

    private val vibrator = Launcher.instance.context.vibrator
    private val hasAmplitudeControl = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && vibrator.hasAmplitudeControl()
    private val audioManager = Launcher.instance.context.audioManager

    /**
     * 触感反馈
     */
    fun hapticFeedback(view: View?) {
        view ?: return
        val duration = when (vibrationAmplitude) {
            0 -> {
                @Suppress("DEPRECATION")
                val flags = HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING or HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP, flags)
                0L
            }
            1 -> 0L
            2 -> 5L
            3 -> 30L
            else -> 50L
        }
        if (duration != 0L) {
            if (hasAmplitudeControl) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(duration)
            }
        }
    }

    enum class SoundEffect {
        Standard, SpaceBar, Delete, Return;

        companion object {
            fun fromKeyCode(keyCode: Int): SoundEffect = when (keyCode) {
                KeyEvent.KEYCODE_DEL -> Delete
                KeyEvent.KEYCODE_SPACE -> SpaceBar
                KeyEvent.KEYCODE_ENTER -> Return
                else -> Standard
            }
        }
    }

    fun soundEffect(effect: SoundEffect) {
        val fx = when (effect) {
            SoundEffect.Standard -> AudioManager.FX_KEYPRESS_STANDARD
            SoundEffect.SpaceBar -> AudioManager.FX_KEYPRESS_SPACEBAR
            SoundEffect.Delete -> AudioManager.FX_KEYPRESS_DELETE
            SoundEffect.Return -> AudioManager.FX_KEYPRESS_RETURN
        }
        if (soundOnKeyPress < 5) {
            audioManager.playSoundEffect(fx, -1f)
        } else if (soundOnKeyPress < 10) {
            return
        } else {
            audioManager.playSoundEffect(fx, (soundOnKeyPress - 10) / 30f)
        }
    }

    /**
     * 点击反馈：音效 + 触感，键盘按键、候选词、符号等点击统一走这里
     */
    fun clickFeedback(view: View?, keyCode: Int = KeyEvent.KEYCODE_UNKNOWN) {
        soundEffect(SoundEffect.fromKeyCode(keyCode))
        hapticFeedback(view)
    }

}