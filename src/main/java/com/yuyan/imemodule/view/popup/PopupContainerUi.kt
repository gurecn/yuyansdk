package com.yuyan.imemodule.view.popup

import android.content.Context
import android.graphics.Rect
import android.view.View
import com.yuyan.imemodule.prefs.behavior.PopupMenuMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import splitties.views.dsl.core.Ui
import kotlin.math.max
import kotlin.math.roundToInt

abstract class PopupContainerUi(override val ctx: Context, val bounds: Rect, val onDismissSelf: PopupContainerUi.() -> Unit) : Ui {

    /**
     * Popup container view
     */
    abstract override val root: View

    /**
     * Offset on X axis to put this [PopupKeyboardUi] relative to popup trigger view [bounds]
     */
    var offsetX = 0

    /**
     * Offset on Y axis to put this [PopupKeyboardUi] relative to popup trigger view [bounds]
     */
    abstract val offsetY: Int

    fun calcInitialFocusedColumn(columnCount: Int, columnWidth: Int, bounds: Rect): Int {
        val leftSpace = bounds.left
        val rightSpace = max(EnvironmentSingleton.instance.skbWidth - bounds.right, 0)
        var col = (columnCount - 1) / 2
        while (columnWidth * col > leftSpace) col--
        while (columnWidth * (columnCount - col - 1) > rightSpace) col++
        offsetX = ((bounds.width() - columnWidth) / 2) - (columnWidth * col)
        val diff = columnWidth - bounds.width()
        if(diff > 5) {
            if(leftSpace < diff/2)offsetX = leftSpace
            if(rightSpace < diff/2)offsetX = bounds.width() - columnWidth * col - columnWidth
        }
        return col
    }

    fun createColumnOrder(columnCount: Int, initialFocus: Int) = IntArray(columnCount).also {
        var order = 0
        it[initialFocus] = order++
        for (i in 1 until columnCount * 2) {
            val sign = if (i % 2 == 0) -1 else 1
            val delta = (i / 2f).roundToInt()
            val nextColumn = initialFocus + sign * delta
            if (nextColumn < 0 || nextColumn >= columnCount) continue
            it[nextColumn] = order++
        }
    }

    fun changeFocus(x: Float, y: Float): Boolean {
        return onChangeFocus(x - offsetX, y - offsetY)
    }

    abstract fun onChangeFocus(x: Float, y: Float): Boolean

    abstract fun onGestureEvent(distanceX: Float)

    abstract fun onTrigger(): Pair<PopupMenuMode, String>

    companion object {
        fun limitIndex(i: Int, limit: Int) = if (i < 0) 0 else if (i >= limit) limit - 1 else i
    }
}
