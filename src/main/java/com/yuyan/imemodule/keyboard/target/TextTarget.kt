package com.yuyan.imemodule.keyboard.target

/**
 * 键盘文本落点。
 */
interface TextTarget {

    /** 是否为输入法内部编辑器；内部编辑器不参与预编辑、自动空格等宿主侧流程 */
    val isLocalEditor: Boolean get() = false

    fun commit(text: CharSequence)

    fun setComposing(text: CharSequence)

    fun finishComposing()

    fun sendKey(keyCode: Int, shiftSelection: Boolean = false)

    fun deleteBackward(onDeleted: (String) -> Unit)

    fun deleteBackward(count: Int): String?

    fun clear(): String?

    fun restore(text: String): Boolean

    fun commitEditAction(id: Int)

    fun performEditorAction(action: Int)

    fun setSelection(start: Int, end: Int)
}
