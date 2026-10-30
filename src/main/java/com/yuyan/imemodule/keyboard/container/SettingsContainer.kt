package com.yuyan.imemodule.keyboard.container

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.adapter.MenuAdapter
import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.imemodule.data.menuSkbFunsPreset
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.database.DataBaseKT
import com.yuyan.imemodule.entity.SkbFunItem
import com.yuyan.imemodule.manager.InputModeSwitcher
import com.yuyan.imemodule.manager.SkbMenuOrderManager
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.DoublePinyinSchemaMode
import com.yuyan.imemodule.prefs.behavior.SkbMenuMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.keyboard.InputView
import com.yuyan.imemodule.manager.layout.CustomGridLayoutManager
import com.yuyan.imemodule.utils.DevicesUtils
import splitties.dimensions.dp
import java.util.Collections
import java.util.LinkedList

/**
 * 设置键盘容器
 *
 * 设置键盘、切换键盘界面容器。使用RecyclerView + GridLayoutManager。
 */
@SuppressLint("ViewConstructor")
class SettingsContainer(context: Context, inputView: InputView) : BaseContainer(context, inputView) {
    private var mRVMenuLayout: RecyclerView? = null
    private var mTheme: Theme? = null
    private var settingsAdapter: MenuAdapter? = null //设置菜单网格适配器
    private var selectModeAdapter: MenuAdapter? = null //切换键盘列表适配器
    private var itemTouchHelper: ItemTouchHelper? = null
    private var dragEnabled: Boolean = false
    val funItems: MutableList<SkbFunItem> = LinkedList()   //键盘菜单对象
    private val selectModeItems: MutableList<SkbFunItem> = LinkedList()   //切换键盘列表数据
    init {
        initView(context)
    }

    private fun initView(context: Context) {
        mTheme = activeTheme
        mRVMenuLayout = RecyclerView(context).apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator().apply {
                supportsChangeAnimations = false
                addDuration = 120L
                removeDuration = 120L
                moveDuration = 120L
                changeDuration = 120L
            }
            layoutManager = CustomGridLayoutManager(context, EnvironmentSingleton.instance.skbWidth / dp(100))
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        this.addView(mRVMenuLayout)
    }

    /**
     * 弹出键盘设置界面
     */
    fun showSettingsView(editMode: Boolean = false) {
        funItems.clear()
        for(item in DataBaseKT.instance.skbFunDao().getAllMenu()){
            val skbMenuMode = menuSkbFunsPreset[SkbMenuMode.decode(item.name)]
            if(skbMenuMode != null)funItems.add(skbMenuMode)
        }
        val currentAdapter = settingsAdapter
        if (currentAdapter == null) {
            val menuAdapter = MenuAdapter(context, funItems)
            menuAdapter.setOnItemClickLitener { _: RecyclerView.Adapter<*>?, _: View?, position: Int ->
                onMenuItemClick(position)
            }
            settingsAdapter = menuAdapter
            showAdapter(menuAdapter)
        } else {
            showAdapter(currentAdapter)
            currentAdapter.notifyDataSetChanged()
        }
        enableDragItem(editMode)
    }

    private fun onMenuItemClick(position: Int) {
        val item = funItems.getOrNull(position) ?: return
        DevicesUtils.tryVibrate(mRVMenuLayout)
        inputView.onSettingsMenuClick(item.skbMenuMode)
    }

    /**
     * 候选栏菜单顺序/内容变化后刷新加减号状态
     */
    fun refreshBarMenuState() {
        settingsAdapter?.notifyDataSetChanged()
    }

    /**
     * 定制工具栏：拖拽排序 + 菜单项增删
     */
    fun enableDragItem(enable: Boolean) {
        if (dragEnabled == enable) return
        dragEnabled = enable
        val recyclerView = mRVMenuLayout ?: return
        val touchHelper = ensureItemTouchHelper()
        touchHelper.attachToRecyclerView(if (enable) recyclerView else null)
        settingsAdapter?.dragOverListener = if (enable) dragOverListener else null
        settingsAdapter?.notifyItemRangeChanged(0, settingsAdapter?.itemCount ?: 0)
    }

    private val dragOverListener = object : MenuAdapter.DragOverListener {
        override fun onOptionClick(parent: RecyclerView.Adapter<*>?, v: SkbFunItem, position: Int) {
            val inBarMenu = SkbMenuOrderManager.isInBarMenu(v.skbMenuMode)
            val changed = if (inBarMenu) {
                SkbMenuOrderManager.removeBarMenu(v.skbMenuMode)
            } else {
                SkbMenuOrderManager.addBarMenu(v.skbMenuMode)
            }
            if (!changed) return
            DevicesUtils.tryVibrate(mRVMenuLayout)
            inputView.updateCandidateBar()
            settingsAdapter?.notifyDataSetChanged()
        }
    }

