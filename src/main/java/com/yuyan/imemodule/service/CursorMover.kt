package com.yuyan.imemodule.service

import android.view.KeyEvent
import android.view.inputmethod.InputConnection

/**
 * 方向键语义的光标移动。
 */
class CursorMover(private val connection: () -> InputConnection?, private val cache: EditorTextCache, private val fallbackKeyEvent: (Int) -> Unit, ) {
    private var blockedStep = 0
    private var blockedAt = -1
    private var unsupported = false

    /** 每次开始输入重置会话状态 */
    fun onStartInput() {
        blockedStep = 0
        blockedAt = -1
        unsupported = false
    }

    /**
     * 移动一格光标
     */
    fun move(keyCode: Int) {
        val step = if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) 1 else -1
        val selectionStart = cache.selectionStart
        val selectionEnd = cache.selectionEnd
        val hasSelection = selectionStart != selectionEnd
        val from = when {
            !hasSelection -> selectionStart
            step > 0 -> selectionEnd
            else -> selectionStart
        }
        if (from < 0 || unsupported) {
            fallbackKeyEvent(keyCode) // 宿主未上报光标位置，无法安全推算目标位置
            return
        }
        if (!hasSelection && step == blockedStep && from == blockedAt) return // 已确认无法继续移动
        val target = if (hasSelection) from else from + step
        val inputConnection = connection()
        if (inputConnection == null) {
            fallbackKeyEvent(keyCode)
            return
        }
        if (target < 0 || !applySelection(inputConnection, target)) {
            // 已在文本首尾，或宿主不支持选区定位：保持光标不动，绝不把按键事件交给宿主
            if (hasSelection) return
            blockedStep = step
            blockedAt = from
            if (step > 0 && hasTextAfterCursor(inputConnection)) unsupported = true
            return
        }
        blockedStep = 0
        blockedAt = -1
        cache.onSelectionMovedByIme(target, target)
    }

    private fun applySelection(inputConnection: InputConnection, target: Int): Boolean = try {
        inputConnection.setSelection(target, target)
    } catch (_: Exception) {
        false
    }

    /** 右移被拒时光标后仍有文本，说明宿主不支持 setSelection 而不是已到末尾 */
    private fun hasTextAfterCursor(inputConnection: InputConnection): Boolean = try {
        !inputConnection.getTextAfterCursor(1, 0).isNullOrEmpty()
    } catch (_: Exception) {
        false
    }
}
