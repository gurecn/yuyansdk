package com.yuyan.imemodule.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.adapter.PrefixSettingsAdapter
import com.yuyan.imemodule.application.Launcher
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.database.DataBaseKT
import com.yuyan.imemodule.database.entry.SideSymbol
import com.yuyan.imemodule.utils.DevicesUtils
import com.yuyan.imemodule.keyboard.KeyboardManager
import com.yuyan.imemodule.libs.recyclerview.SwipeMenu
import com.yuyan.imemodule.libs.recyclerview.SwipeMenuBridge
import com.yuyan.imemodule.libs.recyclerview.SwipeMenuItem
import com.yuyan.imemodule.libs.recyclerview.SwipeRecyclerView
import com.yuyan.imemodule.libs.recyclerview.touch.OnItemMoveListener
import splitties.dimensions.dp
import splitties.views.dsl.core.add
import splitties.views.dsl.core.lParams
import splitties.views.dsl.core.matchParent
import splitties.views.dsl.core.textView
import splitties.views.dsl.core.wrapContent
import java.util.Collections

class PrefixSettingsFragment(type:String) : Fragment(){
    private var  mType = "pinyin"
    private var  datas:MutableList<SideSymbol>
    init {
        mType = type
        datas = if(type == "pinyin")DataBaseKT.instance.sideSymbolDao().getAllSideSymbolPinyin().toMutableList() else DataBaseKT.instance.sideSymbolDao().getAllSideSymbolNumber().toMutableList()
    }
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.setting_ime_prefixs)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = with(requireContext()) {
        val header = LinearLayout(Launcher.instance.context).apply {
            gravity = Gravity.CENTER_VERTICAL
            add(textView {
                gravity = Gravity.CENTER
                background = null
                text = context.getString(R.string.skb_prefix_show_tips)
                setTextColor(activeTheme.keyTextColor)
            }, lParams(width = 0, weight = 1f){
                setMargins(dp(20), 0, dp(20), 0)
            })

            add(textView {
                gravity = Gravity.CENTER
                background = null
                text = context.getString(R.string.skb_prefix_commit_tips)
                setTextColor(activeTheme.keyTextColor)
            }, lParams(width = 0, weight = 2f))

            add(textView {
                gravity = Gravity.CENTER
                background = null
                text = context.getString(R.string.skb_prefix_sort_tips)
                setTextColor(activeTheme.keyTextColor)
            }, lParams(width = 0, weight = 1f))
        }

        val adapter = PrefixSettingsAdapter(datas, mType)
        val mItemMoveListener: OnItemMoveListener = object :
            OnItemMoveListener {
            override fun onItemMove(srcHolder: RecyclerView.ViewHolder, targetHolder: RecyclerView.ViewHolder): Boolean {
                val fromPosition = srcHolder.bindingAdapterPosition
                val toPosition = targetHolder.bindingAdapterPosition
                if(fromPosition < 0 || fromPosition >= datas.size) return false
                if(toPosition < 0 || toPosition >= datas.size) return false
                Collections.swap(datas, fromPosition, toPosition)
                adapter.notifyItemMoved(fromPosition, toPosition)
                return true
            }
            override fun onItemDismiss(srcHolder: RecyclerView.ViewHolder?) {
            }
        }

        val mRVSymbolsView = SwipeRecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
            setLongPressDragEnabled(true)
            setOnItemLongClickListener{ _, _ ->
              DevicesUtils.tryVibrate(this)
            }
            setOnItemMoveListener(mItemMoveListener)
        }
        mRVSymbolsView.setSwipeMenuCreator{ _: SwipeMenu, rightMenu: SwipeMenu, _: Int ->
            val deleteItem = SwipeMenuItem(context).apply {
                setImage(R.drawable.ic_menu_delete)
            }
            rightMenu.addMenuItem(deleteItem)
        }
        mRVSymbolsView.setOnItemMenuClickListener { menuBridge: SwipeMenuBridge, position: Int ->
            menuBridge.closeMenu()
            if(menuBridge.position == 0 && position < datas.size) {
                datas.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
        }
        mRVSymbolsView.setAdapter(adapter)
        LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            fitsSystemWindows = true
            add(header, lParams(width = matchParent, height = wrapContent) {
                setMargins(0, dp(20), 0, dp(0))
            })
            add(mRVSymbolsView, lParams(width = matchParent, height = matchParent))
        }
    }

    override fun onPause() {
        super.onPause()
        DataBaseKT.instance.sideSymbolDao().deleteAll(mType)
        DataBaseKT.instance.sideSymbolDao().insertAll(datas.filter { it.symbolKey.isNotBlank()})
        KeyboardManager.instance.clearKeyboard()
    }
}