    private fun ensureItemTouchHelper(): ItemTouchHelper {
        itemTouchHelper?.let { return it }
        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END, 0)
            }

            override fun onMove(recyclerView: RecyclerView, oldHolder: RecyclerView.ViewHolder, targetHolder: RecyclerView.ViewHolder): Boolean {
                val fromPosition = oldHolder.bindingAdapterPosition //得到拖动ViewHolder的position
                val toPosition = targetHolder.bindingAdapterPosition //得到目标ViewHolder的position
                if (fromPosition == RecyclerView.NO_POSITION || toPosition == RecyclerView.NO_POSITION) return false
                //使用集合工具类Collections，分别把中间所有的item的位置重新交换
                if (fromPosition < toPosition) {
                    for (i in fromPosition until toPosition) {
                        Collections.swap(funItems, i, i + 1)
                    }
                } else {
                    for (i in fromPosition downTo toPosition + 1) {
                        Collections.swap(funItems, i, i - 1)
                    }
                }
                settingsAdapter?.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun canDropOver(recyclerView: RecyclerView, current: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = true

            // 长按拖拽：交给 ItemTouchHelper 自己判断长按，普通滑动仍然是滚动列表
            override fun isLongPressDragEnabled() = true

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState != ItemTouchHelper.ACTION_STATE_DRAG) return
                viewHolder?.itemView?.let { view ->
                    view.animate().scaleX(1.05f).scaleY(1.05f).setDuration(120L).start()
                    view.alpha = 0.95f
                }
                DevicesUtils.tryVibrate(mRVMenuLayout)
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.animate().scaleX(1f).scaleY(1f).setDuration(120L).start()
                viewHolder.itemView.alpha = 1f
                // 松手后再落库，避免拖拽过程中主线程反复写数据库
                SkbMenuOrderManager.persistSettingMenus(funItems)
            }
        })
        itemTouchHelper = touchHelper
        return touchHelper
    }

    /**
     * 弹出键盘界面
     */
    fun showSkbSelelctModeView() {
        enableDragItem(false)
        selectModeItems.clear()
        selectModeItems.add(
            SkbFunItem(
                mContext.getString(R.string.keyboard_name_t9),
                R.drawable.selece_input_mode_py9,
                SkbMenuMode.PinyinT9
            )
        )
        selectModeItems.add(
            SkbFunItem(
                mContext.getString(R.string.keyboard_name_cn26),
                R.drawable.selece_input_mode_py26,
                SkbMenuMode.Pinyin26Jian
            )
        )
        selectModeItems.add(
            SkbFunItem(
                mContext.getString(R.string.keyboard_name_hand),
                R.drawable.selece_input_mode_handwriting,
                SkbMenuMode.PinyinHandWriting
            )
        )
        val doublePYSchemaMode = AppPrefs.getInstance().input.doublePYSchemaMode.getValue()
        val doublePinyinSchemaName = when (doublePYSchemaMode) {
            DoublePinyinSchemaMode.flypy -> R.string.double_pinyin_flypy_plus
            DoublePinyinSchemaMode.natural -> R.string.double_pinyin_natural
            DoublePinyinSchemaMode.abc -> R.string.double_pinyin_abc
            DoublePinyinSchemaMode.mspy -> R.string.double_pinyin_mspy
            DoublePinyinSchemaMode.sogou -> R.string.double_pinyin_sougou
            DoublePinyinSchemaMode.ziguang -> R.string.double_pinyin_ziguang
        }
        selectModeItems.add(
            SkbFunItem(
                mContext.getString(doublePinyinSchemaName),
                R.drawable.selece_input_mode_dpy26,
                SkbMenuMode.Pinyin26Double
            )
        )
        selectModeItems.add(
            SkbFunItem(
                mContext.getString(R.string.keyboard_name_pinyin_lx_17),
                R.drawable.selece_input_mode_lx17,
                SkbMenuMode.PinyinLx17
            )
        )
        selectModeItems.add(
            SkbFunItem(
                mContext.getString(R.string.keyboard_name_stroke),
                R.drawable.selece_input_mode_stroke,
                SkbMenuMode.PinyinStroke
            )
        )
        val currentAdapter = selectModeAdapter
        if (currentAdapter == null) {
            val menuAdapter = MenuAdapter(context, selectModeItems)
            menuAdapter.setOnItemClickLitener { _: RecyclerView.Adapter<*>?, _: View?, position: Int ->
                DevicesUtils.tryVibrate(mRVMenuLayout)
                selectModeItems.getOrNull(position)?.let { onKeyboardMenuClick(it) }
            }
            selectModeAdapter = menuAdapter
            showAdapter(menuAdapter)
        } else {
            showAdapter(currentAdapter)
            currentAdapter.notifyDataSetChanged()
        }
    }

    private fun showAdapter(menuAdapter: MenuAdapter) {
        if (mRVMenuLayout?.adapter !== menuAdapter) mRVMenuLayout?.adapter = menuAdapter
    }

    private fun onKeyboardMenuClick(data: SkbFunItem) {
        val value = when (data.skbMenuMode) {
            SkbMenuMode.Pinyin26Jian -> Pair(InputModeSwitcher.MASK_SKB_LAYOUT_QWERTY_PINYIN, CustomConstant.SCHEMA_ZH_QWERTY)
            SkbMenuMode.PinyinHandWriting -> Pair(InputModeSwitcher.MASK_SKB_LAYOUT_HANDWRITING, CustomConstant.SCHEMA_ZH_HANDWRITING)
            SkbMenuMode.PinyinLx17 -> Pair(InputModeSwitcher.MASK_SKB_LAYOUT_LX17, CustomConstant.SCHEMA_ZH_DOUBLE_LX17)
            SkbMenuMode.PinyinStroke -> Pair(InputModeSwitcher.MASK_SKB_LAYOUT_STROKE, CustomConstant.SCHEMA_ZH_STROKE)
            SkbMenuMode.Pinyin26Double -> Pair(InputModeSwitcher.MASK_SKB_LAYOUT_QWERTY_PINYIN, CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY + AppPrefs.getInstance().input.doublePYSchemaMode.getValue())
            else -> Pair(InputModeSwitcher.MASK_SKB_LAYOUT_T9_PINYIN, CustomConstant.SCHEMA_ZH_T9)
        }
        InputModeSwitcher.switchModeForSetting(value)
        inputView.resetToIdleState()
    }
}