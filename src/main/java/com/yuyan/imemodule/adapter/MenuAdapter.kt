package com.yuyan.imemodule.adapter

import android.content.Context
import android.graphics.drawable.VectorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.LauncherModel
import com.yuyan.imemodule.callback.OnRecyclerItemClickListener
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.data.flower.FlowerTypefaceMode
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.entity.SkbFunItem
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.KeyboardOneHandedMod
import com.yuyan.imemodule.prefs.behavior.SkbMenuMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton

class MenuAdapter (context: Context?, private val data: MutableList<SkbFunItem>) : RecyclerView.Adapter<MenuAdapter.SymbolHolder>() {
    private val inflater: LayoutInflater
    private var mOnItemClickListener: OnRecyclerItemClickListener? = null
    private val textColor: Int
    private var mTheme: Theme? = null
    fun setOnItemClickLitener(mOnItemClickLitener: OnRecyclerItemClickListener?) {
        mOnItemClickListener = mOnItemClickLitener
    }
    init {
        val theme = ThemeManager.activeTheme
        textColor = theme.keyTextColor
        mTheme = ThemeManager.activeTheme
        inflater = LayoutInflater.from(context)
    }


    inner class SymbolHolder(view: View) : RecyclerView.ViewHolder(view) {
        var entranceNameTextView: TextView? = null
        var entranceIconImageView: ImageView? = null
        init {
            entranceIconImageView = itemView.findViewById(R.id.entrance_image)
            entranceNameTextView = itemView.findViewById(R.id.entrance_name)
            val skbWidth = EnvironmentSingleton.instance.skbWidth
            val layoutParams = itemView.layoutParams
            layoutParams.width = skbWidth.div(4)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolHolder {
        val view = inflater.inflate(R.layout.sdk_item_skb_menu_entrance, parent, false)
        return SymbolHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: SymbolHolder, position: Int) {
        val item = data[position]
        holder.entranceNameTextView?.text = item.funName
        holder.entranceIconImageView?.setImageResource(item.funImgRecource)
        val color = if (isSettingsMenuSelect(item)) mTheme!!.genericActiveBackgroundColor else mTheme!!.keyTextColor
        holder.entranceNameTextView?.setTextColor(color)
        val vectorDrawableCompat = holder.entranceIconImageView?.getDrawable() as VectorDrawable
        vectorDrawableCompat.setTint(color)
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener { v: View? ->
                mOnItemClickListener!!.onItemClick(this, v, position)
            }
        }
    }

    private fun isSettingsMenuSelect(data: SkbFunItem): Boolean {
        val rimeValue = AppPrefs.getInstance().internal.pinyinModeRime.getValue()
        val result: Boolean = when (data.skbMenuMode) {
            // Setting Menu
            SkbMenuMode.DarkTheme -> ThemeManager.activeTheme.isDark
            SkbMenuMode.NumberRow -> ThemeManager.prefs.abcNumberLine.getValue()
            SkbMenuMode.JianFan -> AppPrefs.getInstance().input.chineseFanTi.getValue()
            SkbMenuMode.LockEnglish -> ThemeManager.prefs.keyboardLockEnglish.getValue()
            SkbMenuMode.SymbolShow -> ThemeManager.prefs.keyboardSymbol.getValue()
            SkbMenuMode.Mnemonic -> ThemeManager.prefs.keyboardMnemonic.getValue()
            SkbMenuMode.EmojiInput -> AppPrefs.getInstance().input.emojiInput.getValue()
            SkbMenuMode.OneHanded -> ThemeManager.prefs.oneHandedMod.getValue() != KeyboardOneHandedMod.None
            SkbMenuMode.FlowerTypeface -> LauncherModel.instance.flowerTypeface != FlowerTypefaceMode.Disabled
            SkbMenuMode.FloatKeyboard -> ThemeManager.prefs.keyboardModeFloat.getValue()

            // Keyboard Menu
            SkbMenuMode.PinyinT9 -> rimeValue == CustomConstant.SCHEMA_ZH_T9
            SkbMenuMode.Pinyin26Jian -> rimeValue == CustomConstant.SCHEMA_ZH_QWERTY
            SkbMenuMode.PinyinHandWriting -> rimeValue == CustomConstant.SCHEMA_ZH_HANDWRITING
            SkbMenuMode.Pinyin26Double -> rimeValue == CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY
            SkbMenuMode.PinyinLx17 -> rimeValue == CustomConstant.SCHEMA_ZH_DOUBLE_LX17
            else -> false
        }
        return result
    }
}