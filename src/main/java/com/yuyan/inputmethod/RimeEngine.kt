package com.yuyan.inputmethod

import android.view.KeyEvent
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.inputmethod.core.CandidateListItem
import com.yuyan.inputmethod.core.Rime
import com.yuyan.inputmethod.util.T9PinYinUtils
import com.yuyan.inputmethod.util.buildSpannedString
import com.yuyan.inputmethod.util.isDigitsOnly
import com.yuyan.inputmethod.util.underline
import java.util.Locale

object RimeEngine {
    private const val PINYIN_T9_0 = 41
    private const val PINYIN_T9_1 = 8
    private const val PINYIN_T9_9 = 16

    private val keyRecordStack = KeyRecordStack()
    private var mInputModeSwitcher : InputModeSwitcherManager? = null  // 键盘模式
    private var pinyinCandidates: Array<String> = emptyArray() // 候选词界面的候选拼音列表
    var showCandidates: Array<CandidateListItem> = emptyArray() // 所有待展示的候选词
    var showComposition: String = "" // 候选词上方展示的拼音
    var preCommitText: String = "" // 待提交的文字
    fun init() {
        Rime.getInstance(false)
    }

    fun selectSchema(mod: String, inputMode:InputModeSwitcherManager? = null): Boolean {
        mInputModeSwitcher= inputMode
        if(mod != Rime.getCurrentRimeSchema()) {
            keyRecordStack.clear()
            val shareDir = CustomConstant.RIME_DICT_PATH
            val userDir = CustomConstant.RIME_DICT_PATH
            Rime.startupRime(shareDir, userDir, true)
            return Rime.selectSchema(mod)
        }
        return true
    }

    /**
     * 是否输入完毕，等待上屏。
     */
    fun isFinish(): Boolean {
        return preCommitText.isEmpty()
    }

    fun onNormalKey(keyCode: Int) {
        val keyChar = when (keyCode) {
            in KeyEvent.KEYCODE_A..KeyEvent.KEYCODE_Z -> {
                keyCode - KeyEvent.KEYCODE_A + 'a'.code
            }
            in (KeyEvent.KEYCODE_A or KeyEvent.META_SHIFT_RIGHT_ON)..(KeyEvent.KEYCODE_Z or KeyEvent.META_SHIFT_RIGHT_ON) -> {
                keyCode - (KeyEvent.KEYCODE_A or KeyEvent.META_SHIFT_RIGHT_ON) + 'A'.code
            }
            KeyEvent.KEYCODE_APOSTROPHE -> '\''.code
            in PINYIN_T9_1..PINYIN_T9_9 -> keyCode + PINYIN_T9_0
            else -> keyCode
        }
        if (keyRecordStack.pushKey(keyCode)) {
            Rime.processKey(keyChar, 0)
        }
        updateCandidatesOrCommitText()
    }

    fun onDeleteKey() {
        processDelAction()
        updateCandidatesOrCommitText()
    }

    fun selectCandidate(index: Int): String? {
        Rime.selectCandidate(index)
        keyRecordStack.pushCandidateSelectAction()
        return updateCandidatesOrCommitText()
    }

    fun selectCandidateAndForceCommit(index: Int) {
        Rime.selectCandidate(index)
        val rimeCommit = Rime.getRimeCommit()
        val text = rimeCommit?.commitText?: Rime.compositionText
        reset()
        preCommitText = text
    }

    fun getNextPageCandidates(): List<CandidateListItem> {
        val candidates = mutableListOf<CandidateListItem>()
        for(i in 0..4){
            if (Rime.hasRight()) {
                Rime.processKey(getRimeKeycodeByName("Page_Down"), 0)
                val caned = Rime.getRimeContext()!!.candidates
                candidates.addAll(caned.asList())
            } else {
                break
            }
        }
        return candidates
    }

