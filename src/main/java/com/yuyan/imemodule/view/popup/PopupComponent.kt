
package com.yuyan.imemodule.view.popup

import android.graphics.Rect
import android.view.KeyEvent
import android.view.View
import com.yuyan.imemodule.application.Launcher
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.PopupMenuMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import splitties.views.dsl.core.add
import splitties.views.dsl.core.frameLayout
import splitties.views.dsl.core.lParams
import java.util.LinkedList

class PopupComponent private constructor(){
    private var showingEntryUi:PopupEntryUi? = null
    private val freeEntryUi = LinkedList<PopupEntryUi>()

    private var showingContainerUi:PopupContainerUi? = null

    private val popupRadius by lazy {
        ThemeManager.prefs.keyRadius.getValue().toFloat()
    }

    val root by lazy {
        Launcher.instance.context.frameLayout {
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            isClickable = false
            isFocusable = false
        }
    }

    companion object{
        private var instance:PopupComponent?=null
            get() {
                if (field ==null) field = PopupComponent()
                return field
            }
        fun get():PopupComponent{
            return instance!!
        }
    }

    fun showPopup(content: String, bounds: Rect) {
        showingEntryUi?.apply {
            lastShowTime = System.currentTimeMillis()
            setText(content)
            return
        }
        val popup = (freeEntryUi.poll()
            ?: PopupEntryUi(Launcher.instance.context)).apply {
            lastShowTime = System.currentTimeMillis()
            setBackground(ThemeManager.activeTheme, popupRadius)
            setText(content)
        }
        root.apply {
            add(popup.root, lParams(bounds.width(), bounds.height()) {
                bottomMargin = EnvironmentSingleton.instance.skbAreaHeight - (bounds.top + bounds.bottom)/2
                leftMargin = EnvironmentSingleton.instance.leftMarginWidth + bounds.left
            })
        }
        showingEntryUi = popup
    }

    fun showKeyboard(label: String, labelSmall: String, bounds: Rect) {
        showingEntryUi?.setText("") ?: showPopup("", bounds)
        var labels =  (PopupSmallPreset[labelSmall] ?: emptyArray<String>()).plus(PopupPreset[label] ?: emptyArray())
        if(labels.isEmpty())labels = PopupPreset[labelSmall] ?: emptyArray()
        if(labels.isNotEmpty()) {
            reallyShowKeyboard(labels, bounds)
        } else {
            dismissPopup()
        }
    }

    fun showKeyboardMenu(mCurrentKey: SoftKey, bounds: Rect, distanceY: Float) {
        val key = when(mCurrentKey.code) {
            InputModeSwitcherManager.USER_DEF_KEYCODE_LANG_2 ->  Pair(PopupMenuMode.SwitchIME, "🌐")
            InputModeSwitcherManager.USER_DEF_KEYCODE_EMOJI_8 ->  Pair(PopupMenuMode.EMOJI, "😆")
            InputModeSwitcherManager.USER_DEF_KEYCODE_SHIFT_1 -> {
                Pair(PopupMenuMode.EnglishCell, if(AppPrefs.getInstance().input.abcSearchEnglishCell.getValue()) "直输模式" else "拼写模式")
            }
            KeyEvent.KEYCODE_DEL -> {
                if(distanceY < 0)  Pair(PopupMenuMode.Revertl,  "🔄 下滑还原") else Pair(PopupMenuMode.Clear,  "🔙 上滑清空")
            }
            InputModeSwitcherManager.USER_DEF_KEYCODE_CURSOR_DIRECTION_9 -> Pair(PopupMenuMode.Move,  "")
            else ->  Pair(PopupMenuMode.Enter,  "↩️ 换行")
        }
        showingEntryUi?.setText("") ?: showPopup("", bounds)
        reallyMenuKeyboard(key, bounds, mCurrentKey.code != KeyEvent.KEYCODE_DEL)
    }

    fun onGestureEvent(distanceX: Float) {
        showingContainerUi?.onGestureEvent(distanceX)
    }

    private fun reallyMenuKeyboard(key: Pair<PopupMenuMode, String>, bounds: Rect, isSelect: Boolean,) {
        val popupWidth = EnvironmentSingleton.instance.skbWidth.div(10) * key.second.length / 2
        val keyboardUi = PopupKeyboardMenuUi(bounds, { dismissPopup() }, popupRadius, popupWidth, isSelect, key)
        root.apply {
            add(keyboardUi.root, lParams {
                bottomMargin = EnvironmentSingleton.instance.skbAreaHeight - (bounds.top + bounds.bottom)/2
                leftMargin = EnvironmentSingleton.instance.leftMarginWidth + bounds.left + keyboardUi.offsetX
            })
        }
        dismissPopup()
        showingContainerUi= keyboardUi
    }

    private fun reallyShowKeyboard(keys: Array<String>, bounds: Rect) {
        val popupWidth = EnvironmentSingleton.instance.skbWidth.div(10)
        val keyboardUi = PopupKeyboardUi(bounds, { dismissPopup() }, popupRadius, popupWidth, keys)
        root.apply {
            add(keyboardUi.root, lParams {
                bottomMargin = EnvironmentSingleton.instance.skbAreaHeight - (bounds.top + bounds.bottom)/2
                leftMargin = EnvironmentSingleton.instance.leftMarginWidth + bounds.left + keyboardUi.offsetX
            })
        }
        dismissPopup()
        showingContainerUi= keyboardUi
    }

    fun changeFocus(x: Float, y: Float): Boolean {
        return showingContainerUi?.changeFocus(x, y) ?: false
    }

    fun triggerFocused(): Pair<PopupMenuMode, String> {
        return showingContainerUi?.onTrigger()?:Pair(PopupMenuMode.None, "")
    }

    fun dismissPopup() {
        dismissPopupContainer()
        showingEntryUi?.also {
            showingEntryUi = null
            root.removeView(it.root)
            freeEntryUi.add(it)
        }
    }

    private fun dismissPopupContainer() {
        showingContainerUi?.also {
            showingContainerUi = null
            root.removeView(it.root)
        }
    }
}
