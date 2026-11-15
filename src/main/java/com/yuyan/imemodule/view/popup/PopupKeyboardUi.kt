
package com.yuyan.imemodule.view.popup

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import com.yuyan.imemodule.application.Launcher
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.prefs.behavior.PopupMenuMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.utils.StringUtils
import splitties.dimensions.dp
import splitties.views.dsl.core.Ui
import splitties.views.dsl.core.add
import splitties.views.dsl.core.frameLayout
import splitties.views.dsl.core.horizontalLayout
import splitties.views.dsl.core.lParams
import splitties.views.dsl.core.matchParent
import splitties.views.dsl.core.verticalLayout
import splitties.views.dsl.core.view
import splitties.views.gravityCenter
import splitties.views.gravityEnd
import splitties.views.gravityStart
import splitties.views.gravityTopEnd
import splitties.views.padding
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class PopupKeyboardUi(bounds: Rect, onDismissSelf: PopupContainerUi.() -> Unit = {}, private val radius: Float, private val keyWidth: Int, private val keys: Array<String>) : PopupContainerUi(Launcher.instance.context, bounds, onDismissSelf) {

    class PopupKeyUi(override val ctx: Context, val theme: Theme, val text: String, highlight: Drawable) : Ui {

        val textView = view(::AutoScaleTextView) {
            text = this@PopupKeyUi.text
            scaleMode = AutoScaleTextView.Mode.Proportional
            setTextSize(TypedValue.COMPLEX_UNIT_PX, EnvironmentSingleton.instance.keyTextSize * 1.2f)
            setTextColor(theme.keyTextColor)
        }

        val textViewSdb = view(::AutoScaleTextView) {
            text = "半"
            padding = dp(3)
            scaleMode = AutoScaleTextView.Mode.Proportional
            setTextSize(TypedValue.COMPLEX_UNIT_PX, EnvironmentSingleton.instance.keyTextSmallSize * 0.8f)
            setTextColor(theme.keyTextColor)
        }

        /**
         * 高亮层：换格时只切换可见性，不改 background，
         * 避免每次换格都触发一次 requestLayout
         */
        val highlightView = View(ctx).apply {
            background = highlight
            visibility = View.INVISIBLE
        }

        override val root = frameLayout {
            add(highlightView, lParams(width = matchParent, height = matchParent))
            add(textView, lParams {
                gravity = gravityCenter
            })
            if(StringUtils.isDBCSymbol(this@PopupKeyUi.text)){
                add(textViewSdb, lParams {
                    gravity = gravityTopEnd
                })
            }
        }
    }

    private val inactiveBackground = GradientDrawable().apply {
        cornerRadius = radius
        setColor(ThemeManager.activeTheme.popupBackgroundColor)
    }

    private val focusBackground = GradientDrawable().apply {
        cornerRadius = radius
        setColor(ThemeManager.activeTheme.accentKeyBackgroundColor)
    }

    private val rowCount: Int
    private val columnCount: Int

    // those 2 variables meas initial focus row/column during initialization
    private val focusRow: Int
    private val focusColumn: Int

    // 当前焦点所在的逻辑行/列，与 focusRow/focusColumn 一起构成格坐标映射
    private var currentRow: Int
    private var currentColumn: Int

    init {
        val keyCount: Float = keys.size.toFloat()
        rowCount = ceil(keyCount / 5).toInt()
        columnCount = (keyCount / rowCount).roundToInt()
        focusRow = 0
        focusColumn = calcInitialFocusedColumn(columnCount, keyWidth, bounds)
        currentRow = focusRow
        currentColumn = focusColumn
    }

    override val offsetY = 0 - bounds.height() * (rowCount - 1)

    private val columnOrder = createColumnOrder(columnCount, focusColumn)

    private val keyOrders = Array(rowCount) { row ->
        IntArray(columnCount) { col -> row * columnCount + columnOrder[col] }
    }

    /**
     * 每行的视觉槽位表：视觉列 -> 键索引，-1 表示该列没有键。
     */
    private val slotKeys = Array(rowCount) { row ->
        val order = keyOrders[row]
        val visible = order.filter { it < keys.size }
        val start = if (order[0] < keys.size) 0 else columnCount - visible.size
        IntArray(columnCount) { col ->
            val offset = col - start
            if (offset in visible.indices) visible[offset] else -1
        }
    }

    private var focusedIndex = keyOrders[focusRow][focusColumn]

    private val keyUis = keys.map {
        PopupKeyUi(ctx, ThemeManager.activeTheme, it, focusBackground)
    }

    init {
        markFocus(focusedIndex)
    }

    override val root = verticalLayout root@{
        background = inactiveBackground
        outlineProvider = ViewOutlineProvider.BACKGROUND
        elevation = dp(2f)
        // add rows in reverse order, because newly added view shows at bottom
        for (i in rowCount - 1 downTo 0) {
            val order = keyOrders[i]
            add(horizontalLayout row@{
                for (j in 0 until columnCount) {
                    val keyUi = keyUis.getOrNull(order[j])
                    if (keyUi == null) {
                        // align columns to right (end) when first column is empty, eg.
                        // |   | 6 | 5 | 4 |(no free space)
                        // | 3 | 2 | 1 | 0 |(no free space)
                        gravity = if (j == 0) gravityEnd else gravityStart
                    } else {
                        add(keyUi.root, lParams(keyWidth, bounds.height()))
                    }
                }
            }, lParams(width = matchParent))
        }
    }

    private fun markFocus(index: Int) {
        keyUis.getOrNull(index)?.highlightView?.visibility = View.VISIBLE
    }

    private fun markInactive(index: Int) {
        keyUis.getOrNull(index)?.highlightView?.visibility = View.INVISIBLE
    }

    override fun onGestureEvent(distance: Float) {}

    /**
     * 复用前把焦点带回初始格，并清除上一次滑动留下的高亮
     */
    override fun resetForReuse() {
        currentRow = focusRow
        currentColumn = focusColumn
        keyUis.indices.forEach { markInactive(it) }
        focusedIndex = keyOrders[focusRow][focusColumn]
        markFocus(focusedIndex)
    }

    override fun onChangeFocus(x: Float, y: Float): Boolean {
        val columnFloat = focusColumn + x / keyWidth
        val rowFloat = focusRow - y / bounds.height()
        if (columnFloat < -2f || columnFloat > columnCount + 1f || rowFloat < -2f || rowFloat > rowCount + 1f) {
            onDismissSelf(this)
            return true
        }
        val newRow = settleIndex(currentRow, rowFloat, rowCount)
        val newColumn = settleIndex(currentColumn, columnFloat, columnCount)
        if (newRow == currentRow && newColumn == currentColumn) return false
        val newFocus = slotKeys[newRow][newColumn]
        currentRow = newRow
        currentColumn = newColumn
        if (newFocus < 0) return false
        if (newFocus != focusedIndex) {
            markInactive(focusedIndex)
            markFocus(newFocus)
            focusedIndex = newFocus
        }
        return false
    }

    private fun settleIndex(current: Int, target: Float, limit: Int): Int {
        var next = current
        if (target > next + SWITCH_GATE) {
            next = ceil(target - SWITCH_GATE).toInt().coerceAtMost(limit - 1)
        } else if (target < next - SWITCH_GATE) {
            next = floor(target + SWITCH_GATE).toInt().coerceAtLeast(0)
        }
        return next
    }

    override fun onTrigger(): Pair<PopupMenuMode, String> {
        return Pair(PopupMenuMode.Text, keys[focusedIndex])
    }

    private companion object {
        const val SWITCH_GATE = 0.55f
    }

}
