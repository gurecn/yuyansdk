package com.yuyan.imemodule.service

import android.os.Build
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Proxy

/**
 * 删除键交给宿主删除、按光标回报差值记录删除内容的行为验证。
 * FakeHost 模拟微信输入框：删除键整体删除末尾的 "[xxx]" 表情文本，光标回报由用例手动下发。
 */
class EditorTextCacheKeyDeleteTest {
    private lateinit var host: FakeHost
    private lateinit var cache: EditorTextCache
    private val records = mutableListOf<String>()

    @Before
    fun setUp() {
        // 初始文本依赖 EditorInfo.setInitialSurroundingText（API 30+），避免用例自身触发同步取文本
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
    }

    @After
    fun tearDown() {
        if (::cache.isInitialized) main { cache.onFinishInput() }
    }

    @Test
    fun hundredDeletesWithoutSyncIpc() {
        val initial = (0 until 150).joinToString("") { ('a' + it % 26).toString() }
        start(initial)
        // 前 50 次逐次回报，后 50 次每 5 次集中回报，模拟回报滞后
        repeat(50) { press(); flushReports() }
        repeat(10) { repeat(5) { press() }; flushReports() }
        waitFor(EditorTextCache.KEY_DELETE_TIMEOUT + EditorTextCache.KEY_DELETE_IDLE + 300)
        assertEquals(0, host.getTextCalls)
        assertEquals(100, records.size)
        assertEquals(initial.substring(50), records.reversed().joinToString(""))
        assertEquals(initial.substring(0, 50), peek())
    }

    @Test
    fun wechatEmojiDeletedAtOnceAndRestored() {
        start("你好[抠鼻]")
        press()
        flushReports()
        assertEquals(listOf("[抠鼻]"), records)
        assertEquals("你好", host.text.toString())
        assertEquals("你好", peek())
        // 还原：原样提交记录内容，宿主再渲染为表情
        main {
            host.connection.commitText(records.last(), 1)
            cache.onCommitText(records.last())
        }
        flushReports()
        assertEquals("你好[抠鼻]", host.text.toString())
        assertEquals("你好[抠鼻]", peek())
        assertEquals(0, host.getTextCalls)
    }

    @Test
    fun mergedReportRecordedOnce() {
        start("abcdef")
        press()
        press()
        flushReports(mergeAll = true)
        assertEquals(listOf("ef"), records)
        waitFor(EditorTextCache.KEY_DELETE_TIMEOUT + 300)
        // 多余的等待项被丢弃，缓存仍与宿主一致
        assertEquals("abcd", peek())
        press()
        flushReports()
        assertEquals(listOf("ef", "d"), records)
        assertEquals(0, host.getTextCalls)
    }

    @Test
    fun lateReportInvalidatesWithoutDowngrade() {
        start("abc")
        press()
        waitFor(EditorTextCache.KEY_DELETE_TIMEOUT + 30)
        flushReports()
        waitFor(EditorTextCache.KEY_DELETE_IDLE + 500)
        assertTrue(records.isEmpty())
        assertFalse(main { cache.keyDeleteUnsupported })
        assertTrue(host.getTextCalls in 1..2)
        assertEquals("ab", peek())
    }

    @Test
    fun hostIgnoringDelKeyDowngrades() {
        host = FakeHost("abc", ignoreDel = true)
        start("abc")
        press()
        waitFor(EditorTextCache.KEY_DELETE_TIMEOUT + EditorTextCache.KEY_DELETE_IDLE + 300)
        assertTrue(main { cache.keyDeleteUnsupported })
        assertTrue(records.isEmpty())
        assertEquals(1, host.getTextCalls)
        assertEquals("abc", peek())
        // 降级只在本次输入会话内有效
        start("abc")
        assertFalse(main { cache.keyDeleteUnsupported })
    }

