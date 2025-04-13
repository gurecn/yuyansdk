package com.yuyan.inputmethod

import android.view.KeyEvent
import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.utils.StringUtils
import com.yuyan.inputmethod.core.CandidateListItem
import com.yuyan.inputmethod.core.Rime
import com.yuyan.inputmethod.util.DoublePinYinUtils
import com.yuyan.inputmethod.util.T9PinYinUtils
import com.yuyan.inputmethod.util.buildSpannedString
import java.util.Locale

object RimeEngine {
    private val keyRecordStack = KeyRecordStack()
    private var pinyins: Array<String> = emptyArray() // 候选词界面的候选拼音列表
    var showCandidates: List<CandidateListItem> = emptyList() // 所有待展示的候选词
    var showComposition: String = "" // 候选词上方展示的拼音
    var preCommitText: String = "" // 待提交的文字
    private var customPhraseSize: Int = 0 // 自定义引擎候选词长度
    fun init() {
        Rime.getInstance(false)
    }

    fun selectSchema(mod: String): Boolean {
        keyRecordStack.clear()
        val shareDir = CustomConstant.RIME_DICT_PATH
        val userDir = CustomConstant.RIME_DICT_PATH
        Rime.startupRime(shareDir, userDir, true)
        return Rime.selectSchema(mod)
    }

    /**
     * 是否输入完毕，等待上屏。
     */
    fun isFinish(): Boolean {
        return showCandidates.isEmpty() && showComposition.isBlank()
    }

    fun onNormalKey(event: KeyEvent) {
        val keyCode = event.keyCode
        val keyChar = when (keyCode) {
            KeyEvent.KEYCODE_APOSTROPHE -> if(isFinish()) '/'.code else '\''.code
            else -> event.unicodeChar
        }
        if (keyRecordStack.pushKey(keyCode))Rime.processKey(keyChar, event.action)
        updateCandidatesOrCommitText()
    }

    fun onDeleteKey() {
        processDelAction()
        updateCandidatesOrCommitText()
    }

    fun selectCandidate(index: Int): String? {
        val indexReal = index - customPhraseSize
        Rime.selectCandidate(indexReal)
        keyRecordStack.pushCandidateSelectAction()
        return updateCandidatesOrCommitText()
    }

    fun getNextPageCandidates(): Array<CandidateListItem> {
        return if (Rime.hasRight()) {
            Rime.processKey(getRimeKeycodeByName("Page_Down"), 0)
           val candidates = Rime.getRimeContext()!!.candidates
            if (InputModeSwitcherManager.isEnglishUpperCase) {
                for (item in candidates) {
                    item.text = item.text.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                }
            } else if (InputModeSwitcherManager.isEnglishUpperLockCase) {
                for (item in candidates) {
                    item.text = item.text.uppercase()
                }
            } else {
                for (item in candidates) {
                    item.text = item.text.lowercase()
                }
            }
            candidates
        } else emptyArray()
    }

    fun selectPinyin(index: Int) {
        val pinyinKey = keyRecordStack.pushPinyinSelectAction(pinyins[index]) ?: return
        Rime.replaceKey(pinyinKey.posInInput, pinyinKey.pinyinLength, pinyinKey.inputKeys())
        updateCandidatesOrCommitText()
    }

    fun predictAssociationWords(text: String) {
        pinyins = emptyArray()
        if (text.isNotEmpty()) {
            val words = CustomEngine.predictAssociationWordsChinese(text).plus(Rime.getAssociateList(text))
            showCandidates = words.filterNotNull().map {
                CandidateListItem("", it)
            }
            showComposition = ""
        }
    }

    fun selectAssociation(index: Int) {
        val indexReal = index - customPhraseSize
        Rime.chooseAssociate(indexReal)
        updateCandidatesOrCommitText()
        preCommitText = showCandidates.getOrNull(indexReal)?.text?:""
    }

