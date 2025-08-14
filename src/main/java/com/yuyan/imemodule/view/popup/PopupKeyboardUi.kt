
package com.yuyan.imemodule.view.popup

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
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

/**
 * @param theme [Theme]
 * @param bounds bound [Rect] of popup trigger view. Used to calculate free space of both sides and
 * determine column order. See [focusColumn].
 * @param onDismissSelf callback when popup keyboard wants to close
 * @param radius popup keyboard and key radius
 * @param keyWidth key width in popup keyboard
 * trigger view to popup keyboard view. See [offsetX] and [offsetY].
 * @param keys character to commit when triggered
 */
class PopupKeyboardUi(bounds: Rect, onDismissSelf: PopupContainerUi.() -> Unit = {}, private val radius: Float, private val keyWidth: Int, private val keys: Array<String>) : PopupContainerUi(Launcher.instance.context, bounds, onDismissSelf) {

    class PopupKeyUi(override val ctx: Context, val theme: Theme, val text: String) : Ui {

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

        override val root = frameLayout {
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

    private var lastX: Float
    private var lastY: Float

    // those 2 variables meas initial focus row/column during initialization
    private val focusRow: Int
    private val focusColumn: Int

    init {
        val keyCount: Float = keys.size.toFloat()
        rowCount = ceil(keyCount / 5).toInt()
        columnCount = (keyCount / rowCount).roundToInt()
        focusRow = 0
        focusColumn = calcInitialFocusedColumn(columnCount, keyWidth, bounds)
        lastX = 0f
        lastY = 0f
    }

    /**
     * Offset on X axis made up of 2 parts:
     *  1. from trigger view bounds left to popup entry view left
     *  2. from left-most column to initial focused column
     *
     * Offset on Y axis made up of 2 parts as well:
     *  1. from trigger view top to popup entry view top
     *  2. from top-most row to initial focused row (bottom row)
     *
     * ```
     *                    c───┬───┬───┐
     *                    │   │ 4 │ 5 │
     *                 ┌─ ├───p───┼───┤ ─┐
     *   popupKeyHeight│  │ 3 │ 1 │ 2 │  │
     *                 └─ └───┼───┼───┘  │
     *                        │   │      │popupHeight
     *                 ┌───── │o─┐│      │
     *  bounds.height()│      ││a││      │
     *                 └───── └┴─┴┘ ─────┘
     * ```
     * o: trigger view top-left origin
     *
     * p: popup preview ([PopupEntryUi]) top-left origin
     *
     * c: container view top-left origin
     *
     * Applying only `1.` parts of both X and Y offset, the origin should transform from `o` to `p`.
     * `2.` parts of both offset transform it from `p` to `c`.
     */
    override val offsetY = 0 - bounds.height() * (rowCount - 1)

    private val columnOrder = createColumnOrder(columnCount, focusColumn)

    /**
     * row with smaller index displays at bottom.
     * for example, keyOrders array:
     * ```
     * [[2, 0, 1, 3], [6, 4, 5, 7]]
     * ```
     * displays as
     * ```
     * | 6 | 4 | 5 | 7 |
     * | 2 | 0 | 1 | 3 |
     * ```
     * in which `0` indicates default focus
     */
    private val keyOrders = Array(rowCount) { row ->
        IntArray(columnCount) { col -> row * columnCount + columnOrder[col] }
    }

    private var focusedIndex = keyOrders[focusRow][focusColumn]

    private val keyUis = keys.map {
        PopupKeyUi(ctx, ThemeManager.activeTheme, it)
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
        keyUis.getOrNull(index)?.apply {
            root.background = focusBackground
            textView.setTextColor(theme.keyTextColor)
            textViewSdb.setTextColor(theme.keyTextColor)
        }
    }

    private fun markInactive(index: Int) {
        keyUis.getOrNull(index)?.apply {
            root.background = null
            textView.setTextColor(theme.keyTextColor)
            textViewSdb.setTextColor(theme.keyTextColor)
        }
    }

    override fun onGestureEvent(distanceX: Float) {}

    override fun onChangeFocus(x: Float, y: Float): Boolean {
        if(lastX == 0f){
            lastX = x
            lastY = y
            return false
        }
        // move to next row when gesture moves above 30% from bottom of current row
        var newRow = focusRow - ((y-lastY) / bounds.height() - 0.2).roundToInt()
        // move to next column when gesture moves out of current column
        var newColumn = focusColumn + floor((x-lastX) / keyWidth).toInt()
        // retain focus when gesture moves between ±2 rows/columns of range
        if (newRow < -2 || newRow > rowCount + 1 || newColumn < -2 || newColumn > columnCount + 1) {
            onDismissSelf(this)
            return true
        }
        newRow = limitIndex(newRow, rowCount)
        newColumn = limitIndex(newColumn, columnCount)
        val newFocus = keyOrders[newRow][newColumn]
        if (newFocus < keyUis.size) {
            markInactive(focusedIndex)
            markFocus(newFocus)
            focusedIndex = newFocus
        }
        return false
    }

    override fun onTrigger(): Pair<PopupMenuMode, String> {
        return Pair(PopupMenuMode.Text, keys[focusedIndex])
    }

}
