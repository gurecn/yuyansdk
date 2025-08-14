package com.yuyan.imemodule.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.yuyan.imemodule.R
import com.yuyan.imemodule.callback.OnItemSelectedListener
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.FullDisplayCenterMode
import com.yuyan.imemodule.prefs.behavior.FullDisplayKeyMode
import com.yuyan.imemodule.keyboard.KeyboardManager
import splitties.dimensions.dp
import splitties.views.dsl.core.add
import splitties.views.dsl.core.lParams
import splitties.views.dsl.core.matchParent
import splitties.views.dsl.core.textView
import splitties.views.dsl.core.verticalLayout
import splitties.views.dsl.core.wrapContent

class FullDisplayKeyboardFragment: Fragment(){
    private lateinit var advancedContainer: Container
    private lateinit var normalContainer: Container
    private lateinit var items: LinearLayout
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = with(requireContext()) {
        val fullDisplayKeyboardEnable = AppPrefs.getInstance().internal.fullDisplayKeyboardEnable.getValue()
        advancedContainer = Container(context).apply {
            tip.setText(R.string.keyboard_full_display_advanced_tip)
            icon.setImageResource(R.drawable.keyboard_t9_full_display)
            mOnClickListener = View.OnClickListener {
                AppPrefs.getInstance().internal.fullDisplayKeyboardEnable.setValue(true)
                normalContainer.updateView(false)
                items.visibility = View.VISIBLE
            }
            if(fullDisplayKeyboardEnable)updateView(true)
        }
        normalContainer = Container(context).apply {
            tip.setText(R.string.keyboard_full_display_normal_tip)
            icon.setImageResource(R.drawable.keyboard_t9_normal)
            mOnClickListener = View.OnClickListener {
                AppPrefs.getInstance().internal.fullDisplayKeyboardEnable.setValue(false)
                advancedContainer.updateView(false)
                items.visibility = View.GONE
            }
            if(!fullDisplayKeyboardEnable)updateView(true)
        }
        val header = LinearLayout(context).apply {
            add(advancedContainer, lParams(width = 0, height = wrapContent, weight = 1f).apply { setMargins(dp(20)) })
            add(normalContainer, lParams(width = 0, height = wrapContent, weight = 1f).apply { setMargins(dp(20)) })
        }

        items = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val keyModes = arrayOf(FullDisplayKeyMode.SwitchIme, FullDisplayKeyMode.SwitchLanguage, FullDisplayKeyMode.Clipboard, FullDisplayKeyMode.Phrases, FullDisplayKeyMode.None)
            val fullDisplayKeyLeft = AppPrefs.getInstance().internal.fullDisplayKeyModeLeft.getValue()
            addView(Item(context, R.array.FullDisplayKeyMode, FullDisplayKeyMode.decode(fullDisplayKeyLeft).ordinal).apply {
                title.setText(R.string.keyboard_full_display_key_left)
                onItemSelected = OnItemSelectedListener{
                    AppPrefs.getInstance().internal.fullDisplayKeyModeLeft.setValue(keyModes[it].name)
                }
            }, lParams(width = matchParent, height = wrapContent))
            val fullDisplayKeyRight = AppPrefs.getInstance().internal.fullDisplayKeyModeRight.getValue()
            addView(Item(context, R.array.FullDisplayKeyMode, FullDisplayKeyMode.decode(fullDisplayKeyRight).ordinal).apply {
                title.setText(R.string.keyboard_full_display_key_right)
                onItemSelected = OnItemSelectedListener{
                    AppPrefs.getInstance().internal.fullDisplayKeyModeRight.setValue(keyModes[it].name)
                }
            }, lParams(width = matchParent, height = wrapContent))

            val centerModes = arrayOf(FullDisplayCenterMode.MoveCursor, FullDisplayCenterMode.None)
            val fullDisplayCenter = AppPrefs.getInstance().internal.fullDisplayCenterMode.getValue()
            addView(Item(context, R.array.FullDisplayCenterKeyMode, FullDisplayCenterMode.decode(fullDisplayCenter).ordinal).apply {
                title.setText(R.string.keyboard_full_display_key_center)
                onItemSelected = OnItemSelectedListener{
                    AppPrefs.getInstance().internal.fullDisplayCenterMode.setValue(centerModes[it].name)
                }
            }, lParams(width = matchParent, height = wrapContent))
        }
        if(!fullDisplayKeyboardEnable)items.visibility = View.GONE
        val elevation = textView {
            setBackgroundResource(R.color.skb_elevation_color)
        }
        verticalLayout {
            add(header, lParams(width = matchParent, height = wrapContent) {
                setMargins(dp(20))
            })
            add(elevation, lParams(width = matchParent,height = dp(1)) {
                setMargins(dp(40),dp(20),dp(40), 0)
            })
            add(items, lParams(width = matchParent, height = wrapContent))
        }
    }

    class Container(context: Context?):LinearLayout(context) {
        val tip: TextView
        val icon: ImageView
        var mOnClickListener: OnClickListener? = null
        init {
            setPadding(dp(20))
            orientation = VERTICAL
            gravity = Gravity.CENTER
            icon = ImageView(context)
            addView(icon, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(100)))
            tip = textView {
                setText(R.string.keyboard_full_display_advanced_tip)
                gravity = Gravity.CENTER
            }
            addView(tip)
            setOnClickListener { view: View? ->
                updateView(true)
                mOnClickListener?.onClick(view)
            }
        }

        fun updateView(isSelect:Boolean){
            if(isSelect){
                icon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_anim_reduce_90))
                setBackgroundResource(R.drawable.shape_select_rectangle)
            } else {
                background = null
            }
        }
    }

    @SuppressLint("ViewConstructor")
    class Item(context: Context?, keyModId:Int, select:Int):LinearLayout(context) {
        val title: TextView
        var onItemSelected: OnItemSelectedListener? = null
        init {
            setPadding(dp(40), dp(20), dp(40), dp(20))
            gravity = Gravity.CENTER
            title = textView {
                textSize = 16f
            }
            val keyMode = resources.getStringArray(keyModId)
            val adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, keyMode)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            val spinner = Spinner(context).apply {
                gravity = Gravity.END
            }
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    onItemSelected?.onItemSelected(position)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            spinner.setSelection(select)
            add(title, lParams(width = 0,height = wrapContent, weight = 1f))
            add(spinner, lParams(width = wrapContent,height = wrapContent, weight = 0f))
        }
    }

    override fun onStop() {
        super.onStop()
        KeyboardManager.instance.clearKeyboard()
    }
}