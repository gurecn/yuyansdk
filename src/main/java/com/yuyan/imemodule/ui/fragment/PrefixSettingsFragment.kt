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
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.adapter.PrefixSettingsAdapter
import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.db.DataBaseKT
import splitties.dimensions.dp
import splitties.views.dsl.core.add
import splitties.views.dsl.core.lParams
import splitties.views.dsl.core.matchParent
import splitties.views.dsl.core.textView
import splitties.views.dsl.core.verticalLayout
import splitties.views.dsl.core.wrapContent

class PrefixSettingsFragment(pos:Int) : Fragment(){
    private var  positon = 0
    init {
        positon = pos
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
            }, lParams(width = 0, weight = 2f))

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

        val mRVSymbolsView = SwipeRecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
        }
        val adapter = PrefixSettingsAdapter(if(positon == 0)DataBaseKT.instance.sideSymbolDao().getAllSideSymbolPinyin() else DataBaseKT.instance.sideSymbolDao().getAllSideSymbolNumber())
        mRVSymbolsView.setAdapter(adapter)
        verticalLayout {
//            fitsSystemWindows = true
            add(header, lParams(width = matchParent, height = wrapContent) {
                setMargins(0, dp(20), 0, dp(0))
            })
            add(mRVSymbolsView, lParams(width = matchParent, height = matchParent))
        }
    }
}