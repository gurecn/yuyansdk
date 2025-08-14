package com.yuyan.imemodule.entity.keyboard

import android.graphics.drawable.Drawable
import com.yuyan.imemodule.keyboard.keyIconRecords
import java.util.Locale
import java.util.Objects

/**
 * 可以变换状态的按键。See [com.yuyan.imemodule.entity.keyboard.ToggleState] 按键状态
 */
class SoftKeyToggle(code: Int) : SoftKey(code = code) {
    private var mToggleStates: List<ToggleState>? = null

    fun setToggleStates(toggleStates: List<ToggleState>?) {
        mToggleStates = toggleStates
    }

    /**
     * 查找该按键是否有stateId的状态，如果有就切换到这个状态。
     * @return 返回状态是否切换.true:状态变化  false：无该状态或状态未变化。
     */
    fun enableToggleState(stateId: Int): Boolean {
        val oldStateId = super.stateId
        if (oldStateId == stateId) return false
        super.stateId = stateId
        return true
    }

    override val keyIcon: Drawable?
        get() {
            val state = toggleState
            return if (null != state) {
                keyIconRecords[Objects.hash(code, stateId)]
            } else super.keyIcon
        }

    override val keyLabel: String
        get() {
            val state = toggleState
            return state?.label ?: super.getkeyLabel()
        }

    override fun changeCase(upperCase: Boolean) {
        val state = toggleState
        if (state?.label != null) {
            state.label = if (upperCase) state.label.lowercase(Locale.getDefault())
            else state.label.uppercase(Locale.getDefault())
        }
    }

    private val toggleState: ToggleState?

        get() {
            for (state in mToggleStates!!) {
                if (state.stateId == stateId) {
                    return state
                }
            }
            return null
        }
}
