package com.yuyan.imemodule.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.callback.OnRecyclerItemClickListener
import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.imemodule.data.flower.FlowerTypefaceMode
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.database.DataBaseKT
import com.yuyan.imemodule.entity.SkbFunItem
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.SkbMenuMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance

class MenuAdapter (context: Context?, val data: MutableList<SkbFunItem>) : RecyclerView.Adapter<MenuAdapter.SymbolHolder>() {
    private val inflater: LayoutInflater
    private var mOnItemClickListener: OnRecyclerItemClickListener? = null
    private val textColor: Int
    private var mTheme: Theme
    private var background: GradientDrawable
    fun setOnItemClickLitener(mOnItemClickLitener: OnRecyclerItemClickListener?) {
        mOnItemClickListener = mOnItemClickLitener
    }

    var dragOverListener: DragOverListener? = null
    init {
        val theme = ThemeManager.activeTheme
        textColor = theme.keyTextColor
        mTheme = ThemeManager.activeTheme
        inflater = LayoutInflater.from(context)
        background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(ThemeManager.activeTheme.keyBackgroundColor)
        }
    }

    inner class SymbolHolder(view: View) : RecyclerView.ViewHolder(view) {
        var entranceNameTextView: TextView? = null
        var entranceIconImageView: ImageView? = null
        var entranceOption: ImageView? = null
        init {
            entranceIconImageView = itemView.findViewById(R.id.entrance_image)
            entranceNameTextView = itemView.findViewById(R.id.entrance_name)
            entranceOption = itemView.findViewById(R.id.entrance_option)
            entranceOption?.background = background
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolHolder {
        val view = inflater.inflate(R.layout.sdk_item_skb_menu_entrance, parent, false)
        return SymbolHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: SymbolHolder, position: Int) {
        val item = data[position]
        holder.entranceNameTextView?.text = item.funName
        holder.entranceIconImageView?.setImageResource(item.funImgRecource)
        val color = if (isSettingsMenuSelect(item)) mTheme.accentKeyBackgroundColor else mTheme.keyTextColor
        holder.entranceNameTextView?.setTextColor(color)
        holder.entranceIconImageView?.getDrawable()?.setTint(color)
        if (dragOverListener != null) {
            holder.entranceOption?.visibility = View.VISIBLE
            if(DataBaseKT.instance.skbFunDao().getBarMenu(item.skbMenuMode.name) != null){
                holder.entranceOption?.setImageResource(R.drawable.ic_menu_minus)
            } else {
                holder.entranceOption?.setImageResource(R.drawable.ic_menu_plus)
            }
            holder.entranceIconImageView?.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    dragOverListener?.startDragItem(holder)
                }
                return@setOnTouchListener false
            }
            holder.entranceOption?.getDrawable()?.setTint(color)
            holder.entranceOption?.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    dragOverListener?.onOptionClick(this, item, position)
                }
                return@setOnTouchListener false
            }
            holder.itemView.setOnClickListener(null)
        } else {
            holder.entranceOption?.visibility = View.GONE
            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener { v: View? ->
                    mOnItemClickListener!!.onItemClick(this, v, position)
                }
            }
        }
    }

    private fun isSettingsMenuSelect(data: SkbFunItem): Boolean {
        val rimeValue = AppPrefs.getInstance().internal.pinyinModeRime.getValue()
        val result: Boolean = when (data.skbMenuMode) {
            // Setting Menu
            SkbMenuMode.DarkTheme -> ThemeManager.activeTheme.isDark
            SkbMenuMode.NumberRow -> AppPrefs.getInstance().keyboardSetting.abcNumberLine.getValue()
            SkbMenuMode.JianFan -> AppPrefs.getInstance().input.chineseFanTi.getValue()
            SkbMenuMode.LockEnglish -> AppPrefs.getInstance().keyboardSetting.keyboardLockEnglish.getValue()
            SkbMenuMode.SymbolShow -> ThemeManager.prefs.keyboardSymbol.getValue()
            SkbMenuMode.Mnemonic -> AppPrefs.getInstance().keyboardSetting.keyboardMnemonic.getValue()
            SkbMenuMode.EmojiInput -> AppPrefs.getInstance().input.emojiInput.getValue()
            SkbMenuMode.OneHanded -> AppPrefs.getInstance().keyboardSetting.oneHandedModSwitch.getValue()
            SkbMenuMode.FlowerTypeface -> CustomConstant.flowerTypeface != FlowerTypefaceMode.Disabled
            SkbMenuMode.FloatKeyboard -> EnvironmentSingleton.instance.keyboardModeFloat
            // Keyboard Menu
            SkbMenuMode.PinyinT9 -> rimeValue == CustomConstant.SCHEMA_ZH_T9
            SkbMenuMode.Pinyin26Jian -> rimeValue == CustomConstant.SCHEMA_ZH_QWERTY
            SkbMenuMode.PinyinHandWriting -> rimeValue == CustomConstant.SCHEMA_ZH_HANDWRITING
            SkbMenuMode.PinyinLx17 -> rimeValue == CustomConstant.SCHEMA_ZH_DOUBLE_LX17
            SkbMenuMode.Pinyin26Double -> rimeValue.startsWith(CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY) && rimeValue != CustomConstant.SCHEMA_ZH_DOUBLE_LX17
            SkbMenuMode.PinyinStroke -> rimeValue == CustomConstant.SCHEMA_ZH_STROKE
            SkbMenuMode.TextEdit -> InputModeSwitcherManager.isTextEditSkb
            else -> false
        }
        return result
    }

    interface DragOverListener {
        fun startDragItem(holder: RecyclerView.ViewHolder)
        fun onOptionClick(parent: RecyclerView.Adapter<*>?, v: SkbFunItem, position: Int)
    }
}