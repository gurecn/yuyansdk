
package com.yuyan.imemodule.view.popup

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.ViewOutlineProvider
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.Launcher
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.prefs.behavior.PopupMenuMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton
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
import kotlin.math.ceil
import kotlin.math.roundToInt

class PopupKeyboardMenuUi(bounds: Rect, onDismissSelf: PopupContainerUi.() -> Unit = {}, private val radius: Float, private val keyWidth: Int, private var isSelect: Boolean, private var popupMenuPair: Pair<PopupMenuMode, String>) : PopupContainerUi(Launcher.instance.context, bounds, onDismissSelf) {

    class PopupKeyUi(override val ctx: Context, val text: String) : Ui {
        val textView = view(::AutoScaleTextView) {
            text = this@PopupKeyUi.text
            scaleMode = AutoScaleTextView.Mode.Proportional
            setTextSize(TypedValue.COMPLEX_UNIT_PX, EnvironmentSingleton.instance.keyTextSize * 1.2f)
            setTextColor(ThemeManager.activeTheme.keyTextColor)
        }

        override val root = frameLayout {
            add(textView, lParams {
                gravity = gravityCenter
            })
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

    private val focusBackgroundClear = GradientDrawable().apply {
        cornerRadius = radius
        setColor(ctx.getColor(R.color.red_400))
    }

    private val rowCount: Int
    private val columnCount: Int

    private var lastX: Float
    private var lastY: Float

    private val focusRow: Int
    private val focusColumn: Int
    private var popupMenuMode: PopupMenuMode = PopupMenuMode.None

    private var keys:Array<String> = arrayOf(popupMenuPair.second)

    init {
        val keyCount: Float = keys.size.toFloat()
        rowCount = ceil(keyCount / 5).toInt()
        columnCount = (keyCount / rowCount).roundToInt()
        focusRow = 0
        focusColumn = calcInitialFocusedColumn(columnCount, keyWidth, bounds)
        lastX = 0f
        lastY = 0f
    }

    override val offsetY = 0 - bounds.height() * (rowCount - 1)

    private val columnOrder = createColumnOrder(columnCount, focusColumn)

    private val keyOrders = Array(rowCount) { row ->
        IntArray(columnCount) { col -> row * columnCount + columnOrder[col] }
    }

    private var focusedIndex = keyOrders[focusRow][focusColumn]

    private var keyUis = keys.map {
        PopupKeyUi(ctx, it)
    }

    init {
       if(isSelect)markFocus(focusedIndex)
    }

    override val root = verticalLayout root@{
        background = inactiveBackground
        outlineProvider = ViewOutlineProvider.BACKGROUND
        elevation = dp(2f)
        for (i in rowCount - 1 downTo 0) {
            val order = keyOrders[i]
            add(horizontalLayout row@{
                for (j in 0 until columnCount) {
                    val keyUi = keyUis.getOrNull(order[j])
                    if (keyUi == null) {
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
            root.background =  if(popupMenuPair.first == PopupMenuMode.Clear)focusBackgroundClear else focusBackground
            textView.setTextColor(ThemeManager.activeTheme.keyTextColor)
        }
    }

    private fun markInactive() {
        keyUis.getOrNull(0)?.apply {
            root.background = null
            textView.setTextColor(ThemeManager.activeTheme.keyTextColor)
        }
    }

    override fun onGestureEvent(distanceX: Float) {
        if(popupMenuPair.first == PopupMenuMode.Clear){
            isSelect = distanceX > 0
            if(popupMenuMode == PopupMenuMode.None){
                if(isSelect){
                    popupMenuMode = PopupMenuMode.Clear
                } else {
                    popupMenuMode = PopupMenuMode.Revertl
                    popupMenuPair = Pair(PopupMenuMode.Revertl,  "🔄 下滑还原")
                    keys = arrayOf(popupMenuPair.second)
                    keyUis.getOrNull(0)?.apply {
                        textView.text = popupMenuPair.second
                    }
                    isSelect = true
                }
            }
        }else if(popupMenuPair.first == PopupMenuMode.Revertl){
            isSelect = distanceX < 0
        }
        if(isSelect)markFocus(0) else markInactive()
    }
    override fun onChangeFocus(x: Float, y: Float): Boolean {
        return false
    }

    override fun onTrigger(): Pair<PopupMenuMode, String> {
        return if(isSelect)popupMenuPair else Pair(PopupMenuMode.None, "")
    }
}
