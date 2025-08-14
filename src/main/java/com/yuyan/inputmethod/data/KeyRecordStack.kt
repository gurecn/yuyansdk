package com.yuyan.inputmethod.data

import android.view.KeyEvent
import com.yuyan.inputmethod.RimeEngine.processDelAction
import com.yuyan.inputmethod.util.T9PinYinUtils

class KeyRecordStack {
    private val keyRecords = ArrayList<InputKey>(20)

    fun pop(): InputKey? = keyRecords.removeLastOrNull()

    fun clear() = keyRecords.clear()

    fun forEachReversed(action: (InputKey) -> Unit) {
        for (i in keyRecords.indices.reversed()) {
            action(keyRecords[i])
        }
    }

    fun pushKey(keyCode: Int): Boolean {
        val lastKey = keyRecords.lastOrNull()
        if (lastKey is InputKey.Apostrophe && keyRecords.size == 1) {
            processDelAction()
        }else if (keyCode == KeyEvent.KEYCODE_APOSTROPHE) {
            // 连续分词没有意义
            if (lastKey is InputKey.Apostrophe) return false
            // 选择拼音之后分词没有意义，但是需要把分词操作入栈
            if (lastKey == InputKey.SelectPinyinAction) {
                keyRecords.add(InputKey.Apostrophe(true))
                return false
            }
        }
        // 选择拼音只是记录其是不是最后一个操作，如果不是在选择之后立即删除，则不需记录
        if (lastKey == InputKey.SelectPinyinAction) {
            keyRecords.removeLastOrNull()
        }
        when (keyCode) {
            KeyEvent.KEYCODE_APOSTROPHE -> {
                keyRecords.add(InputKey.Apostrophe())
            }
            in KeyEvent.KEYCODE_1..KeyEvent.KEYCODE_9 -> {
                keyRecords.add(InputKey.T9Key(keyCode))
            }
            in KeyEvent.KEYCODE_A..KeyEvent.KEYCODE_Z -> {
                keyRecords.add(InputKey.QwertKey(keyCode))
            }
            else -> {
                keyRecords.add(InputKey.DefaultAction)
            }
        }
        return true
    }

    fun pushPinyinSelectAction(pinyin: String?): InputKey.PinyinKey? {
        pinyin ?: return null
        // 第一个T9Key替换为PinyinKey，并把其后 pinyin.length-1 个T9Key删除
        val records = ArrayList<InputKey?>(keyRecords.size)
        var firstT9KeyPos = -1
        var accLen = 0
        keyRecords.mapIndexedTo(records) { i, inputKey ->
            if (firstT9KeyPos < 0 && inputKey is InputKey.T9Key && !inputKey.consumed) {
                firstT9KeyPos = i
            }
            when {
                firstT9KeyPos < 0 -> inputKey
                firstT9KeyPos == i -> {
                    accLen = 1
                    InputKey.PinyinKey(pinyin)
                }
                accLen > 0 && accLen < pinyin.length -> {
                    if (inputKey is InputKey.T9Key) {
                        ++accLen
                        null
                    } else {
                        inputKey
                    }
                }
                inputKey == InputKey.SelectPinyinAction -> null
                else -> inputKey
            }
        }
        keyRecords.clear()
        keyRecords.addAll(records.filterNotNull())
        keyRecords.add(InputKey.SelectPinyinAction)
        val index = keyRecords.indexOfLast { it is InputKey.PinyinKey }
        if (index >= 0) {
            val posInInput = keyRecords.subList(0, index).fold(0) { acc, inputKey ->
                acc + when (inputKey) {
                    is InputKey.T9Key -> 1
                    is InputKey.PinyinKey -> inputKey.inputKeyLength
                    else -> 0
                }
            }
            keyRecords[index] = (keyRecords[index] as InputKey.PinyinKey).copy(posInInput)
        }
        return keyRecords.getOrNull(index) as? InputKey.PinyinKey
    }

    fun pushCandidateSelectAction() {
        if (keyRecords.lastOrNull() == InputKey.SelectPinyinAction) {
            keyRecords.removeLastOrNull()
        }
        keyRecords.add(InputKey.DefaultAction)
    }

    fun restorePinyinToT9Key(pinyinKey: InputKey.PinyinKey? = null): InputKey.PinyinKey? {
        if (pinyinKey != null) {
            keyRecords.add(pinyinKey)
        }
        val index = keyRecords.indexOfLast { it is InputKey.PinyinKey }
        val inputKey = keyRecords.getOrNull(index) as? InputKey.PinyinKey
        if (index >= 0) {
            keyRecords.replaceAt(index, inputKey!!.restoreToT9key())
        }
        return inputKey
    }

    private fun <T> ArrayList<T>.replaceAt(index: Int, elements: List<T>) {
        if (index == lastIndex) {
            removeAt(index)
            addAll(elements)
        } else {
            val heads = take(index)
            val tails = takeLast(size - index - 1)
            clear()
            addAll(heads)
            addAll(elements)
            addAll(tails)
        }
    }
}

interface InputKey {
    class Apostrophe(val dummy: Boolean = false) : InputKey

    object DefaultAction : InputKey

    object SelectPinyinAction : InputKey
    class T9Key(private val keyChar: String, var consumed: Boolean = false) : InputKey {
        constructor(keyCode: Int) : this(String(intArrayOf(keyCode - KeyEvent.KEYCODE_0 + '0'.code), 0, 1))

        override fun toString(): String = keyChar
    }

    class QwertKey(private val keyChar: String) : InputKey {
        constructor(keyCode: Int) : this(String(intArrayOf(keyCode - KeyEvent.KEYCODE_A + 'a'.code), 0, 1))

        override fun toString(): String = keyChar
    }

    class PinyinKey(private val pinyin: String, val posInInput: Int = 0) : InputKey {
        private var t9InputKeys: String? = null

        val pinyinLength: Int = pinyin.length
        val inputKeyLength: Int = pinyinLength + 1

        fun t9Keys() = t9InputKeys ?: restoreToT9key().joinToString("")

        fun restoreToT9key(): List<T9Key> =
            pinyin.map { T9Key(T9PinYinUtils.pinyin2T9Key(it).toString()) }.also {
                t9InputKeys = it.joinToString("")
            }

        fun copy(posInInput: Int) = PinyinKey(pinyin, posInInput)

        fun inputKeys() = "${pinyin.lowercase()}'"
    }
}