package com.yuyan.inputmethod.core

import androidx.annotation.Keep

@Keep
data class SchemaItem(
    val id: String,
    val name: String = "",
)

@Keep
data class CandidateListItem(
    var comment: String,
    var text: String,
)

@Keep
data class RimeComposition(
    val length: Int = 0,
    val cursorPos: Int = 0,
    val selStart: Int = 0,
    val selEnd: Int = 0,
    val preedit: String? = "",
)

@Keep
data class RimeMenu(
    val pageSize: Int = 0,
    val pageNo: Int = 0,
    val isLastPage: Boolean = false,
    val highlightedCandidateIndex: Int = 0,
    val numCandidates: Int = 0,
    val candidates: Array<CandidateListItem> = arrayOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RimeMenu

        if (pageSize != other.pageSize) return false
        if (pageNo != other.pageNo) return false
        if (isLastPage != other.isLastPage) return false
        if (highlightedCandidateIndex != other.highlightedCandidateIndex) return false
        if (numCandidates != other.numCandidates) return false
        if (!candidates.contentEquals(other.candidates)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pageSize
        result = 31 * result + pageNo
        result = 31 * result + isLastPage.hashCode()
        result = 31 * result + highlightedCandidateIndex
        result = 31 * result + numCandidates
        result = 31 * result + candidates.contentHashCode()
        return result
    }
}

@Keep
data class RimeCommit(
    val commitText: String = "",
)

@Keep
data class RimeContext(
    val composition: RimeComposition? = null,
    val menu: RimeMenu? = null,
    val commitTextPreview: String? = "",
    val selectLabels: Array<String> = arrayOf(),
) {
    val candidates: Array<CandidateListItem>
        get() {
            val numCandidates = menu?.numCandidates ?: 0
            return if (numCandidates != 0) menu!!.candidates else arrayOf()
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RimeContext

        if (composition != other.composition) return false
        if (menu != other.menu) return false
        if (commitTextPreview != other.commitTextPreview) return false
        if (!selectLabels.contentEquals(other.selectLabels)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = composition?.hashCode() ?: 0
        result = 31 * result + (menu?.hashCode() ?: 0)
        result = 31 * result + commitTextPreview.hashCode()
        result = 31 * result + selectLabels.contentHashCode()
        return result
    }
}

/** Rime狀態  */
@Keep
data class RimeStatus(
    val schemaId: String = "",
    val schemaName: String = "",
    val isDisable: Boolean = true,
    val isComposing: Boolean = false,
    val isAsciiMode: Boolean = true,
    val isFullShape: Boolean = false,
    val isSimplified: Boolean = false,
    val isTraditional: Boolean = false,
    val isAsciiPunch: Boolean = true,
)
