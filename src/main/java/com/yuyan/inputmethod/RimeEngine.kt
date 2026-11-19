package com.yuyan.inputmethod

import android.view.KeyEvent
import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.imemodule.application.Launcher
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.inputmethod.core.CandidateListItem
import com.yuyan.inputmethod.core.Rime
import com.yuyan.inputmethod.util.DoublePinYinUtils
import com.yuyan.inputmethod.util.QwertyPinYinUtils
import com.yuyan.inputmethod.util.T9PinYinUtils
import java.util.Locale

object RimeEngine {
    private const val YUYAN_CANDIDATE_PREFIX = "yuyan:"
    const val EXPRESSION_INITIAL_WINDOW = 64
    const val EXPRESSION_MAX_WINDOW = 4096
    private var pinyins: Array<String> = emptyArray() // 候选词界面的候选拼音列表
    var showCandidates: List<CandidateListItem> = emptyList() // 所有待展示的候选词
    var showComposition: String = "" // 候选词上方展示的拼音
    var preCommitText: String = "" // 待提交的文字
    const val MASK_CASE_LOWER = 0
    private var charCase = 0x0000

    /** 引擎公式扫描结果 */
    sealed interface ExpressionResponse {
        data object None : ExpressionResponse
        data object NeedMore : ExpressionResponse
        class Candidates(val values: List<String>) : ExpressionResponse
    }

    fun init() {
        Rime.getInstance(false)
    }

    fun selectSchema(mod: String): Boolean {
        charCase = MASK_CASE_LOWER
        Rime.startup(Launcher.instance.context, false)
        return Rime.selectSchema(mod)
    }

    fun getCurrentRimeSchema(): String {
        return Rime.getCurrentRimeSchema()
    }

    /**
     * 是否输入完毕
     */
    fun isFinish(): Boolean {
        return !Rime.isComposing
    }

    fun onNormalKey(event: KeyEvent) {
        val keyCode = event.keyCode
        val keyChar = if(keyCode == KeyEvent.KEYCODE_APOSTROPHE) if(isFinish()) '/'.code else '\''.code
            else event.unicodeChar
        Rime.processKey(keyChar, event.action)
        updateCandidatesOrCommitText()
    }

    fun onDeleteKey() {
        Rime.processKey(Rime.getRimeKeycodeByName("BackSpace"), 0)
        updateCandidatesOrCommitText()
    }

    fun selectCandidate(index: Int): String? {
        Rime.selectCandidate(index)
        return updateCandidatesOrCommitText()
    }

    fun getNextPageCandidates(): Array<CandidateListItem> {
        return if (Rime.hasRight()) {
            Rime.processKey(Rime.getRimeKeycodeByName("Page_Down"), 0)
           val candidates = Rime.getRimeContext()!!.candidates
            val englishSchema = isEnglishSchema
            for (item in candidates) item.text = toCandidateCase(item.text, englishSchema)
            candidates
        } else emptyArray()
    }

    fun selectPinyin(index: Int) {
        Rime.selectPinyin(index)
        updateCandidatesOrCommitText()
    }

    fun predictAssociationWords(text: String) {
        pinyins = emptyArray()
        if (text.isNotEmpty()) {
            showCandidates = Rime.getAssociateList(text)
                .filterNotNull()
                .map { CandidateListItem("", it) }
            showComposition = ""
        }
    }

    fun selectAssociation(index: Int) {
        val content = showCandidates.getOrNull(index)?.text.orEmpty()
        Rime.chooseAssociate(index)
        updateCandidatesOrCommitText()
        preCommitText = content
    }

    fun reset() {
        showCandidates = emptyList()
        pinyins = emptyArray()
        showComposition = ""
        preCommitText = ""
        Rime.clearComposition()
        if(charCase == KeyEvent.META_SHIFT_ON) charCase = MASK_CASE_LOWER
    }

    fun destroy() = Rime.destroy()

