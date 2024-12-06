package com.yuyan.imemodule.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yuyan.imemodule.R
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.utils.DevicesUtils
import com.yuyan.imemodule.utils.DevicesUtils.sp2px
import com.yuyan.imemodule.view.keyboard.KeyboardManager
import com.yuyan.imemodule.view.widget.seekbar.SignSeekBar
import splitties.dimensions.dp
import splitties.resources.color
import splitties.views.dsl.core.add
import splitties.views.dsl.core.lParams
import splitties.views.dsl.core.matchParent
import splitties.views.dsl.core.textView
import splitties.views.dsl.core.verticalLayout
import splitties.views.dsl.core.wrapContent


class KeyboardFeedbacksFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = with(requireContext()) {

        val soundOnKeyPress by AppPrefs.getInstance().internal.soundOnKeyPress
        val vibrationAmplitude by AppPrefs.getInstance().internal.vibrationAmplitude

        val soundSeekBar =  SignSeekBar(context)
        soundSeekBar.getConfigBuilder()
            .min(0f).max(40f).progress(soundOnKeyPress.toFloat()).sectionCount(4)
            .secondTrackColor(getColor(android.R.color.holo_green_light))
            .showThumbShadow(true)
            .bottomSidesLabels(resources.getStringArray(R.array.sound_labels))
            .thumbBgAlpha(0.3f)
            .thumbRadius(dp(2))
            .thumbRadiusOnDragging(dp(2))
            .thumbRatio(0.7f)
            .touchToSeek()
            .sectionTextSize(sp2px(5))
            .sectionTextPosition(SignSeekBar.TextPosition.BELOW_SECTION_MARK)
            .trackColor(getColor(R.color.color_gray))
            .trackSize(dp(2))
            .build()

        soundSeekBar.setOnProgressChangedListener(object : SignSeekBar.OnProgressChangedListener {
            override fun onProgressChanged(signSeekBar: SignSeekBar, progress: Int, progressFloat: Float, fromUser: Boolean) {}
            override fun getProgressOnFinally(signSeekBar: SignSeekBar, progress: Int, progressFloat: Float, fromUser: Boolean) {}
            override fun getProgressOnActionUp(signSeekBar: SignSeekBar, progress: Int, progressFloat: Float) {
                AppPrefs.getInstance().internal.soundOnKeyPress.setValue(progress)
                DevicesUtils.tryPlayKeyDown()
            }
        })

       val vibrateSeekBar =  SignSeekBar(context)
        vibrateSeekBar.getConfigBuilder()
            .min(0f).max(4f).progress(vibrationAmplitude.toFloat()).sectionCount(4)
            .secondTrackColor(getColor(android.R.color.holo_green_light))
            .showThumbShadow(true)
            .bottomSidesLabels(resources.getStringArray(R.array.vibrate_labels))
            .thumbBgAlpha(0.3f)
            .thumbRadius(dp(4))
            .thumbRadiusOnDragging(dp(10))
            .thumbRatio(0.7f)
            .touchToSeek()
            .sectionTextSize(sp2px(5))
            .autoAdjustSectionMark()
            .sectionTextPosition(SignSeekBar.TextPosition.BELOW_SECTION_MARK)
            .trackColor(getColor(R.color.color_gray))
            .trackSize(dp(4))
            .build()

        vibrateSeekBar.setOnProgressChangedListener(object : SignSeekBar.OnProgressChangedListener {
            override fun onProgressChanged(signSeekBar: SignSeekBar, progress: Int, progressFloat: Float, fromUser: Boolean) { }
            override fun getProgressOnActionUp(signSeekBar: SignSeekBar, progress: Int, progressFloat: Float) {}

            override fun getProgressOnFinally(signSeekBar: SignSeekBar, progress: Int, progressFloat: Float, fromUser: Boolean) {
                AppPrefs.getInstance().internal.vibrationAmplitude.setValue(progress)
                DevicesUtils.tryVibrate(signSeekBar)
            }
        })

        verticalLayout {
            addView(textView {
                setText(R.string.button_sound_volume)
                textSize = 16f
                setTextColor(context.color(R.color.skb_key_text_color))
            }, lParams(width = matchParent, height = wrapContent) {
                setMargins(dp(20), dp(20), dp(20), dp(10))
            })
            add(soundSeekBar, lParams(width = matchParent, height = wrapContent) {
                setMargins(dp(30), 0, dp(30), dp(10))
            })
            addView(textView {
                setText(R.string.button_vibration_amplitude)
                textSize = 16f
                setTextColor(context.color(R.color.skb_key_text_color))
            }, lParams(width = matchParent, height = wrapContent) {
                setMargins(dp(20), dp(20), dp(20), dp(10))
            })
            add(vibrateSeekBar, lParams(width = matchParent, height = wrapContent) {
                setMargins(dp(20), 0, dp(20), dp(10))
            })
        }
    }


    override fun onStop() {
        super.onStop()
        KeyboardManager.instance.clearKeyboard();
    }
}