    fun reset() {
        showCandidates = emptyList()
        pinyins = emptyArray()
        showComposition = ""
        preCommitText = ""
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
            preCommitText = if (InputModeSwitcherManager.isEnglishUpperCase) {
                preCommitText.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            } else if (InputModeSwitcherManager.isEnglishUpperLockCase) {
                preCommitText.uppercase()
            } else {
                preCommitText.lowercase()
            }
            showComposition = ""
            showCandidates = emptyList()
            return preCommitText
        }
        val candidates = Rime.getRimeContext()?.candidates?.asList() ?: emptyList()
        customPhraseSize = 0
        val compositionText = Rime.compositionText
        showCandidates = when {
            compositionText.isNotBlank() -> {
                val phrase = CustomEngine.processPhrase(compositionText.replace("\'", ""))
                if(InputModeSwitcherManager.isEnglish && StringUtils.isLetter(compositionText) &&
                    !compositionText.equals(candidates.first().text, ignoreCase = true) ){
                    phrase.add(0, compositionText)
                }
                customPhraseSize = phrase.size
                phrase.map { content -> CandidateListItem("📋", content) }.toMutableList().plus(candidates)
            }
            else -> candidates
        }
        var composition = getCurrentComposition(candidates)
        if (InputModeSwitcherManager.isEnglishUpperCase) {
            for (item in showCandidates) item.text = item.text.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            composition = composition.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        } else if (InputModeSwitcherManager.isEnglishUpperLockCase) {
            for (item in showCandidates) item.text = item.text.uppercase()
            composition = composition.uppercase()
        } else {
            for (item in showCandidates) item.text = item.text.lowercase()
            composition = composition.lowercase()
        }
        var count = compositionText.count { it in '1'..'9' }
        pinyins =
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

    private fun getCurrentComposition(candidates: List<CandidateListItem>): String {
        val composition = Rime.compositionText
        if(Rime.getCurrentRimeSchema() == CustomConstant.SCHEMA_EN) return ""
        if(composition.isEmpty()) return ""
        if(candidates.isEmpty()) return composition
        val comment = candidates.first().comment
        return when {
            comment.isBlank() || comment.contains("☯") || comment.startsWith("~")-> composition
            Rime.getCurrentRimeSchema().startsWith(CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY) ->  {
                if(!AppPrefs.getInstance().keyboardSetting.keyboardDoubleInputKey.getValue()) composition
                else {
                    val compositionList = composition.filter { it.code <= 0xFF }.split("'".toRegex())
                    buildSpannedString {
                        append(composition.filter { it.code > 0xFF })
                        comment.split("'").zip(compositionList).forEach { (pinyin, composition) ->
                            append(if (composition.length >= 2) pinyin else {
                                 when(Rime.getCurrentRimeSchema()){
                                    "double_pinyin_abc" ->DoublePinYinUtils.double_pinyin_abc
                                    "double_pinyin_ziguang" ->DoublePinYinUtils.double_pinyin_ziguang
                                    "double_pinyin_ls17" ->DoublePinYinUtils.double_pinyin_ls17
                                    else ->DoublePinYinUtils.double_pinyin
                                }.getOrElse(composition[0]){pinyin.first().toString()}
                            })
                            append("'")
                        }
                        if (!composition.endsWith("'")) delete(length - 1, length)
                    }
                }
            }
            else -> {
                val compositionList = composition.filter { it.code <= 0xFF }.split("'".toRegex())
                var pos = 0
                buildSpannedString {
                    append(composition.filter { it.code > 0xFF })
                    comment.split("'").zip(compositionList).forEach { (pinyin, composition) ->
                        if (composition.length >= pinyin.length) {
                            pos += pinyin.length
                            append(pinyin)
                        }
                        else {
                            val temp = pinyin.substring(0, composition.length)
                            pos += composition.length
                            append(temp)
                        }
                        append("'")
                        pos += 1
                    }
                    if (!composition.endsWith("'")) {
                        pos -= 1
                        delete(length - 1, length)
                    }
                    if (pos < composition.length) {
                        append("'")
                        append(composition.substring(pos, composition.length))
                    }
                }
            }
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

        fun pop(): InputKey? = keyRecords.removeLastOrNull()

        fun clear() = keyRecords.clear()

        fun getkeyRecords():ArrayList<InputKey> = keyRecords

        inline fun forEachReversed(action: (InputKey) -> Unit) {
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

    private interface InputKey {
        class Apostrophe(val dummy: Boolean = false) : InputKey

        object DefaultAction : InputKey

        object SelectPinyinAction : InputKey

        class T9Key(val keyChar: String, var consumed: Boolean = false) : InputKey {
            constructor(keyCode: Int) : this(String(intArrayOf(keyCode - KeyEvent.KEYCODE_0 + '0'.code), 0, 1))

            override fun toString(): String = keyChar
        }

        class QwertKey(val keyChar: String) : InputKey {
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
}