    private fun updateCandidatesOrCommitText(): String? {
        val rimeCommit = Rime.getRimeCommit()
        if (rimeCommit != null) {
            preCommitText = toCandidateCase(rimeCommit.commitText, isEnglishSchema)
            showComposition = ""
            showCandidates = emptyList()
            return preCommitText
        }
        val candidates = Rime.getRimeContext()?.candidates?.asList() ?: emptyList()
        showCandidates = candidates
        val rimeSchema = Rime.getCurrentRimeSchema()
        val composition = toCompositionCase(getCurrentComposition(candidates))
        val englishSchema = rimeSchema == CustomConstant.SCHEMA_EN
        for (item in showCandidates) item.text = toCandidateCase(item.text, englishSchema)
        pinyins = Rime.pinyins()
        showComposition = composition
        preCommitText = ""
        return null
    }

    /**
     * 拿到候选词拼音组合
     */
    fun getPrefixs(): Array<String> {
        return pinyins
    }

    private fun getCurrentComposition(candidates: List<CandidateListItem>): String {
        val composition = Rime.compositionText
        val rimeSchema = Rime.getCurrentRimeSchema()
        if(rimeSchema == CustomConstant.SCHEMA_EN) return ""
        if(composition.isEmpty()) return ""
        if(candidates.isEmpty()) return composition
        val comment = candidates.firstOrNull { !it.comment.startsWith(YUYAN_CANDIDATE_PREFIX) }?.comment
            ?: return composition
        val result =  when {
            comment.isNotBlank() && comment.startsWith("~") -> composition
            rimeSchema == CustomConstant.SCHEMA_ZH_T9 -> {
                T9PinYinUtils.getT9Composition(composition, comment)
            }
            rimeSchema.startsWith(CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY) -> {
                if(!AppPrefs.getInstance().keyboardSetting.keyboardDoubleInputKey.getValue()) composition
                else DoublePinYinUtils.getDoublePinYinComposition(rimeSchema, composition, comment)
            }
            else -> {
                QwertyPinYinUtils.getQwertyComposition(composition, comment)
            }
        }
        return if (!composition.endsWith("'") && result.endsWith("'")) result.dropLast(1) else result
    }

    /**
     * 设置输入法搜索参数
     */
    fun setImeOption(option: String, value: Boolean) {
        Rime.setOption(option, value)
    }

    /**
     * 扫描 [text] 尾部算式并求值，[window] 为调用方取到的文本窗口大小。
     */
    fun getExpressionCandidates(text: String, window: Int): ExpressionResponse {
        val raw = Rime.getExpressionCandidates(text, window)
        return when {
            raw == "1" -> ExpressionResponse.None
            raw.startsWith("1\n") -> ExpressionResponse.Candidates(
                raw.substring(2).lineSequence().filter { it.isNotEmpty() }.toList()
            )
            raw == "2" -> ExpressionResponse.NeedMore
            else -> ExpressionResponse.None
        }
    }

    fun nextExpressionWindow(window: Int): Int? =
        if (window >= EXPRESSION_MAX_WINDOW) null
        else (window * 4).coerceAtMost(EXPRESSION_MAX_WINDOW)

    fun setCharCase(charCase: Int) {
        this.charCase = charCase
    }

    private val isEnglishSchema: Boolean
        get() = Rime.getCurrentRimeSchema() == CustomConstant.SCHEMA_EN

    /**
     * 候选词与上屏文本的大小写转换。
     */
    private fun toCandidateCase(text: String, englishSchema: Boolean): String {
        if (text.isEmpty() || text.any { it.code > 0x7F }) return text
        return when (charCase) {
            KeyEvent.META_SHIFT_ON -> text.lowercase(Locale.ENGLISH).replaceFirstChar { it.titlecase(Locale.ENGLISH) }
            KeyEvent.META_CAPS_LOCK_ON -> text.uppercase(Locale.ENGLISH)
            else -> if (englishSchema) text.lowercase(Locale.ENGLISH) else text
        }
    }

    /**
     * 候选词上方拼音（按键串）的大小写，始终跟随 Shift 状态显示
     */
    private fun toCompositionCase(text: String): String = when (charCase) {
        KeyEvent.META_SHIFT_ON -> text.lowercase(Locale.ENGLISH).replaceFirstChar { it.titlecase(Locale.ENGLISH) }
        KeyEvent.META_CAPS_LOCK_ON -> text.uppercase(Locale.ENGLISH)
        else -> text.lowercase(Locale.ENGLISH)
    }

}