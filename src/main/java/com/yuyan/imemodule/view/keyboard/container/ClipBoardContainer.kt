package com.yuyan.imemodule.view.keyboard.container

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.adapter.ClipBoardAdapter
import com.yuyan.imemodule.application.LauncherModel
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.entity.ClipBoardDataBean
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.utils.DevicesUtils
import com.yuyan.imemodule.view.keyboard.InputView
import com.yuyan.inputmethod.core.CandidateListItem
import splitties.views.textResource

/**
 * 粘贴板列表键盘容器
 *
 * 使用RecyclerView实现垂直ListView列表布局。
 */
@SuppressLint("ViewConstructor")
class ClipBoardContainer(context: Context, inputView: InputView) : BaseContainer(context, inputView) {
    private var mRVSymbolsView: RecyclerView? = null
    private var mTVLable: TextView? = null

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        mTVLable = TextView(context).apply {
            textResource = R.string.clipboard_empty_ltip
            gravity = Gravity.CENTER
            setTextColor(ThemeManager.activeTheme.keyTextColor)
            textSize = DevicesUtils.px2dip(EnvironmentSingleton.instance.candidateTextSize.toFloat()).toFloat()
        }
        mRVSymbolsView = RecyclerView(context)
        mRVSymbolsView!!.setHasFixedSize(true)
        mRVSymbolsView!!.setItemAnimator(null)
        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mRVSymbolsView!!.setLayoutManager(manager)
        val layoutParams2 = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        mRVSymbolsView!!.layoutParams = layoutParams2
        this.addView(mRVSymbolsView)
    }

    /**
     * 显示候选词界面 , 点击候选词时执行
     */
    fun showClipBoardView() {
        val copyContents : MutableList<ClipBoardDataBean> = LauncherModel.instance.mClipboardDao?.getAllClipboardContent("") ?: return
        val viewParent = mTVLable?.parent
        if (viewParent != null) {
            (viewParent as ViewGroup).removeView(mTVLable)
        }
        if(copyContents.size == 0){
            this.addView(mTVLable, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        }
        val words = ArrayList<CandidateListItem?>()
        for (clipBoardDataBean in copyContents) {
            val copyContent = clipBoardDataBean.copyContent
            if(!copyContent.isNullOrEmpty()) {
                words.add(CandidateListItem("", copyContent))
            }
        }
        val adapter = ClipBoardAdapter(context, copyContents)
        adapter.setOnItemClickLitener { parent: RecyclerView.Adapter<*>?, _: View?, position: Int ->
            if (parent is ClipBoardAdapter) {
                inputView.responseLongKeyEvent(SoftKey(), copyContents[position].copyContent)
            }
        }
        mRVSymbolsView!!.setAdapter(adapter)
    }
}
