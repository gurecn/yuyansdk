package com.yuyan.imemodule.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.yuyan.imemodule.R
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.utils.DevicesUtils
import com.yuyan.imemodule.utils.DevicesUtils.sp2px
import com.yuyan.imemodule.keyboard.KeyboardManager
import com.yuyan.imemodule.view.widget.seekbar.SignSeekBar
import splitties.dimensions.dp
import splitties.views.dsl.appcompat.switch
import splitties.views.dsl.constraintlayout.above
import splitties.views.dsl.constraintlayout.before
import splitties.views.dsl.constraintlayout.below
import splitties.views.dsl.constraintlayout.constraintLayout
import splitties.views.dsl.constraintlayout.endOfParent
import splitties.views.dsl.constraintlayout.lParams
import splitties.views.dsl.constraintlayout.matchConstraints
import splitties.views.dsl.constraintlayout.startOfParent
import splitties.views.dsl.constraintlayout.topOfParent
import splitties.views.dsl.constraintlayout.topToTopOf
import splitties.views.dsl.core.add
import splitties.views.dsl.core.matchParent
import splitties.views.dsl.core.textView
import splitties.views.dsl.core.wrapContent
import splitties.views.gravityVerticalCenter


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

        val soundSwitch= switch {
            isChecked = soundOnKeyPress < 5 || soundOnKeyPress > 10
            soundSeekBar.isEnabled = soundOnKeyPress < 5 || soundOnKeyPress > 10
            setOnCheckedChangeListener { _, isChecked ->
                if(isChecked){
                    soundSeekBar.setProgress(0f)
                    AppPrefs.getInstance().internal.soundOnKeyPress.setValue(0)
                    soundSeekBar.isEnabled = true
                } else {
                    soundSeekBar.setProgress(10f)
                    AppPrefs.getInstance().internal.soundOnKeyPress.setValue(10)
                    soundSeekBar.isEnabled = false
                }
            }
        }

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

        val vibrationSwitch = switch {
            isChecked = vibrationAmplitude != 1
            vibrateSeekBar.isEnabled = vibrationAmplitude != 1
            setOnCheckedChangeListener { _, isChecked ->
                if(isChecked){
                    vibrateSeekBar.setProgress(0f)
                    AppPrefs.getInstance().internal.vibrationAmplitude.setValue(0)
                    vibrateSeekBar.isEnabled = true
                } else {
                    vibrateSeekBar.setProgress(1f)
                    AppPrefs.getInstance().internal.vibrationAmplitude.setValue(1)
                    vibrateSeekBar.isEnabled = false
                }
            }
        }

        vibrateSeekBar.setOnProgressChangedListener(object : SignSeekBar.OnProgressChangedListener {
            override fun onProgressChanged(signSeekBar: SignSeekBar, progress: Int, progressFloat: Float, fromUser: Boolean) { }
            override fun getProgressOnActionUp(signSeekBar: SignSeekBar, progress: Int, progressFloat: Float) {}

            override fun getProgressOnFinally(signSeekBar: SignSeekBar, progress: Int, progressFloat: Float, fromUser: Boolean) {
                AppPrefs.getInstance().internal.vibrationAmplitude.setValue(progress)
                DevicesUtils.tryVibrate(signSeekBar)
            }
        })

        val soundTitle = textView {
            setText(R.string.button_sound_volume)
            textSize = 16f
            gravity = gravityVerticalCenter
        }

        val vibrationTitle = textView {
            setText(R.string.button_vibration_amplitude)
            textSize = 16f
            gravity = gravityVerticalCenter
        }

        constraintLayout {
            val lineHeight = dp(48)
            val itemMargin = dp(30)
            add(soundTitle, lParams(matchConstraints, lineHeight) {
                topOfParent()
                startOfParent(itemMargin)
                before(soundSwitch)
                above(soundSeekBar)
            })
            add(soundSwitch, lParams(matchConstraints, lineHeight) {
                topToTopOf(soundTitle)
                endOfParent(itemMargin)
            })
            add(soundSeekBar, lParams(width = matchParent, height = wrapContent) {
                below(soundTitle)
                startOfParent(itemMargin)
                endOfParent(itemMargin)
                above(vibrationTitle)
            })

            add(vibrationTitle, lParams(matchConstraints, lineHeight) {
                below(soundSeekBar)
                startOfParent(itemMargin)
                topToTopOf(soundSeekBar, itemMargin)
                above(vibrateSeekBar)
            })
            add(vibrationSwitch, lParams(matchConstraints, lineHeight) {
                topToTopOf(vibrationTitle)
                endOfParent(itemMargin)
            })
            add(vibrateSeekBar, lParams(width = matchParent, height = wrapContent) {
                below(vibrationTitle)
                startOfParent(itemMargin)
                endOfParent(itemMargin)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.keyboard_feedback)
    }

    override fun onStop() {
        super.onStop()
        KeyboardManager.instance.clearKeyboard()
    }
}