package com.yuyan.imemodule.view.widget

import android.content.Context

class ImeEditText(context: Context) : androidx.appcompat.widget.AppCompatEditText(context) {
    fun commitText(content: String) {
        val start = selectionStart
        val end = selectionEnd
        if (editTextHasSelection(start, end)) {
            this.text?.replace(start, end, content)
        } else {
            this.text?.insert(start, content)
        }
    }
    private fun editTextHasSelection(selectionStart: Int, selectionEnd: Int): Boolean {
        return selectionStart >= 0 && selectionEnd > 0 && selectionStart != selectionEnd
    }
}