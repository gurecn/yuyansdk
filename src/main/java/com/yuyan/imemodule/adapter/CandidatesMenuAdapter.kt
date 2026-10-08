package com.yuyan.imemodule.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.callback.OnRecyclerItemClickListener
import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.imemodule.data.flower.FlowerTypefaceMode
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.entity.SkbFunItem
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.SkbMenuMode
import com.yuyan.imemodule.prefs.behavior.SymbolMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.keyboard.KeyboardManager
import com.yuyan.imemodule.keyboard.container.ClipBoardContainer
import com.yuyan.imemodule.keyboard.container.SymbolContainer
import com.yuyan.imemodule.manager.InputModeSwitcher
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance
import splitties.views.padding

/**
 * 候选词界面适配器
 */
class CandidatesMenuAdapter(context: Context?) : RecyclerView.Adapter<CandidatesMenuAdapter.SymbolHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var mOnItemClickListener: OnRecyclerItemClickListener? = null
    private var itemHeight: Int = (instance.heightForCandidatesArea * 0.8f).toInt()
    private var mMenuPadding: Int = (instance.heightForCandidatesArea * 0.05f).toInt()
    private val menuItems = mutableListOf<SkbFunItem>()
    private val removeBadgeBackground = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(activeTheme.keyBackgroundColor)
    }

    var onRemoveListener: ((SkbFunItem, Int) -> Unit)? = null

    var editMode: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            notifyItemRangeChanged(0, itemCount)
        }

    var items: List<SkbFunItem>
        get() = menuItems
        set(value) {
            val diffResult = DiffUtil.calculateDiff(MyDiffCallback(menuItems.toList(), value))
            menuItems.clear()
            menuItems.addAll(value)
            diffResult.dispatchUpdatesTo(this)
        }

    fun setOnItemClickLitener(mOnItemClickLitener: OnRecyclerItemClickListener?) {
        mOnItemClickListener = mOnItemClickLitener
    }

    fun getMenuMode(position: Int): SkbMenuMode? = menuItems.getOrNull(position)?.skbMenuMode

    fun snapshot(): List<SkbFunItem> = menuItems.toList()

    fun moveItem(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition !in menuItems.indices || toPosition !in menuItems.indices) return false
        menuItems.add(toPosition, menuItems.removeAt(fromPosition))
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    fun removeItem(position: Int): SkbFunItem? {
        if (position !in menuItems.indices) return null
        val item = menuItems.removeAt(position)
        notifyItemRemoved(position)
        return item
    }

    inner class SymbolHolder(view: View) : RecyclerView.ViewHolder(view) {
        var entranceIconImageView: ImageView? = null
        var removeImageView: ImageView? = null
        init {
            entranceIconImageView = itemView.findViewById(R.id.candidates_menu_item)
            removeImageView = itemView.findViewById(R.id.candidates_menu_remove)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolHolder {
        return SymbolHolder(inflater.inflate(R.layout.sdk_item_recyclerview_candidates_menu, parent, false))
    }

    override fun getItemCount(): Int = menuItems.size

    override fun onBindViewHolder(holder: SymbolHolder, position: Int) {
        val item = menuItems[position]
        holder.entranceIconImageView?.padding = mMenuPadding
        val layoutParams = holder.entranceIconImageView?.layoutParams
        layoutParams?.width = itemHeight
        layoutParams?.height = itemHeight
        holder.entranceIconImageView?.setImageResource(item.funImgResource)
        val color = if (isSettingsMenuSelect(item)) activeTheme.accentKeyBackgroundColor else activeTheme.keyTextColor
        holder.entranceIconImageView?.getDrawable()?.setTint(color)
        if(ThemeManager.prefs.keyBorder.getValue()) {
            val bg = GradientDrawable()
            bg.setColor(activeTheme.keyBackgroundColor)
            bg.shape = GradientDrawable.OVAL
            holder.entranceIconImageView?.background = bg
        }
        holder.removeImageView?.visibility = if (editMode) View.VISIBLE else View.GONE
        if (editMode) {
            holder.removeImageView?.background = removeBadgeBackground
            holder.removeImageView?.getDrawable()?.setTint(activeTheme.accentKeyBackgroundColor)
        }
        val clickListener = View.OnClickListener { v: View ->
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) return@OnClickListener
            if (editMode) {
                onRemoveListener?.invoke(menuItems[currentPosition], currentPosition)
            } else {
                mOnItemClickListener?.onItemClick(this, v, currentPosition)
            }
        }
        if (editMode || mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(clickListener)
        } else {
            holder.itemView.setOnClickListener(null)
        }
    }

    private fun isSettingsMenuSelect(data: SkbFunItem): Boolean {
        val rimeValue = AppPrefs.getInstance().internal.pinyinModeRime.getValue()
        val result: Boolean = when (data.skbMenuMode) {
            // Setting Menu
            SkbMenuMode.DarkTheme -> activeTheme.isDark
            SkbMenuMode.NumberRow -> AppPrefs.getInstance().keyboardSetting.abcNumberLine.getValue()
            SkbMenuMode.JianFan -> AppPrefs.getInstance().input.chineseFanTi.getValue()
            SkbMenuMode.LockEnglish -> AppPrefs.getInstance().keyboardSetting.keyboardLockEnglish.getValue()
            SkbMenuMode.SymbolShow -> ThemeManager.prefs.keyboardSymbol.getValue()
            SkbMenuMode.Mnemonic ->  AppPrefs.getInstance().keyboardSetting.keyboardMnemonic.getValue()
            SkbMenuMode.EmojiInput -> AppPrefs.getInstance().input.emojiInput.getValue()
            SkbMenuMode.OneHanded -> AppPrefs.getInstance().keyboardSetting.oneHandedModSwitch.getValue()
            SkbMenuMode.FlowerTypeface -> CustomConstant.flowerTypeface != FlowerTypefaceMode.Disabled
            SkbMenuMode.FloatKeyboard -> EnvironmentSingleton.instance.keyboardModeFloat
            SkbMenuMode.ClipBoard -> (KeyboardManager.instance.currentContainer as? ClipBoardContainer)?.getMenuMode() == SkbMenuMode.ClipBoard
            SkbMenuMode.Phrases -> (KeyboardManager.instance.currentContainer as? ClipBoardContainer)?.getMenuMode() == SkbMenuMode.Phrases
            SkbMenuMode.Emojicon -> (KeyboardManager.instance.currentContainer as? SymbolContainer)?.getMenuMode() == SymbolMode.Emojicon
            SkbMenuMode.Emoticon -> (KeyboardManager.instance.currentContainer as? SymbolContainer)?.getMenuMode() == SymbolMode.Emoticon
            // Keyboard Menu
            SkbMenuMode.PinyinT9 -> rimeValue == CustomConstant.SCHEMA_ZH_T9
            SkbMenuMode.Pinyin26Jian -> rimeValue == CustomConstant.SCHEMA_ZH_QWERTY
            SkbMenuMode.PinyinHandWriting -> rimeValue == CustomConstant.SCHEMA_ZH_HANDWRITING
            SkbMenuMode.PinyinLx17 -> rimeValue == CustomConstant.SCHEMA_ZH_DOUBLE_LX17
            SkbMenuMode.Pinyin26Double -> rimeValue.startsWith(CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY) && rimeValue != CustomConstant.SCHEMA_ZH_DOUBLE_LX17
            SkbMenuMode.PinyinStroke -> rimeValue == CustomConstant.SCHEMA_ZH_STROKE
            SkbMenuMode.LockClipBoard -> CustomConstant.lockClipBoardEnable
            SkbMenuMode.TextEdit -> InputModeSwitcher.isTextEditSkb
            else -> false
        }
        return result
    }

    class MyDiffCallback(private val oldList: List<SkbFunItem>, private val newList: List<SkbFunItem>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].skbMenuMode == newList[newItemPosition].skbMenuMode
        }
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.funName == newItem.funName && oldItem.funImgResource == newItem.funImgResource
        }
    }

    fun notifyChanged() {
        itemHeight = (instance.heightForCandidatesArea * 0.8f).toInt()
        mMenuPadding = (instance.heightForCandidatesArea * 0.05f).toInt()
        removeBadgeBackground.setColor(activeTheme.keyBackgroundColor)
        notifyDataSetChanged()
    }
}