    fun selectPinyin(index: Int) {
        val pinyinKey = keyRecordStack.pushPinyinSelectAction(pinyinCandidates[index]) ?: return
        Rime.replaceKey(pinyinKey.posInInput, pinyinKey.pinyinLength, pinyinKey.inputKeys())
        updateCandidatesOrCommitText()
    }

    fun reset() {
        showCandidates = emptyArray()
        pinyinCandidates = emptyArray()
        showComposition = ""
        keyRecordStack.clear()
        Rime.clearComposition()
    }

    fun destroy() = Rime.destroy()

    private fun processDelAction() {
        when (val lastKey = keyRecordStack.pop()) {
            is InputKey.PinyinKey -> {
                val pinyinKey = keyRecordStack.restorePinyinToT9Key(lastKey) ?: return
                replacePinyinWithT9Keys(pinyinKey)
            }
            InputKey.SelectPinyinAction -> {
                val pinyinKey = keyRecordStack.restorePinyinToT9Key() ?: return
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
        /**
         * 当前输入状态是“你h”时，引擎默认删除行为是“ni”（删除h并且删除“你”的选中状态）
         * 可能存在引擎操作栈与记录的操作栈不一样的问题
         * 临时方案，尝试不同长度的替换，至少保证可以把拼音回退成9键
         */
        if (!Rime.replaceKey(pinyinKey.posInInput, pinyinKey.inputKeyLength, pinyinKey.t9Keys())) {
            Rime.replaceKey(pinyinKey.posInInput, pinyinKey.pinyinLength, pinyinKey.t9Keys())
        }
    }

    private fun updateCandidatesOrCommitText(): String? {
        val rimeCommit = Rime.getRimeCommit()
        if (rimeCommit != null) {
            keyRecordStack.clear()
            preCommitText = rimeCommit.commitText
            if(mInputModeSwitcher != null) {
                if (mInputModeSwitcher!!.isEnglishUpperCase) {
                    preCommitText = preCommitText.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                } else if (mInputModeSwitcher!!.isEnglishUpperLockCase) {
                    preCommitText = preCommitText.uppercase()
                }
            }
            return preCommitText
        }
        val candidates = Rime.getRimeContext()?.candidates ?: emptyArray()
        var composition = getCurrentComposition(candidates)
        if(mInputModeSwitcher != null) {
            if (mInputModeSwitcher!!.isEnglishUpperCase) {
                for (item in candidates) {
                    item.text = item.text.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                }
                composition = composition.toString().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            } else if (mInputModeSwitcher!!.isEnglishUpperLockCase) {
                for (item in candidates) {
                    item.text = item.text.uppercase()
                }
                composition = composition.toString().uppercase()
            }
        }
        var count = Rime.compositionText.count { it in '1'..'9' }
        val pyCandidates =
            if (count > 0) {
                val remainT9Keys = ArrayList<InputKey>(count)
                keyRecordStack.forEachReversed { inputKey ->
                    if (inputKey is InputKey.T9Key) {
                        inputKey.consumed = count-- <= 0
                        if (!inputKey.consumed) {
                            remainT9Keys.add(inputKey)
                        }
                    }
                }
                val t9Input = remainT9Keys.joinToString("").reversed()
                T9PinYinUtils.t9KeyToPinyin(t9Input)
            } else {
                emptyArray()
            }
        pinyinCandidates = pyCandidates
        showCandidates = candidates
        showComposition = composition
        preCommitText = ""
        return null
    }

    /**
     * 拿到候选词拼音组合
     */
    fun getPrefixs(): Array<String> {
        var count = Rime.compositionText.count { it in '1'..'9' }
        val pyCandidates =
            if (count > 0) {
                val remainT9Keys = ArrayList<InputKey>(count)
                keyRecordStack.forEachReversed { inputKey ->
                    if (inputKey is InputKey.T9Key) {
                        inputKey.consumed = count-- <= 0
                        if (!inputKey.consumed) {
                            remainT9Keys.add(inputKey)
                        }
                    }
                }
                val t9Input = remainT9Keys.joinToString("").reversed()
                T9PinYinUtils.t9KeyToPinyin(t9Input)
            } else {
                emptyArray()
            }
        return pyCandidates
    }

    private fun getCurrentComposition(candidates: Array<CandidateListItem>): String {
        val compositionText = Rime.compositionText
        return when {
            candidates.isEmpty() -> compositionText
            compositionText.isEmpty() -> compositionText
            Rime.getCurrentRimeSchema() == CustomConstant.SCHEMA_ZH_T9  -> {
                val compositionList: List<String> =compositionText.filter { it.code <= 0xFF }.split("[ ']".toRegex())
                val pinyinList: List<String> = candidates.first().comment.split(" ")
                buildSpannedString {
                    append(compositionText.filter { it.code > 0xFF })
                    pinyinList.zip(compositionList).forEach { (pinyin, composition) ->
                        val py = if (composition.length >= pinyin.length) {
                            pinyin
                        } else {
                            pinyin.substring(0, composition.length)
                        }
                        if (composition.isDigitsOnly()) {
                            append(py)
                        } else {
                            underline {
                                append(py)
                            }
                        }
                        append("'")
                    }
                    // 不以分词结束的则去掉拼音末尾的分词符号
                    if (keyRecordStack.getTop() !is InputKey.Apostrophe && isNotEmpty()) {
                        delete(length - 1, length)
                    }
                }
            }
            else -> {compositionText.replace(" ", "'")}
        }
    }

    /**
     * 设置输入法搜索参数
     */
    fun setImeOption(option: String, value: Boolean) {
        Rime.setOption(option, value)
    }

    /**
     * 获取Rime定义键值
     */
    private fun getRimeKeycodeByName(name: String) : Int {
        return Rime.getRimeKeycodeByName(name)
    }

    private class KeyRecordStack {
        private val keyRecords = ArrayList<InputKey>(20)

        fun getTop(): InputKey? = keyRecords.lastOrNull()

        fun pop(): InputKey? = keyRecords.removeLastOrNull()

        fun clear() = keyRecords.clear()

        inline fun forEachReversed(action: (InputKey) -> Unit) {
            for (i in keyRecords.indices.reversed()) {
                action(keyRecords[i])
            }
        }

        fun pushKey(keyCode: Int): Boolean {
            val lastKey = keyRecords.lastOrNull()
            if (keyCode == KeyEvent.KEYCODE_APOSTROPHE) {
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
                keyRecords.removeLast()
            }
            when (keyCode) {
                KeyEvent.KEYCODE_APOSTROPHE -> {
                    keyRecords.add(InputKey.Apostrophe())
                }
                in PINYIN_T9_1..PINYIN_T9_9 -> {
                    keyRecords.add(InputKey.T9Key(keyCode))
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
                keyRecords.removeLast()
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

    private interface InputKey {
        class Apostrophe(val dummy: Boolean = false) : InputKey

        object DefaultAction : InputKey

        object SelectPinyinAction : InputKey

        class T9Key(val keyChar: String, var consumed: Boolean = false) : InputKey {
            constructor(keyCode: Int) : this(String(intArrayOf(keyCode + PINYIN_T9_0), 0, 1))

            override fun toString(): String = keyChar
        }

        class PinyinKey(private val pinyin: String, val posInInput: Int = 0) : InputKey {
            private var t9InputKeys: String? = null

            val pinyinLength: Int = pinyin.length
            val inputKeyLength: Int = pinyinLength + 1

            fun t9Keys() = t9InputKeys ?: restoreToT9key().joinToString("")

            fun restoreToT9key(): List<T9Key> =
                pinyin.map { T9Key(T9PinYinUtils.pinyin2T9Key(it)) }.also {
                    t9InputKeys = it.joinToString("")
                }

            fun copy(posInInput: Int) = PinyinKey(pinyin, posInInput)

            @OptIn(ExperimentalStdlibApi::class)
            fun inputKeys() = "${pinyin.lowercase()}'"
        }
    }
}