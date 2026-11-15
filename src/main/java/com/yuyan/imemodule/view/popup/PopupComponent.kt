
package com.yuyan.imemodule.view.popup

import android.graphics.Rect
import android.view.KeyEvent
import android.view.View
import com.yuyan.imemodule.application.Launcher
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.manager.InputModeSwitcher
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

    /**
     * 弹窗容器缓存：长按同一个键时直接复用已构建好的 View 树，
     * 只需重置焦点状态并重新挂载，避免每次长按重建全套 TextView。
     * accessOrder = true，按最近使用淘汰。
     */
    private val containerCache = object : LinkedHashMap<String, PopupContainerUi>(4, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, PopupContainerUi>?): Boolean {
            return size > MAX_CACHED_CONTAINERS
        }
    }

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

        private const val MAX_CACHED_CONTAINERS = 3
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

    /**
     * @return 是否真的弹出了符号弹窗（预设为空时不会显示）
     */
    fun showKeyboard(label: String, labelSmall: String, bounds: Rect): Boolean {
        var labels =  (PopupSmallPreset[labelSmall] ?: emptyArray<String>()).plus(PopupPreset[label] ?: emptyArray())
        if(labels.isEmpty())labels = PopupPreset[labelSmall] ?: emptyArray()
        if(labels.isNotEmpty()) {
            reallyShowKeyboard(labels, bounds)
            return true
        }
        dismissPopup()
        return false
    }

    fun showKeyboardMenu(mCurrentKey: SoftKey, bounds: Rect, distanceY: Float) {
        val key = when(mCurrentKey.code) {
            InputModeSwitcher.USER_KEYCODE_LANG ->  Pair(PopupMenuMode.SwitchIME, "🌐")
            InputModeSwitcher.USER_KEYCODE_COMMA_EMOJI ->  Pair(PopupMenuMode.EMOJI, "😆")
            KeyEvent.KEYCODE_SHIFT_LEFT -> {
                Pair(PopupMenuMode.EnglishCell, if(AppPrefs.getInstance().input.abcSearchEnglishCell.getValue()) "直输模式" else "拼写模式")
            }
            KeyEvent.KEYCODE_DEL -> Pair(PopupMenuMode.Clear,  "🔙 上滑清空")
            InputModeSwitcher.USER_KEYCODE_CURSOR_DIRECTION -> Pair(PopupMenuMode.Move,  "")
            else ->  Pair(PopupMenuMode.Enter,  "↩️ 换行")
        }
        reallyMenuKeyboard(key, bounds, mCurrentKey.code != KeyEvent.KEYCODE_DEL)
    }

    fun onGestureEvent(distance: Float) {
        showingContainerUi?.onGestureEvent(distance)
    }

    private fun reallyMenuKeyboard(key: Pair<PopupMenuMode, String>, bounds: Rect, isSelect: Boolean,) {
        val popupWidth = EnvironmentSingleton.instance.skbWidth.div(10) * key.second.length / 2
        val signature = containerSignature('M', bounds) { "${key.first}|${key.second}|$isSelect|$popupWidth" }
        val keyboardUi = obtainContainer(signature) {
            PopupKeyboardMenuUi(bounds, { dismissPopup() }, popupRadius, popupWidth, isSelect, key)
        }
        attachContainer(keyboardUi, bounds)
    }

    private fun reallyShowKeyboard(keys: Array<String>, bounds: Rect) {
        val popupWidth = EnvironmentSingleton.instance.skbWidth.div(10)
        val signature = containerSignature('S', bounds) { keys.joinToString("\u0001") }
        val keyboardUi = obtainContainer(signature) {
            PopupKeyboardUi(bounds, { dismissPopup() }, popupRadius, popupWidth, keys)
        }
        attachContainer(keyboardUi, bounds)
    }

    /**
     * 容器挂载：先卸下旧容器与气泡再上新，避免瞬间存在两个容器触发多余的一轮布局
     */
    private fun attachContainer(containerUi: PopupContainerUi, bounds: Rect) {
        dismissPopup()
        root.apply {
            add(containerUi.root, lParams {
                bottomMargin = EnvironmentSingleton.instance.skbAreaHeight - (bounds.top + bounds.bottom)/2
                leftMargin = EnvironmentSingleton.instance.leftMarginWidth + bounds.left + containerUi.offsetX
            })
        }
        showingContainerUi = containerUi
    }

    private fun obtainContainer(signature: String, factory: () -> PopupContainerUi): PopupContainerUi {
        containerCache[signature]?.let {
            it.resetForReuse()
            return it
        }
        return factory().also { containerCache[signature] = it }
    }

    /**
     * 容器复用签名：位置、键盘尺寸、字号、主题或内容任一变化都需要重建
     */
    private fun containerSignature(tag: Char, bounds: Rect, content: () -> String): String {
        val env = EnvironmentSingleton.instance
        return buildString(64) {
            append(tag).append('|')
            append(bounds.left).append(',').append(bounds.top).append(',')
            append(bounds.right).append(',').append(bounds.bottom).append('|')
            append(env.skbWidth).append('|').append(env.keyTextSize).append('|')
            append(System.identityHashCode(ThemeManager.activeTheme)).append('|')
            append(content())
        }
    }

    fun changeFocus(x: Float, y: Float): Boolean {
        return showingContainerUi?.changeFocus(x, y) ?: false
    }

    fun triggerFocused(): Pair<PopupMenuMode, String> {
        return showingContainerUi?.onTrigger()?:Pair(PopupMenuMode.None, "")
    }

    fun isTriggerSelected(): Boolean {
        return showingContainerUi?.isTriggerSelected() ?: false
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
