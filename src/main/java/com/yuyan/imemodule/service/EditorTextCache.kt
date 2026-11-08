package com.yuyan.imemodule.service

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection

/**
 * 光标前文本缓存。
 *
 * InputConnection 的取文本接口是同步跨进程调用，目标应用响应慢时会阻塞输入法主线程。
 * 该方案在输入法侧镜像光标前文本：按键处理只读本地缓存，仅在输入开始、光标跳变等
 * 少数时机做一次延迟合并的校准，校准失败或应用响应过慢时放弃缓存而不是继续阻塞。
 * 当前方案存在同步风险，会导致缓存和输入框内容出现失步情况。
 */
class EditorTextCache(private val connection: () -> InputConnection?) {
    private val handler = Handler(Looper.getMainLooper())
    private val committedText = StringBuilder()
    private var composingText = ""
    private var valid = false
    private var cachedCursorPos = -1   // 缓存内容对应的光标位置
    private var reportedCursorPos = -1 // 应用最近一次上报的光标位置
    private var reportedSelEnd = -1    // 应用最近一次上报的选区末端
    private val selectionActive: Boolean get() = reportedCursorPos != reportedSelEnd
    private val pendingCursorPositions = ArrayDeque<Int>()
    private var slowResponseCount = 0  // 连续慢响应次数，超限后不再校准
    private var refreshPending = false
    private var pendingLength = 0
    private var pendingCallback: ((String) -> Unit)? = null
    private val refreshTask = Runnable { refresh() }

    /** 应用最近一次上报的选区，光标位置未知时为 -1 */
    val selectionStart: Int get() = reportedCursorPos
    val selectionEnd: Int get() = reportedSelEnd

