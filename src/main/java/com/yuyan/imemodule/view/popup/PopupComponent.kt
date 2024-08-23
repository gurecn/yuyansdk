
package com.yuyan.imemodule.view.popup

import android.graphics.Rect
import android.view.View
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.utils.LogUtil
import com.yuyan.imemodule.view.keyboard.InputView
import kotlinx.coroutines.Job
import org.mechdancer.dependency.Dependent
import org.mechdancer.dependency.UniqueComponent
import org.mechdancer.dependency.manager.ManagedHandler
import org.mechdancer.dependency.manager.managedHandler
import splitties.views.bottomPadding
import splitties.views.dsl.core.add
import splitties.views.dsl.core.frameLayout
import splitties.views.dsl.core.lParams
import splitties.views.dsl.core.verticalLayout
import splitties.views.rightPadding
import java.util.LinkedList

class PopupComponent private constructor():
    UniqueComponent<PopupComponent>(), Dependent, ManagedHandler by managedHandler() {

    private val showingEntryUi = HashMap<Int, PopupEntryUi>()
    private val dismissJobs = HashMap<Int, Job>()
    private val freeEntryUi = LinkedList<PopupEntryUi>()

    private val showingContainerUi = HashMap<Int, PopupContainerUi>()

    private val popupRadius by lazy {
        ThemeManager.prefs.keyRadius.getValue().toFloat()
    }
    private val hideThreshold = 100L

    val root by lazy {
        ImeSdkApplication.context.frameLayout {
            // we want (0, 0) at top left
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            isClickable = false
            isFocusable = false
        }
    }

    companion object{
        private var instance:PopupComponent?=null
            get() {
                if (field ==null){
                    field = PopupComponent()
                }
                return field
            }
        fun get():PopupComponent{
            return instance!!
        }
    }

    private fun showPopup(viewId: Int, content: String, bounds: Rect) {
        showingEntryUi[viewId]?.apply {
            dismissJobs[viewId]?.also {
                dismissJobs.remove(viewId)?.cancel()
            }
            lastShowTime = System.currentTimeMillis()
            setText(content)
            return
        }
        val popup = (freeEntryUi.poll()
            ?: PopupEntryUi(ImeSdkApplication.context, ThemeManager.activeTheme, bounds.height(), popupRadius)).apply {
            lastShowTime = System.currentTimeMillis()
            setText(content)
        }
        var bottomPadding =
            if(!EnvironmentSingleton.instance.isLandscape && !ThemeManager.prefs.keyboardModeFloat.getValue()){
                AppPrefs.getInstance().internal.keyboardBottomPadding.getValue() + EnvironmentSingleton.instance.systemNavbarWindowsBottom} else { 0 }
        root.apply {
            add(popup.root, lParams(bounds.width(), bounds.height()) {
                bottomMargin = EnvironmentSingleton.instance.inputAreaHeight + bottomPadding - bounds.top
                leftMargin = bounds.left
            })
        }
        showingEntryUi[viewId] = popup
    }

    private fun updatePopup(viewId: Int, content: String) {
        showingEntryUi[viewId]?.setText(content)
    }

    private fun showKeyboard(viewId: Int, label: String, service: InputView?, bounds: Rect) {
        val keys = if(service != null && service.mInputModeSwitcher.isChinese) {
            PopupChinesePreset[label] ?: return
        } else {
            PopupPreset[label] ?: return
        }
        showingEntryUi[viewId]?.setText("") ?: showPopup(viewId, "", bounds)
        reallyShowKeyboard(viewId, keys, bounds)
    }

    private fun reallyShowKeyboard(viewId: Int, keys: Array<String>, bounds: Rect) {
        val popupWidth = EnvironmentSingleton.instance.skbWidth.div(10)
        val keyboardUi = PopupKeyboardUi(ImeSdkApplication.context, ThemeManager.activeTheme, bounds, { dismissPopup(viewId) }, popupRadius, popupWidth, bounds.height(), bounds.height(), keys, keys)
        val bottomPadding =
        if(!EnvironmentSingleton.instance.isLandscape && !ThemeManager.prefs.keyboardModeFloat.getValue()){
            AppPrefs.getInstance().internal.keyboardBottomPadding.getValue() + EnvironmentSingleton.instance.systemNavbarWindowsBottom} else { 0 }
        root.apply {
            add(keyboardUi.root, lParams {
                bottomMargin = EnvironmentSingleton.instance.inputAreaHeight  + bottomPadding - bounds.top
                leftMargin = bounds.left + keyboardUi.offsetX
            })
        }
        dismissPopup(viewId)
        showingContainerUi[viewId] = keyboardUi
    }

    private fun changeFocus(viewId: Int, x: Float, y: Float): Boolean {
        return showingContainerUi[viewId]?.changeFocus(x, y) ?: false
    }

    private fun triggerFocused(viewId: Int): String? {
        return showingContainerUi[viewId]?.onTrigger()
    }

    private fun dismissPopup(viewId: Int) {
        dismissPopupContainer(viewId)
        showingEntryUi[viewId]?.also {
            val timeLeft = it.lastShowTime + hideThreshold - System.currentTimeMillis()
            if (timeLeft <= 0L) {
                dismissPopupEntry(viewId, it)
            } else {
                dismissPopupEntry(viewId, it)
                dismissJobs.remove(viewId)
            }
        }
    }

    private fun dismissPopupContainer(viewId: Int) {
        showingContainerUi[viewId]?.also {
            showingContainerUi.remove(viewId)
            root.removeView(it.root)
        }
    }

    private fun dismissPopupEntry(viewId: Int, popup: PopupEntryUi) {
        showingEntryUi.remove(viewId)
        root.removeView(popup.root)
        freeEntryUi.add(popup)
    }

    val listener = PopupActionListener { action ->
        with(action) {
            when (this) {
                is PopupAction.ChangeFocusAction -> changeFocus(viewId, x, y)
                is PopupAction.DismissAction -> dismissPopup(viewId)
                is PopupAction.PreviewAction -> showPopup(viewId, content, bounds)
                is PopupAction.PreviewUpdateAction -> updatePopup(viewId, content)
                is PopupAction.ShowKeyboardAction -> showKeyboard(viewId, lable, service, bounds)
                is PopupAction.TriggerAction -> {
                    val text = triggerFocused(viewId)
                    service?.apply {
                        responseLongKeyEvent(text)
                    }
                }
            }
        }
    }
}
