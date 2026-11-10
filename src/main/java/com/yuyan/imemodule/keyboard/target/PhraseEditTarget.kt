package com.yuyan.imemodule.keyboard.target

import android.view.KeyEvent
import com.yuyan.imemodule.view.EditPhrasesView

/**
 * 输入法内部编辑器落点，用于常用语编辑等不经过宿主编辑器的场景。
 */
class PhraseEditTarget(private val view: EditPhrasesView, private val onSubmit: () -> Unit) : TextTarget {
    override val isLocalEditor = true

    override fun commit(text: CharSequence) = view.commitText(text.toString())

    override fun setComposing(text: CharSequence) = Unit

    override fun finishComposing() = Unit

    override fun sendKey(keyCode: Int, shiftSelection: Boolean) {
        when (keyCode) {
            KeyEvent.KEYCODE_DEL -> view.deleteBackward()
            KeyEvent.KEYCODE_ENTER -> onSubmit()
            KeyEvent.KEYCODE_DPAD_LEFT -> view.moveCursor(-1)
            KeyEvent.KEYCODE_DPAD_RIGHT -> view.moveCursor(1)
            else -> view.insertKeyChar(keyCode)
        }
    }
    override fun deleteBackward(onDeleted: (String) -> Unit) = view.deleteBackward()

    override fun deleteBackward(count: Int): String? {
        view.deleteBackward(count)
        return null
    }

    override fun clear(): String? {
        view.clearPhrasesContent()
        return null
    }

    override fun restore(text: String): Boolean = false

    override fun commitEditAction(id: Int) = Unit

    override fun performEditorAction(action: Int) = Unit

    override fun setSelection(start: Int, end: Int) = Unit
}
