package com.yuyan.imemodule.adapter

import android.content.Context
import android.graphics.drawable.VectorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.LauncherModel
import com.yuyan.imemodule.callback.OnRecyclerItemClickListener
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.data.flower.FlowerTypefaceMode
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.entity.SkbFunItem
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.SkbMenuMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.view.keyboard.KeyboardManager
import com.yuyan.imemodule.view.keyboard.container.ClipBoardContainer
import com.yuyan.imemodule.view.keyboard.container.SymbolContainer

/**
 * 候选词界面适配器
 */
class CandidatesMenuAdapter(context: Context?) : RecyclerView.Adapter<CandidatesMenuAdapter.SymbolHolder>() {
    private val inflater: LayoutInflater
    private var mOnItemClickListener: OnRecyclerItemClickListener? = null
    private val itemHeight: Int
    var items: List<SkbFunItem> = emptyList()
        set(value) {
            val diffResult = DiffUtil.calculateDiff(MyDiffCallback(field, value))
            field = value
            diffResult.dispatchUpdatesTo(this)
        }
    fun setOnItemClickLitener(mOnItemClickLitener: OnRecyclerItemClickListener?) {
        mOnItemClickListener = mOnItemClickLitener
    }

    init {
        inflater = LayoutInflater.from(context)
        itemHeight = (EnvironmentSingleton.instance.heightForCandidates * 0.7f).toInt()
    }

    inner class SymbolHolder(view: View) : RecyclerView.ViewHolder(view) {
        var entranceIconImageView: ImageView? = null
        init {
            entranceIconImageView = itemView.findViewById(R.id.candidates_menu_item)
            val layoutParams = itemView.layoutParams
            layoutParams.width = itemHeight
            layoutParams.height = itemHeight
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidatesMenuAdapter.SymbolHolder {
        return SymbolHolder(inflater.inflate(R.layout.sdk_item_recyclerview_candidates_menu, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CandidatesMenuAdapter.SymbolHolder, position: Int) {
        val item = items[position]
        holder.entranceIconImageView?.setImageResource(item.funImgRecource)
        val color = if (isSettingsMenuSelect(item)) activeTheme.genericActiveBackgroundColor else activeTheme.keyTextColor
        val vectorDrawableCompat = holder.entranceIconImageView?.getDrawable() as VectorDrawable
        vectorDrawableCompat.setTint(color)
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener { v: View? ->
                mOnItemClickListener!!.onItemClick(this, v, position)
            }
        }
    }

    fun getMenuMode(position: Int): SkbMenuMode? = if(items.size > position)items[position].skbMenuMode else null

    private fun isSettingsMenuSelect(data: SkbFunItem): Boolean {
        val rimeValue = AppPrefs.getInstance().internal.pinyinModeRime.getValue()
        val result: Boolean = when (data.skbMenuMode) {
            // Setting Menu
            SkbMenuMode.DarkTheme -> activeTheme.isDark
            SkbMenuMode.NumberRow -> AppPrefs.getInstance().keyboardSetting.abcNumberLine.getValue()
            SkbMenuMode.JianFan -> AppPrefs.getInstance().input.chineseFanTi.getValue()
            SkbMenuMode.LockEnglish -> AppPrefs.getInstance().keyboardSetting.keyboardLockEnglish.getValue()
            SkbMenuMode.SymbolShow -> ThemeManager.prefs.keyboardSymbol.getValue()
            SkbMenuMode.Mnemonic -> ThemeManager.prefs.keyboardMnemonic.getValue()
            SkbMenuMode.EmojiInput -> AppPrefs.getInstance().input.emojiInput.getValue()
            SkbMenuMode.OneHanded -> AppPrefs.getInstance().keyboardSetting.oneHandedModSwitch.getValue()
            SkbMenuMode.FlowerTypeface -> LauncherModel.instance.flowerTypeface != FlowerTypefaceMode.Disabled
            SkbMenuMode.FloatKeyboard -> EnvironmentSingleton.instance.keyboardModeFloat
            SkbMenuMode.ClipBoard -> KeyboardManager.instance.currentContainer is ClipBoardContainer && (KeyboardManager.instance.currentContainer as ClipBoardContainer).getMenuMode() == SkbMenuMode.ClipBoard
            SkbMenuMode.Phrases -> KeyboardManager.instance.currentContainer is ClipBoardContainer && (KeyboardManager.instance.currentContainer as ClipBoardContainer).getMenuMode() == SkbMenuMode.Phrases
            SkbMenuMode.EmojiKeyboard -> KeyboardManager.instance.currentContainer is SymbolContainer  && (KeyboardManager.instance.currentContainer as SymbolContainer).getMenuMode() == CustomConstant.EMOJI_TYPR_FACE_DATA
            SkbMenuMode.Emoticons -> KeyboardManager.instance.currentContainer is SymbolContainer && (KeyboardManager.instance.currentContainer as SymbolContainer).getMenuMode() == CustomConstant.EMOJI_TYPR_SMILE_TEXT
            // Keyboard Menu
            SkbMenuMode.PinyinT9 -> rimeValue == CustomConstant.SCHEMA_ZH_T9
            SkbMenuMode.Pinyin26Jian -> rimeValue == CustomConstant.SCHEMA_ZH_QWERTY
            SkbMenuMode.PinyinHandWriting -> rimeValue == CustomConstant.SCHEMA_ZH_HANDWRITING
            SkbMenuMode.PinyinLx17 -> rimeValue == CustomConstant.SCHEMA_ZH_DOUBLE_LX17
            SkbMenuMode.Pinyin26Double -> rimeValue.startsWith(CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY) && rimeValue != CustomConstant.SCHEMA_ZH_DOUBLE_LX17
            SkbMenuMode.PinyinStroke -> rimeValue == CustomConstant.SCHEMA_ZH_STROKE
            else -> false
        }
        return result
    }

    class MyDiffCallback(private val oldList: List<SkbFunItem>, private val newList: List<SkbFunItem>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = true
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].funName == newList[newItemPosition].funName
        }
    }
}