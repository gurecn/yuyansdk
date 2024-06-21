package com.yuyan.imemodule.view.keyboard.container

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.adapter.CandidatesAdapter
import com.yuyan.imemodule.adapter.ClipBoardAdapter
import com.yuyan.imemodule.adapter.PrefixAdapter
import com.yuyan.imemodule.application.LauncherModel
import com.yuyan.imemodule.entity.ClipBoardDataBean
import com.yuyan.imemodule.utils.DevicesUtils.tryPlayKeyDown
import com.yuyan.imemodule.utils.DevicesUtils.tryVibrate
import com.yuyan.imemodule.utils.LogUtil
import com.yuyan.imemodule.view.keyboard.InputView

class ClipBoardContainer(context: Context, inputView: InputView?) : BaseContainer(context, inputView) {
    private var mRVSymbolsView: RecyclerView? = null

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        mRVSymbolsView = RecyclerView(context)
        mRVSymbolsView!!.setHasFixedSize(true)
        mRVSymbolsView!!.setItemAnimator(null)
        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mRVSymbolsView!!.setLayoutManager(manager)
        val layoutParams2 = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        mRVSymbolsView!!.setLayoutParams(layoutParams2)
        this.addView(mRVSymbolsView)
        showClipBoardView()
    }

    /**
     * 显示候选词界面 , 点击候选词时执行
     */
    fun showClipBoardView() {
        val copyContents :List<ClipBoardDataBean>? =   LauncherModel.instance?.mClipboardDao?.getAllClipboardContent("")
        LogUtil.d(TAG, "copyContents:" + copyContents?.size)
        val adapter = ClipBoardAdapter(context, copyContents!!)
        adapter.setOnItemClickLitener { parent: RecyclerView.Adapter<*>?, _: View?, position: Int ->
            if (parent is PrefixAdapter) {
                parent.getSymbolData(position)
                tryPlayKeyDown()
                tryVibrate(this)
                inputView!!.selectPrefix(position)
            } else if (parent is CandidatesAdapter) {
                inputView!!.onChoiceTouched(parent.getItem(position))
            }
        }
        mRVSymbolsView!!.setAdapter(adapter)
    }

    companion object {
        private val TAG = CandidatesContainer::class.java.getSimpleName()
    }
}
