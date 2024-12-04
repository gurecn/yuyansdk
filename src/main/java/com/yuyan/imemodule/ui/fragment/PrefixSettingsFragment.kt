package com.yuyan.imemodule.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yanzhenjie.recyclerview.OnItemLongClickListener
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener
import com.yuyan.imemodule.R
import com.yuyan.imemodule.adapter.PrefixSettingsAdapter
import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.db.DataBaseKT
import com.yuyan.imemodule.db.entry.SideSymbol
import com.yuyan.imemodule.prefs.restore
import com.yuyan.imemodule.utils.LogUtil
import com.yuyan.imemodule.view.keyboard.KeyboardManager
import com.yuyan.imemodule.view.widget.CustomLinearLayout
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
        val header = LinearLayout(ImeSdkApplication.context).apply {
            add(textView {
                gravity = Gravity.CENTER
                background = null
                text = context.getString(R.string.skb_prefix_show_tips)
            }, lParams(width = 0, weight = 1f){
                setMargins(dp(20), 0, dp(20), 0)
            })

            add(textView {
                gravity = Gravity.CENTER
                background = null
                text = context.getString(R.string.skb_prefix_commit_tips)
            }, lParams(width = 0, weight = 2f))

            add(textView {
                gravity = Gravity.CENTER
                background = null
                text = context.getString(R.string.skb_prefix_sort_tips)
            }, lParams(width = 0, weight = 1f))
        }

        val adapter = PrefixSettingsAdapter(datas, mType)
        val mItemMoveListener: OnItemMoveListener = object : OnItemMoveListener {
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
            setOnItemLongClickListener{ _, adapterPosition ->
                AlertDialog.Builder(context)
                    .setTitle(R.string.skb_prefix_delete_title)
                    .setMessage(String.format(getString(R.string.skb_prefix_delete_tips), datas[0].symbolKey))
                    .setNegativeButton(android.R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.sure) { _, _ ->
                        datas.removeAt(adapterPosition)
                        adapter.notifyItemRemoved(adapterPosition)
                    }
                    .show()
            }
            setOnItemMoveListener(mItemMoveListener)
        }
        mRVSymbolsView.setAdapter(adapter)
        CustomLinearLayout(context).apply {
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