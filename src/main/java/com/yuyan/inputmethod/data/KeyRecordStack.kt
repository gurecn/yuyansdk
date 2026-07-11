package com.yuyan.inputmethod.data

import android.view.KeyEvent
import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.inputmethod.core.Rime
import com.yuyan.inputmethod.util.LX17PinYinUtils
import com.yuyan.inputmethod.util.T9PinYinUtils
import java.util.LinkedList

class KeyRecordStack {
    private val keyRecords = ArrayList<InputKey>(20)

    fun pop(): InputKey? = keyRecords.removeLastOrNull()

    fun clear() = keyRecords.clear()

    fun isEmpty() = keyRecords.isEmpty()

    fun forEachReversed(action: (InputKey) -> Unit) {
        for (i in keyRecords.indices.reversed()) {
            action(keyRecords[i])
        }
    }

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


    fun processDelAction() {
        when (val lastKey = pop()) {
            is InputKey.PinyinKey -> {
                val pinyinKey = restorePinyinToT9Key(lastKey) ?: return
                replacePinyinWithT9Keys(pinyinKey)
            }
            InputKey.SelectPinyinAction -> {
                val pinyinKey = restorePinyinToT9Key() ?: return
                replacePinyinWithT9Keys(pinyinKey)
            }
            is InputKey.Apostrophe -> {
                if (!lastKey.dummy) {
                    Rime.processKey(getRimeKeycodeByName("BackSpace"), 0)
                }
            }
            else -> {
                Rime.processKey(getRimeKeycodeByName("BackSpace"), 0)
            }
        }
    }

    private fun replacePinyinWithT9Keys(pinyinKey: InputKey.PinyinKey) {
        if (!Rime.replaceKey(pinyinKey.posInInput, pinyinKey.inputKeyLength, pinyinKey.t9Keys())) {
            Rime.replaceKey(pinyinKey.posInInput, pinyinKey.pinyinLength, pinyinKey.t9Keys())
        }
    }

    /**
     * 获取Rime定义键值
     */
    fun getRimeKeycodeByName(name: String) : Int {
        return Rime.getRimeKeycodeByName(name)
    }

    fun pushPinyinSelectAction(pinyin: String?): InputKey.PinyinKey? {
        pinyin ?: return null
        val keys = LinkedList<InputKey.T9Key>()
        val rimeSchema = Rime.getCurrentRimeSchema()
        when (rimeSchema) {
            CustomConstant.SCHEMA_ZH_T9 -> {
                T9PinYinUtils.pinyin2Key(pinyin).forEach {
                    keys.add(InputKey.T9Key(it))
                }
            }
            CustomConstant.SCHEMA_ZH_DOUBLE_LX17 -> {
                LX17PinYinUtils.pinyin2Key(pinyin).forEach {
                    keys.add(InputKey.T9Key(it))
                }
            }
        }
        val index = (0..keyRecords.size - keys.size).indexOfFirst { start ->
            keys.indices.all { j ->
                val record = keyRecords[start + j]
                record.toString() == keys[j].toString() && record is InputKey.T9Key && !record.consumed
            }
        }
        repeat(keys.size) {
            keyRecords.removeAt(index)
        }
        var posInInput = 0
        keyRecords.forEach {
            if(it is InputKey.SelectPinyinAction) posInInput += 1
        }
        keyRecords.add(InputKey.SelectPinyinAction)
        keyRecords.add(index, InputKey.PinyinKey(pinyin))
        posInInput = keyRecords.subList(0, index).fold(0) { acc, inputKey ->
            acc + when (inputKey) {
                is InputKey.T9Key, is InputKey.Apostrophe -> 1
                is InputKey.PinyinKey -> inputKey.inputKeyLength
                else -> 0
            }
        }
        keyRecords[index] = (keyRecords[index] as InputKey.PinyinKey).copy(posInInput)
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

        fun restoreToT9key(): List<T9Key> {
            val keys = LinkedList<T9Key>()
            val rimeSchema = Rime.getCurrentRimeSchema()
            when (rimeSchema) {
                CustomConstant.SCHEMA_ZH_T9 -> {
                    T9PinYinUtils.pinyin2Key(pinyin).forEach {
                        keys.add(T9Key(it))
                    }
                }
                CustomConstant.SCHEMA_ZH_DOUBLE_LX17 -> {
                    LX17PinYinUtils.pinyin2Key(pinyin).forEach {
                        keys.add(T9Key(it))
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