    /**
     * 输入开始，优先从 EditorInfo 取初始文本，无需跨进程调用。
     */
    fun onStartInput(editorInfo: EditorInfo?) {
        cancelRefresh()
        slowResponseCount = 0
        reset()
        reportedCursorPos = editorInfo?.initialSelStart ?: -1
        reportedSelEnd = editorInfo?.initialSelEnd ?: -1
        val initialText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            editorInfo?.getInitialTextBeforeCursor(MAX_CACHE_LENGTH, 0)
        } else null
        if (initialText != null && reportedCursorPos >= 0) fill(initialText, reportedCursorPos)
        else scheduleRefresh()
    }

    fun onFinishInput() {
        cancelRefresh()
        reset()
        reportedCursorPos = -1
        reportedSelEnd = -1
    }

    /**
     * 应用上报光标变化，用于判断缓存是否已过期。
     */
    fun onUpdateSelection(newSelStart: Int, newSelEnd: Int) {
        reportedCursorPos = newSelStart
        reportedSelEnd = newSelEnd
        if (valid && !selectionActive && cachedCursorPos >= 0) {
            if (newSelStart == cachedCursorPos) {
                pendingCursorPositions.clear()
                if (pendingCallback == null) cancelRefresh() // 缓存与应用一致，无需校准
                return
            }
            // 连续编辑的光标回报可能滞后，只确认中间位置，不覆盖最新的本地镜像。
            val pendingIndex = pendingCursorPositions.indexOf(newSelStart)
            if (pendingIndex >= 0) {
                repeat(pendingIndex + 1) { pendingCursorPositions.removeFirst() }
                return
            }
        }
        // 不属于本地编辑的变化意味着缓存已过期，校准前不能继续拿它记录删除内容。
        invalidate()
    }

    /** 提交文本，预编辑内容会被一并替换 */
    fun onCommitText(text: CharSequence) {
        if (!valid || selectionActive) {
            invalidate()
            return
        }
        moveCursor(text.length - composingText.length)
        composingText = ""
        committedText.append(text)
        trim()
    }

    fun onSetComposingText(text: CharSequence) {
        if (!valid || selectionActive) {
            invalidate()
            return
        }
        moveCursor(text.length - composingText.length)
        composingText = text.toString()
    }

    fun onFinishComposingText() {
        if (!valid) return
        committedText.append(composingText)
        composingText = ""
        trim()
    }

    /**
     * 删除与记录使用同一段文本。返回 null 表示不能可靠记录，调用方可退回删除键。
     * InputConnection 的 true 仅代表请求被接受，不代表远端编辑器已执行完毕。
     */
    fun deleteLastCharBeforeCursor(): String? {
        if (!canRecordDeletion()) return null
        return deleteCachedText(peekLastCharBeforeCursor())
    }

    fun deleteTextBeforeCursor(length: Int): String? {
        if (length <= 0 || !canRecordDeletion()) return null
        return deleteCachedText(peekTextBeforeCursor(length))
    }

    private fun canRecordDeletion(): Boolean =
        valid && !selectionActive && composingText.isEmpty() && cachedCursorPos >= 0

    private fun deleteCachedText(text: String): String? {
        if (text.isEmpty()) {
            if (cachedCursorPos == 0) return ""
            invalidate()
            return null
        }
        val inputConnection = connection() ?: return null
        val accepted = try {
            inputConnection.deleteSurroundingText(text.length, 0)
        } catch (_: Exception) {
            false
        }
        if (!accepted) {
            invalidate()
            return null
        }
        onDeleteBeforeCursor(text.length)
        return text
    }

    /** 删除光标前 [length] 个字符 */
    fun onDeleteBeforeCursor(length: Int) {
        if (!valid || selectionActive || composingText.isNotEmpty()) {
            invalidate()
            return
        }
        if (length < 0 || length > committedText.length) {
            invalidate()
            return
        }
        committedText.setLength(committedText.length - length)
        moveCursor(-length)
    }

    /** 光标或内容被不可推算的操作改变，如方向键、选区编辑、粘贴等 */
    fun onEditorChanged() = invalidate()

    /**
     * 输入法自身用 setSelection 改变选区：文本内容未变，只同步镜像，
     * 避免把连续的光标移动当成内容变化而丢弃缓存。
     */
    fun onSelectionMovedByIme(newSelStart: Int, newSelEnd: Int) {
        reportedCursorPos = newSelStart
        reportedSelEnd = newSelEnd
        if (selectionActive || !valid || cachedCursorPos < 0) {
            if (selectionActive) invalidate()
            return
        }
        if (newSelStart == cachedCursorPos) {
            pendingCursorPositions.clear()
            return
        }
        moveCursor(newSelStart - cachedCursorPos)
    }

    /**
     * 读取缓存中光标前 [length] 个字符，不做跨进程调用，缓存不可用时返回空串。
     */
    fun peekTextBeforeCursor(length: Int): String {
        if (!valid || length <= 0) return ""
        val text = if (composingText.isEmpty()) committedText.toString() else committedText.toString() + composingText
        return if (text.length <= length) text else text.substring(text.length - length)
    }

    /**
     * 读取缓存中光标前一个字符，emoji 等代理对按整体返回。
     */
    fun peekLastCharBeforeCursor(): String {
        if (!canRecordDeletion()) return ""
        val text = peekTextBeforeCursor(2)
        if (text.isEmpty()) return ""
        return if (text == "\r\n" || (text.length == 2 && Character.isSurrogatePair(text[0], text[1]))) text
        else text.substring(text.length - 1)
    }

    /**
     * 请求光标前 [length] 个字符，缓存可用时同步回调，否则校准后回调，校准失败回调空串。
     */
    fun requestTextBeforeCursor(length: Int, callback: (String) -> Unit) {
        if (valid) {
            callback(peekTextBeforeCursor(length))
            return
        }
        if (slowResponseCount >= MAX_SLOW_RESPONSE_COUNT) {
            callback("")
            return
        }
        pendingLength = length
        pendingCallback = callback
        scheduleRefresh()
    }

    private fun scheduleRefresh() {
        if (refreshPending || slowResponseCount >= MAX_SLOW_RESPONSE_COUNT) return
        refreshPending = true
        handler.postDelayed(refreshTask, REFRESH_DELAY) // 延迟合并连续按键产生的校准请求
    }

    private fun cancelRefresh() {
        handler.removeCallbacks(refreshTask)
        refreshPending = false
        pendingCallback = null
        pendingLength = 0
    }

    private fun refresh() {
        refreshPending = false
        val callback = pendingCallback
        val callbackLength = pendingLength
        pendingCallback = null
        pendingLength = 0
        val text = fetchTextBeforeCursor()
        if (text != null) fill(text, reportedCursorPos) else reset()
        callback?.invoke(peekTextBeforeCursor(callbackLength))
    }

    private fun fetchTextBeforeCursor(): CharSequence? {
        val inputConnection = connection() ?: return null
        val startTime = SystemClock.uptimeMillis()
        val text = try {
            inputConnection.getTextBeforeCursor(MAX_CACHE_LENGTH, 0)
        } catch (_: Exception) {
            null
        }
        // 取文本为同步跨进程调用，对响应过慢的应用不再反复校准，避免主线程持续阻塞
        if (text == null || SystemClock.uptimeMillis() - startTime > SLOW_RESPONSE_TIMEOUT) slowResponseCount++
        else slowResponseCount = 0
        return text
    }

    private fun fill(text: CharSequence, cursorPos: Int) {
        val fetched = text.toString()
        // 取到的文本含预编辑内容，拆分后分开保存，避免预编辑被当成已提交文本
        val composing = composingText.takeIf { it.isNotEmpty() && fetched.endsWith(it) } ?: ""
        committedText.setLength(0)
        committedText.append(fetched, 0, fetched.length - composing.length)
        trim()
        composingText = composing
        cachedCursorPos = cursorPos
        pendingCursorPositions.clear()
        valid = true
    }

    private fun reset() {
        committedText.setLength(0)
        composingText = ""
        cachedCursorPos = -1
        pendingCursorPositions.clear()
        valid = false
    }

    private fun invalidate() {
        reset()
        scheduleRefresh()
    }

    private fun trim() {
        if (committedText.length > MAX_CACHE_LENGTH) {
            committedText.delete(0, committedText.length - MAX_CACHE_LENGTH)
        }
    }

    // 光标位置未知（应用未上报）时不做偏移，保持未知状态
    private fun moveCursor(offset: Int) {
        if (cachedCursorPos < 0 || offset == 0) return
        if (pendingCursorPositions.isEmpty()) pendingCursorPositions.addLast(cachedCursorPos)
        cachedCursorPos = (cachedCursorPos + offset).coerceAtLeast(0)
        pendingCursorPositions.addLast(cachedCursorPos)
        if (pendingCursorPositions.size > MAX_CACHE_LENGTH) pendingCursorPositions.removeFirst()
    }

    companion object {
        private const val MAX_CACHE_LENGTH = 1024
        private const val REFRESH_DELAY = 100L
        private const val SLOW_RESPONSE_TIMEOUT = 500L
        private const val MAX_SLOW_RESPONSE_COUNT = 3
    }
}
