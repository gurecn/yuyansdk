package com.yuyan.imemodule.view.keyboard.container

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.yanzhenjie.recyclerview.SwipeMenu
import com.yanzhenjie.recyclerview.SwipeMenuBridge
import com.yanzhenjie.recyclerview.SwipeMenuItem
import com.yanzhenjie.recyclerview.SwipeRecyclerView
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
import com.yuyan.imemodule.utils.LogUtil
import com.yuyan.imemodule.utils.pinyin4j.PinyinHelper
import com.yuyan.imemodule.view.keyboard.InputView
import com.yuyan.inputmethod.core.Kernel
import com.yuyan.inputmethod.util.T9PinYinUtils
import splitties.dimensions.dp
import splitties.views.textResource
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import kotlin.math.ceil

/**
 * 粘贴板列表键盘容器
 *
 * 使用RecyclerView实现垂直ListView列表布局。
 */
@SuppressLint("ViewConstructor")
class ClipBoardContainer(context: Context, inputView: InputView) : BaseContainer(context, inputView) {
    private val mPaint : Paint = Paint() // 测量字符串长度
    private val mRVSymbolsView: SwipeRecyclerView = SwipeRecyclerView(context)
    private var mTVLable: TextView? = null
    private var itemMode:SkbMenuMode? = null

    init {
        mPaint.textSize = dp(22f)
        initView(context)
    }

    private fun initView(context: Context) {
        mTVLable = TextView(context).apply {
            textResource = R.string.clipboard_empty_ltip
            gravity = Gravity.CENTER
            setTextColor(ThemeManager.activeTheme.keyTextColor)
            textSize = DevicesUtils.px2dip(EnvironmentSingleton.instance.candidateTextSize.toFloat()).toFloat()
        }
        mRVSymbolsView.setItemAnimator(null)
        val layoutParams2 = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        mRVSymbolsView.layoutParams = layoutParams2
        this.addView(mRVSymbolsView)
    }

    /**
     * 显示候选词界面 , 点击候选词时执行
     */
    fun showClipBoardView(item: SkbMenuMode) {
        itemMode = item
        mRVSymbolsView.setHasFixedSize(true)
        val copyContents : MutableList<ClipBoardDataBean> =
            if(itemMode == SkbMenuMode.ClipBoard) LauncherModel.instance.mClipboardDao?.getAllClipboardContent() ?: return
            else {
                val phrases = File(CustomConstant.RIME_DICT_PATH + "/custom_phrase_t9.txt")
                    .readLines().filter { !it.startsWith("#") }.map { line ->
                        ClipBoardDataBean("",line.split("\t".toRegex())[0])
                    }.toMutableList()
                phrases.reverse()
                phrases
            }
        val manager =  when (AppPrefs.getInstance().clipboard.clipboardLayoutCompact.getValue()){
            ClipboardLayoutMode.ListView ->  LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            ClipboardLayoutMode.GridView -> GridLayoutManager(context, 2)
            ClipboardLayoutMode.FlexboxView -> {
                calculateColumn(copyContents)
                GridLayoutManager(context, 6).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(i: Int) = mHashMapSymbols[i] ?: 1
                    }
                }
            }
        }
        mRVSymbolsView.setLayoutManager(manager)
        val viewParent = mTVLable?.parent
        if (viewParent != null) {
            (viewParent as ViewGroup).removeView(mTVLable)
        }
        if(copyContents.size == 0){
            this.addView(mTVLable, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        }
        val adapter = ClipBoardAdapter(context, copyContents)
        mRVSymbolsView.setAdapter(null)
        mRVSymbolsView.setOnItemClickListener{ _: View?, position: Int ->
                inputView.responseLongKeyEvent(SoftKey(), copyContents[position].copyContent)
        }
        mRVSymbolsView.setSwipeMenuCreator{ _: SwipeMenu, rightMenu: SwipeMenu, position: Int ->
            val topItem = SwipeMenuItem(mContext).apply {
                setImage(if(itemMode == SkbMenuMode.ClipBoard) {
                    val data: ClipBoardDataBean = copyContents[position]
                    if(data.isKeep)R.drawable.ic_baseline_untop_circle_32
                    else R.drawable.ic_baseline_top_circle_32
                }
                else R.drawable.ic_baseline_edit_circle_32)
            }
            rightMenu.addMenuItem(topItem)
            val deleteItem = SwipeMenuItem(mContext).apply {
                setImage(R.drawable.ic_baseline_delete_circle_32)
            }
            rightMenu.addMenuItem(deleteItem)
        }
        mRVSymbolsView.setOnItemMenuClickListener { menuBridge: SwipeMenuBridge, position: Int ->
            menuBridge.closeMenu()
            if(itemMode == SkbMenuMode.ClipBoard){
                if(menuBridge.position == 0) {
                    val data: ClipBoardDataBean = copyContents[position]
                    data.isKeep = !data.isKeep
                    LauncherModel.instance.mClipboardDao?.updateClopboard(data)
                    showClipBoardView(SkbMenuMode.ClipBoard)
                } else if(menuBridge.position == 1){
                    val data: ClipBoardDataBean = copyContents.removeAt(position)
                    LauncherModel.instance.mClipboardDao?.deleteClipboard(data)
                    mRVSymbolsView.adapter?.notifyItemRemoved(position)
                }
            } else {
                val content = copyContents[position].copyContent!!
                removePhrasesHandle(content)
                if(menuBridge.position == 0) {
                    inputView.onSettingsMenuClick(SkbMenuMode.AddPhrases, content)
                } else if(menuBridge.position == 1){
                    showClipBoardView(SkbMenuMode.Phrases)
                }
                Kernel.initWiIme(AppPrefs.getInstance().internal.pinyinModeRime.getValue())
            }
        }
        mRVSymbolsView.setAdapter(adapter)
    }

    private fun removePhrasesHandle(content:String) {
        if(content.isNotBlank()) {
            deleteLinesStartingWith("/custom_phrase.txt", content+"\t")
            deleteLinesStartingWith("/custom_phrase_t9.txt", content+"\t")
            deleteLinesStartingWith("/custom_phrase_double.txt", content+"\t")
        }
    }

    private fun deleteLinesStartingWith(fileName: String, content: String) {
        val file = File(CustomConstant.RIME_DICT_PATH + fileName)
        val lines = file.readLines().filter { !it.startsWith(content) }
        file.writeText(lines.joinToString(separator = "\n"))
    }

    private val mHashMapSymbols = HashMap<Int, Int>() //候选词索引列数对应表
    private fun calculateColumn(data : MutableList<ClipBoardDataBean>) {
        mHashMapSymbols.clear()
        val itemWidth = EnvironmentSingleton.instance.skbWidth/6 - dp(40)
        var mCurrentColumn = 0
        val contents = data.map { it.copyContent }
        contents.forEachIndexed { position, candidate ->
            var count = getSymbolsCount(candidate!!, itemWidth)
            var nextCount = 0
            if (contents.size > position + 1) {
                val nextCandidate = contents[position + 1]!!
                nextCount = getSymbolsCount(nextCandidate, itemWidth)
            }
            mCurrentColumn = if (mCurrentColumn + count + nextCount > 6) {
                count = 6 - mCurrentColumn
                0
            } else  (mCurrentColumn + count) % 6
            mHashMapSymbols[position] = count
        }
    }

    private fun getSymbolsCount(data: String, itemWidth:Int): Int {
        return if (!TextUtils.isEmpty(data)) ceil(mPaint.measureText(data).div(itemWidth)).toInt() else 0
    }
    fun getMenuMode():SkbMenuMode? {
       return itemMode
    }
}
