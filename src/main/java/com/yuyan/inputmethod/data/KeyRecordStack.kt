package com.yuyan.inputmethod.data

import android.view.KeyEvent
import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.inputmethod.RimeEngine.processDelAction
import com.yuyan.inputmethod.core.Rime
import com.yuyan.inputmethod.data.InputKey.QwertKey
import com.yuyan.inputmethod.util.LX17PinYinUtils
import com.yuyan.inputmethod.util.T9PinYinUtils
import java.util.LinkedList

class KeyRecordStack {
    private val keyRecords = ArrayList<InputKey>(20)

    fun pop(): InputKey? = keyRecords.removeLastOrNull()

    fun clear() = keyRecords.clear()

    fun pushKey(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        val keyChar = event.unicodeChar
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
            in KeyEvent.KEYCODE_A..KeyEvent.KEYCODE_Z -> {
                if('A'.code <= keyChar && 'Z'.code >= keyChar){
                    keyRecords.add(InputKey.T9Key(keyChar))
                } else {
                    keyRecords.add(InputKey.QwertKey(keyChar))
                }
            } else -> {
                keyRecords.add(InputKey.DefaultAction)
            }
        }
        return true
    }

    fun pushPinyinSelectAction(pinyin: String?): InputKey.PinyinKey? {
        pinyin ?: return null
        val keys = LinkedList<QwertKey>()
        val rimeSchema = Rime.getCurrentRimeSchema()
        when (rimeSchema) {
            CustomConstant.SCHEMA_ZH_T9 -> {
                T9PinYinUtils.pinyin2Key(pinyin).forEach {
                    keys.add(QwertKey(it))
                }
            }
            CustomConstant.SCHEMA_ZH_DOUBLE_LX17 -> {
                LX17PinYinUtils.pinyin2Key(pinyin).forEach {
                    keys.add(QwertKey(it))
                }
            }
        }
        val index1 = (0..keyRecords.size - keys.size).indexOfFirst { start ->
            keys.indices.all { j -> keyRecords[start + j].toString() == keys[j].toString() }
        }
        repeat(keys.size) {
            keyRecords.removeAt(index1)
        }
        keyRecords.add(index1, InputKey.PinyinKey(pinyin))
        val posInInput = keyRecords.subList(0, index1).fold(0) { acc, inputKey ->
            acc + when (inputKey) {
                is InputKey.T9Key -> 1
                is InputKey.PinyinKey -> inputKey.inputKeyLength
                else -> 0
            }
        }
        keyRecords[index1] = (keyRecords[index1] as InputKey.PinyinKey).copy(posInInput)
        return keyRecords.getOrNull(index1) as? InputKey.PinyinKey
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
    class T9Key(private val keyChar: Char, var consumed: Boolean = false) : InputKey {
        constructor(keyCode: Int) : this(keyCode.toChar())

        override fun toString(): String = keyChar.toString()
    }

    class QwertKey(private val keyChar: Char) : InputKey {
        constructor(keyCode: Int) : this(keyCode.toChar())

        override fun toString(): String = keyChar.toString()
    }

    class PinyinKey(private val pinyin: String, val posInInput: Int = 0) : InputKey {
        val pinyinLength: Int = pinyin.length
        val inputKeyLength: Int = pinyinLength + 1
        fun t9Keys(): String {
            val rimeSchema = Rime.getCurrentRimeSchema()
            return when (rimeSchema) {
                CustomConstant.SCHEMA_ZH_T9 -> {
                    T9PinYinUtils.pinyin2Key(pinyin)
                }
                CustomConstant.SCHEMA_ZH_DOUBLE_LX17 -> {
                    LX17PinYinUtils.pinyin2Key(pinyin)
                }
                else -> ""
            }
        }

        fun restoreToT9key(): List<QwertKey> {
            val keys = LinkedList<QwertKey>()
            val rimeSchema = Rime.getCurrentRimeSchema()
            when (rimeSchema) {
                CustomConstant.SCHEMA_ZH_T9 -> {
                    T9PinYinUtils.pinyin2Key(pinyin).forEach {
                        keys.add(QwertKey(it))
                    }
                }
                CustomConstant.SCHEMA_ZH_DOUBLE_LX17 -> {
                    LX17PinYinUtils.pinyin2Key(pinyin).forEach {
                        keys.add(QwertKey(it))
                    }
                }
                else -> pinyin
            }

            return keys
        }

        fun copy(posInInput: Int) = PinyinKey(pinyin, posInInput)

        fun pinyin() = "${pinyin.lowercase()}'"
    }
}