    @Test
    fun refreshDeferredWhileDeleting() {
        start("x".repeat(100))
        main { cache.onEditorChanged() } // 缓存失效，连删期间不做记录
        repeat(12) {
            press()
            flushReports()
            waitFor(50)
        }
        assertEquals(0, host.getTextCalls)
        waitFor(EditorTextCache.KEY_DELETE_IDLE + 300)
        assertEquals(1, host.getTextCalls)
        assertTrue(records.isEmpty())
        assertEquals(host.text.toString(), peek())
    }

    @Test
    fun queueOverflowAndUnexpectedReportsDoNotCrash() {
        start("y".repeat(200))
        repeat(EditorTextCache.MAX_PENDING_KEY_DELETES + 8) { press() }
        flushReports()
        assertEquals(EditorTextCache.MAX_PENDING_KEY_DELETES, records.size)
        // 宿主自行删除、光标前移等无法认领的回报只作废缓存
        main {
            host.text.setLength(host.text.length - 1)
            cache.onUpdateSelection(host.text.length, host.text.length)
            cache.onUpdateSelection(host.text.length + 5, host.text.length + 5)
            cache.onUpdateSelection(host.text.length, host.text.length)
        }
        waitFor(EditorTextCache.KEY_DELETE_TIMEOUT + EditorTextCache.KEY_DELETE_IDLE + 300)
        assertEquals(EditorTextCache.MAX_PENDING_KEY_DELETES, records.size)
        assertEquals(host.text.toString(), peek())
    }

    private fun start(text: String) {
        if (!::host.isInitialized) host = FakeHost(text)
        records.clear()
        main {
            if (!::cache.isInitialized) cache = EditorTextCache { host.connection }
            cache.onStartInput(EditorInfo().apply {
                initialSelStart = text.length
                initialSelEnd = text.length
                setInitialSurroundingText(text)
            })
        }
    }

    /** 与 ImeService.deleteBackwardByKey 一致：先登记，再发送删除键 */
    private fun press() = main {
        cache.beginKeyDelete { records += it }
        host.connection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
        host.connection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
    }

    /** 下发宿主积压的光标回报，[mergeAll] 为 true 时只回报最终位置 */
    private fun flushReports(mergeAll: Boolean = false) = main {
        val positions = if (mergeAll) listOfNotNull(host.reports.lastOrNull()) else host.reports.toList()
        host.reports.clear()
        positions.forEach { cache.onUpdateSelection(it, it) }
    }

    private fun peek(): String = main { cache.peekTextBeforeCursor(1024) }

    private fun waitFor(millis: Long) {
        Thread.sleep(millis)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    private fun <T> main(block: () -> T): T {
        var result: Result<T>? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync { result = runCatching(block) }
        return result!!.getOrThrow()
    }

    private class FakeHost(initial: String, private val ignoreDel: Boolean = false) {
        val text = StringBuilder(initial)
        val reports = ArrayDeque<Int>()
        var getTextCalls = 0

        val connection: InputConnection = Proxy.newProxyInstance(
            InputConnection::class.java.classLoader, arrayOf(InputConnection::class.java)
        ) { _, method, args ->
            when (method.name) {
                "getTextBeforeCursor" -> {
                    getTextCalls++
                    val length = args[0] as Int
                    text.substring((text.length - length).coerceAtLeast(0))
                }
                "sendKeyEvent" -> {
                    val event = args[0] as KeyEvent
                    if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_DEL && !ignoreDel) deleteUnit()
                    true
                }
                "commitText" -> {
                    text.append(args[0] as CharSequence)
                    reports.addLast(text.length)
                    true
                }
                "toString" -> "FakeHost"
                "hashCode" -> System.identityHashCode(this)
                "equals" -> false
                else -> when (method.returnType) {
                    java.lang.Boolean.TYPE -> false
                    Integer.TYPE -> 0
                    else -> null
                }
            }
        } as InputConnection

        // 微信输入框：表情文本时整体删除
        private fun deleteUnit() {
            if (text.isEmpty()) return
            val emoji = Regex("\\[[^\\[\\]]{1,8}]$").find(text)
            text.setLength(text.length - (emoji?.value?.length ?: 1))
            reports.addLast(text.length)
        }
    }
}
