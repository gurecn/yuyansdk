package com.yuyan.imemodule.view.keyboard.container

import android.annotation.SuppressLint
import com.google.android.flexbox.JustifyContent
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.yuyan.imemodule.R
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.entity.SkbFunItem
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.behavior.SkbMenuMode
import com.yuyan.imemodule.view.keyboard.KeyboardManager
import com.yuyan.imemodule.adapter.MenuAdapter
import com.yuyan.imemodule.data.allSkbFuns
import com.yuyan.imemodule.data.commonSkbFuns
import com.yuyan.imemodule.data.menuSkbFunsPreset
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.utils.LogUtil
import com.yuyan.imemodule.view.keyboard.InputView
import java.util.LinkedList

/**
 * 设置键盘容器
 *
 * 设置键盘、切换键盘界面容器。使用RecyclerView + FlexboxLayoutManager实现Grid布局。
 */
@SuppressLint("ViewConstructor")
class SettingsContainer(context: Context, inputView: InputView) : BaseContainer(context, inputView) {
    private var mRVMenuLayout: RecyclerView? = null
    private var mTheme: Theme? = null
    private var adapter:MenuAdapter? = null

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        mTheme = activeTheme
        mRVMenuLayout = RecyclerView(context)
        mRVMenuLayout!!.setHasFixedSize(true)
        mRVMenuLayout!!.setItemAnimator(null)
        val manager = FlexboxLayoutManager(context)
        manager.justifyContent = JustifyContent.FLEX_START
        mRVMenuLayout!!.setLayoutManager(manager)
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        mRVMenuLayout!!.layoutParams = layoutParams
        this.addView(mRVMenuLayout)
    }

    /**
     * 弹出键盘设置界面
     */
    fun showSettingsView() {
        //获取键盘功能栏功能对象
        val funItems: MutableList<SkbFunItem> = LinkedList()
        for(item in allSkbFuns){
            val skbMenuMode = menuSkbFunsPreset[SkbMenuMode.decode(item)]
            if(skbMenuMode != null){
                funItems.add(skbMenuMode)
            }
        }
        adapter = MenuAdapter(context, funItems)
        adapter?.setOnItemClickLitener { _: RecyclerView.Adapter<*>?, _: View?, position: Int ->
            inputView.onSettingsMenuClick(funItems[position].skbMenuMode)
        }
        mRVMenuLayout!!.setAdapter(adapter)
        enableDragItem(false)
    }

    fun enableDragItem(enable: Boolean) {
        if (enable) {
            val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
                override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                    return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END, 0)
                }
                override fun onMove(recyclerView: RecyclerView, oldHolder: RecyclerView.ViewHolder, targetHolder: RecyclerView.ViewHolder): Boolean {
                    adapter?.notifyItemMoved(oldHolder.adapterPosition, targetHolder.adapterPosition)
                    // 在每次移动后, 将界面上图标的顺序同步到appsAdapter.data中
//                    adapter?.data?.forEachIndexed { index, _ ->
//                    }
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }

                override fun canDropOver(recyclerView: RecyclerView, current: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = true

                override fun isLongPressDragEnabled() = false
            })

            adapter?.dragOverListener = object : MenuAdapter.DragOverListener {
                override fun startDragItem(holder: RecyclerView.ViewHolder) {
                    itemTouchHelper.startDrag(holder)
                }
                override fun onOptionClick(parent: RecyclerView.Adapter<*>?, v: SkbFunItem, position: Int) {
                    if(commonSkbFuns.contains(v.skbMenuMode.name)){
                        commonSkbFuns.remove(v.skbMenuMode.name)
                    } else {
                        commonSkbFuns.add(v.skbMenuMode.name)
                    }
                    inputView.freshCandidatesMenuBar()
                    adapter?.notifyItemChanged(position)
                }
            }
            itemTouchHelper.attachToRecyclerView(mRVMenuLayout)
        } else {
            adapter?.dragOverListener = null
        }
        adapter?.notifyDataSetChanged()
    }

    /**
     * 弹出键盘界面
     */
    fun showSkbSelelctModeView() {
        val funItems: MutableList<SkbFunItem> = LinkedList()
        funItems.add(SkbFunItem(mContext.getString(R.string.keyboard_name_t9), R.drawable.selece_input_mode_py9, SkbMenuMode.PinyinT9))
        funItems.add(SkbFunItem(mContext.getString(R.string.keyboard_name_cn26), R.drawable.selece_input_mode_py26, SkbMenuMode.Pinyin26Jian))
        funItems.add(SkbFunItem(mContext.getString(R.string.keyboard_name_hand), R.drawable.selece_input_mode_handwriting, SkbMenuMode.PinyinHandWriting))
        funItems.add(SkbFunItem(mContext.getString(R.string.keyboard_name_pinyin_flypy_plus), R.drawable.selece_input_mode_dpy26, SkbMenuMode.Pinyin26Double))
        funItems.add(SkbFunItem(mContext.getString(R.string.keyboard_name_pinyin_lx_17), R.drawable.selece_input_mode_lx17, SkbMenuMode.PinyinLx17))
        val adapter = MenuAdapter(context, funItems)
        adapter.setOnItemClickLitener { _: RecyclerView.Adapter<*>?, _: View?, position: Int ->
            onKeyboardMenuClick(funItems[position])
        }
        mRVMenuLayout!!.setAdapter(adapter)
    }

    private fun onKeyboardMenuClick(data: SkbFunItem) {
        var keyboardValue = 0x2000
        var value = CustomConstant.SCHEMA_ZH_T9
        when (data.skbMenuMode) {
            SkbMenuMode.Pinyin26Jian -> {
                keyboardValue = 0x1000
                value = CustomConstant.SCHEMA_ZH_QWERTY
            }

            SkbMenuMode.PinyinHandWriting -> {
                keyboardValue = 0x3000
                value = CustomConstant.SCHEMA_ZH_HANDWRITING
            }

            SkbMenuMode.Pinyin26Double -> {
                keyboardValue = 0x1000
                value = CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY
            }

            SkbMenuMode.PinyinLx17 -> {
                keyboardValue = 0x6000
                value = CustomConstant.SCHEMA_ZH_DOUBLE_LX17
            }
            else ->{
            }
        }
        val inputMode = keyboardValue or InputModeSwitcherManager.MASK_LANGUAGE_CN or InputModeSwitcherManager.MASK_CASE_UPPER
        AppPrefs.getInstance().internal.inputMethodPinyinMode.setValue(inputMode)
        AppPrefs.getInstance().internal.pinyinModeRime.setValue(value)
        mInputModeSwitcher!!.saveInputMode(inputMode)
        KeyboardManager.instance.switchKeyboard(mInputModeSwitcher!!.skbLayout)
    }
}
