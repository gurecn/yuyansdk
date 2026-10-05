package com.yuyan.imemodule.keyboard.target

import android.view.KeyEvent
import com.yuyan.imemodule.service.ImeService

/**
 * 宿主输入框落点，所有操作经 InputConnection 下发给第三方应用。
 */
class ImeConnectionTarget(private val service: ImeService) : TextTarget {

    override fun commit(text: CharSequence) = service.commitText(text.toString())

    override fun setComposing(text: CharSequence) = service.setComposingText(text)

    override fun finishComposing() = service.finishComposingText()

    override fun sendKey(keyCode: Int, shiftSelection: Boolean) {
        when (keyCode) {
            KeyEvent.KEYCODE_ENTER -> service.sendEnterKeyEvent()
            in KeyEvent.KEYCODE_DPAD_UP..KeyEvent.KEYCODE_DPAD_RIGHT ->
                service.sendCombinationKeyEvents(keyCode, shift = shiftSelection)
            else -> service.sendCombinationKeyEvents(keyCode)
        }
    }

    override fun deleteBackward(): String? {
        val deletedText = service.deleteLastCharBeforeCursor()
        if (deletedText == null) service.sendCombinationKeyEvents(KeyEvent.KEYCODE_DEL)
        return deletedText
    }

    override fun deleteBackward(count: Int): String? = service.deleteTextBeforeCursor(count)

    override fun clear(): String? = service.deleteTextBeforeCursor(CLEAR_TEXT_LENGTH)

    override fun restore(text: String): Boolean = service.restoreDeletedText(text)

    override fun commitEditAction(id: Int) = service.commitTextEditMenu(id)

    override fun performEditorAction(action: Int) = service.performEditorAction(action)

    override fun setSelection(start: Int, end: Int) = service.setSelection(start, end)

    companion object {
        private const val CLEAR_TEXT_LENGTH = 1000
    }
}
