package com.yuyan.imemodule.service

import android.text.InputType
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection

/**
 * 方向键语义的光标移动。
 */
class CursorMover(private val connection: () -> InputConnection?, private val cache: EditorTextCache) {
    private var blockedStep = 0          // 已确认无法继续移动的方向
    private var blockedAt = -1           // 确认边界时的光标位置，光标一变自然失效
    private var remainingAfter = 0       // 光标后已知存在的字符数，即还能安全右移的步数
    private var probeUnsupported = false // 宿主不提供光标后文本，无法预判边界
    private var webEditor = false        // WebView 内核编辑器，不认定位到文本末尾的命令
    private var caretPos = -1            // 宿主最近一次回报的光标位置，移动推算只认它
    private var pendingTarget = -1       // 已发出但还没被回报确认的目标位置
    private var pendingRetries = 0       // 同一目标连续请求次数，用于识别不回报光标位置的宿主

    /** 每次开始输入重置会话状态 */
    fun onStartInput(editorInfo: EditorInfo?) {
        blockedStep = 0
        blockedAt = -1
        remainingAfter = 0
        probeUnsupported = false
        caretPos = -1
        pendingTarget = -1
        pendingRetries = 0
        webEditor = editorInfo != null &&
            editorInfo.inputType and InputType.TYPE_MASK_CLASS == InputType.TYPE_CLASS_TEXT &&
            editorInfo.inputType and InputType.TYPE_MASK_VARIATION == InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT
    }

    /**
     * 宿主回报光标位置：定位请求的真实结果只认这里，与请求不一致说明宿主没有采纳。
     */
    fun onSelectionReported(start: Int, end: Int) {
        if (start != end) { // 选区状态不做单点推算
            caretPos = -1
            pendingTarget = -1
            return
        }
        val expected = pendingTarget
        caretPos = start
        pendingTarget = -1
        pendingRetries = 0
        if (expected >= 0 && start != expected) {
            cache.onSelectionMovedByIme(start, start) // 回正镜像，不把漂移带进后续推算
            remainingAfter = 0                       // 位置已重新校准，边界需要重新探测
        }
    }

    /**
     * 移动一格光标
     */
    fun move(keyCode: Int) {
        settlePendingRequest()
        val step = if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) 1 else -1
        val selectionStart = cache.selectionStart
        val selectionEnd = cache.selectionEnd
        val hasSelection = selectionStart != selectionEnd
        val from = when {
            hasSelection -> if (step > 0) selectionEnd else selectionStart
            caretPos >= 0 -> caretPos
            else -> selectionStart
        }
        if (from < 0) return // 宿主未上报光标位置，无法安全推算目标位置
        if (!hasSelection && step == blockedStep && from == blockedAt) return // 已确认无法继续移动
        val target = if (hasSelection) from else from + step
        if (target < 0) return
        val inputConnection = connection() ?: return
        if (!hasSelection && step > 0 && !hasTextAfter(inputConnection)) {
            block(step, from)
            return
        }
        // WebView 把插入符挪到文本末尾只能靠方向键，定位命令会收起输入法
        val viaKey = !hasSelection && step > 0 && webEditor && remainingAfter == 1
        val accepted = if (viaKey) sendCaretKey(inputConnection, keyCode) else applySelection(inputConnection, target)
        if (accepted) {
            onMoveAccepted(step, target, hasSelection)
        } else if (!hasSelection) {
            block(step, from)
        }
    }

    /** 上一次请求还没被回报确认：优先用旧位置重发，多次无回报则退回按请求推算 */
    private fun settlePendingRequest() {
        if (pendingTarget < 0) return
        if (pendingRetries >= MAX_PENDING_RETRIES) {
            caretPos = pendingTarget
            pendingTarget = -1
            pendingRetries = 0
        } else {
            pendingRetries++
        }
    }

    /** 请求被宿主接受：余量按方向增减，目标位置等待回报确认 */
    private fun onMoveAccepted(step: Int, target: Int, hasSelection: Boolean) {
        blockedStep = 0
        blockedAt = -1
        // 右移一步消耗一个已知光标后字符，左移一步因为刚离开的位置必然有效而多出一个
        if (!hasSelection && !probeUnsupported) remainingAfter = (remainingAfter - step).coerceAtLeast(0)
        pendingTarget = target
        pendingRetries = 0
        cache.onSelectionMovedByIme(target, target)
    }

    private fun block(step: Int, at: Int) {
        blockedStep = step
        blockedAt = at
    }

    /** 光标后是否还有字符；已知余量不足时向宿主探测一次，一次探测的余量可支撑后续多步 */
    private fun hasTextAfter(inputConnection: InputConnection): Boolean {
        if (probeUnsupported) return true // 无法预判边界，保持不拦截的行为
        if (remainingAfter > 0) return true
        val textAfter = try {
            inputConnection.getTextAfterCursor(PROBE_LENGTH, 0)
        } catch (_: Exception) {
            null
        }
        if (textAfter == null) {
            probeUnsupported = true
            return true
        }
        remainingAfter = textAfter.length
        return remainingAfter > 0
    }

    private fun applySelection(inputConnection: InputConnection, target: Int): Boolean = try {
        inputConnection.setSelection(target, target)
    } catch (_: Exception) {
        false
    }

    /** 发一次真实方向键，让 WebView 渲染引擎自己把插入符挪到末尾 */
    private fun sendCaretKey(inputConnection: InputConnection, keyCode: Int): Boolean = try {
        val down = inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
        val up = inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
        down && up
    } catch (_: Exception) {
        false
    }

    companion object {
        private const val PROBE_LENGTH = 64      // 一次探测覆盖的右移步数，避免每步一次跨进程取文本
        private const val MAX_PENDING_RETRIES = 2 // 同一目标最多重发次数，超出后按请求推算位置
    }
}
