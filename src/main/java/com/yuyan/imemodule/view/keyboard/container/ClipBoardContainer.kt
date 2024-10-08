package com.yuyan.imemodule.view.keyboard.container

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.JustifyContent
import com.yuyan.imemodule.R
import com.yuyan.imemodule.adapter.ClipBoardAdapter
import com.yuyan.imemodule.application.LauncherModel
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.entity.ClipBoardDataBean
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.ClipboardLayoutMode
import com.yuyan.imemodule.prefs.behavior.SkbMenuMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.utils.DevicesUtils
import com.yuyan.imemodule.view.keyboard.InputView
import com.yuyan.imemodule.view.keyboard.manager.CustomFlexboxLayoutManager
import splitties.views.textResource
import java.io.File

/**
 * 粘贴板列表键盘容器
 *
 * 使用RecyclerView实现垂直ListView列表布局。
 */
@SuppressLint("ViewConstructor")
class ClipBoardContainer(context: Context, inputView: InputView) : BaseContainer(context, inputView) {
    private var mRVSymbolsView: RecyclerView? = null
    private var mTVLable: TextView? = null
    private var itemMode:SkbMenuMode? = null

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
        mRVSymbolsView!!.setItemAnimator(null)
        val layoutParams2 = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        mRVSymbolsView!!.layoutParams = layoutParams2
        this.addView(mRVSymbolsView)
    }

    /**
     * 显示候选词界面 , 点击候选词时执行
     */
    fun showClipBoardView(item: SkbMenuMode) {
        itemMode = item
        val manager =  when (AppPrefs.getInstance().clipboard.clipboardLayoutCompact.getValue()){
            ClipboardLayoutMode.ListView -> {
                mRVSymbolsView!!.setHasFixedSize(true)
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }
            ClipboardLayoutMode.GridView -> {
                mRVSymbolsView!!.setHasFixedSize(true)
                GridLayoutManager(context, 2)
            }
            ClipboardLayoutMode.FlexboxView -> {
                mRVSymbolsView!!.setHasFixedSize(false)
                CustomFlexboxLayoutManager(context).apply {
                    justifyContent = JustifyContent.FLEX_START // 设置主轴对齐方式为居左
                }
            }
        }
        mRVSymbolsView!!.setLayoutManager(manager)
        val copyContents : MutableList<ClipBoardDataBean> =
            if(itemMode == SkbMenuMode.ClipBoard) LauncherModel.instance.mClipboardDao?.getAllClipboardContent() ?: return
        else {
                File(CustomConstant.RIME_DICT_PATH + "/custom_phrase_t9.txt")
                    .readLines().filter { !it.startsWith("#") }.map { line ->
                        ClipBoardDataBean("",line.split("\t".toRegex())[0])
                    }.toMutableList()
        }
        val viewParent = mTVLable?.parent
        if (viewParent != null) {
            (viewParent as ViewGroup).removeView(mTVLable)
        }
        if(copyContents.size == 0){
            this.addView(mTVLable, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        }
        val adapter = ClipBoardAdapter(context, copyContents)
        adapter.setOnItemClickLitener { parent: RecyclerView.Adapter<*>?, _: View?, position: Int ->
            if (parent is ClipBoardAdapter) {
                inputView.responseLongKeyEvent(SoftKey(), copyContents[position].copyContent)
            }
        }
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                return makeMovementFlags(0, swipeFlags)
            }
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.getBindingAdapterPosition()
                val item: ClipBoardDataBean? = (mRVSymbolsView?.adapter as ClipBoardAdapter).removePosition(position)
                if(item != null) {
                    LauncherModel.instance.mClipboardDao?.deleteClipboard(item)
                    mRVSymbolsView?.adapter?.notifyItemRemoved(position)
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(mRVSymbolsView)
        mRVSymbolsView!!.setAdapter(adapter)
    }

    fun getMenuMode():SkbMenuMode? {
       return itemMode
    }
}
