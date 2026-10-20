package com.yuyan.imemodule.handwriting.internal

import android.content.Context
import com.yuyan.imemodule.handwriting.Candidate
import com.yuyan.imemodule.handwriting.Stroke
import kotlin.math.exp
import kotlin.math.ln

internal class RecEngine(
    private val context: Context,
    cpuThreads: Int = Predictor.DEFAULT_CPU_THREADS,
    powerMode: Int = Predictor.DEFAULT_POWER_MODE
) {

    companion object {
        private const val MAX_ALTERNATIVES_PER_CHAR = 3
        private const val MAX_CANDIDATES = 8
        private const val PER_CHAR_CONFIDENCE_WEIGHT = 0.9f

        private const val SKIP_PER_CHAR_MIN_CONFIDENCE = 0.9f
    }

    private var isInitialized = false

    private val preprocessor = ImagePreprocessor()
    private val predictor = Predictor(context, cpuThreads, powerMode)

    val isReady: Boolean get() = isInitialized && predictor.isReady()

    fun load(): Boolean {
        if (isInitialized) return true

        val loaded = predictor.load()
        isInitialized = loaded
        if (loaded) predictor.warmUp()
        return loaded
    }

    fun recognize(strokes: List<Stroke>): List<Candidate> {
        if (!isInitialized || strokes.isEmpty()) return emptyList()

        return try {
            recognizeLine(strokes)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun recognizeLine(strokes: List<Stroke>): List<Candidate> {
        // 先切分再渲染：渲染需要知道字数才能把宽度撑到「每字一个正方形」
        val groups = StrokeSegmenter.segment(strokes)
        val lineInput = preprocessor.renderStrokesForRec(
            strokes,
            expectedChars = groups.size.coerceAtLeast(1)
        )
        val lineCandidates = predictor.predict(lineInput)

        val charCandidates = if (shouldRunPerChar(lineCandidates, groups.size)) {
            combinePerChar(groups.map { predictor.predict(preprocessor.renderStrokesForRec(it)) })
        } else {
            emptyList()
        }
        return mergeCandidates(lineCandidates, charCandidates)
    }

    // 整行置信度够高且字数不少于切分组数时跳过逐字路径（它是单字场景里最贵的一环）
    private fun shouldRunPerChar(line: List<Candidate>, groupCount: Int): Boolean {
        if (groupCount <= 1) return false

        val top = line.firstOrNull() ?: return true
        val confident = top.confidence >= SKIP_PER_CHAR_MIN_CONFIDENCE
        val charCountMatches = top.text.length >= groupCount
        return !(confident && charCountMatches)
    }

    private fun combinePerChar(perChar: List<List<Candidate>>): List<Candidate> {
        val heads = perChar.map { it.firstOrNull() ?: return emptyList() }
        if (heads.isEmpty()) return emptyList()

        val primaryText = heads.joinToString("") { it.text }
        if (primaryText.isBlank()) return emptyList()

        val combined = LinkedHashMap<String, Candidate>()
        combined[primaryText] = Candidate(primaryText, geometricMean(heads.map { it.confidence }))

        perChar.forEachIndexed { index, candidates ->
            for (alt in candidates.drop(1).take(MAX_ALTERNATIVES_PER_CHAR)) {
                if (alt.text.isBlank()) continue

                val texts = heads.mapTo(mutableListOf()) { it.text }
                texts[index] = alt.text
                val text = texts.joinToString("")
                if (text == primaryText) continue

                val scores = heads.mapTo(mutableListOf()) { it.confidence }
                scores[index] = alt.confidence
                // minSdk 23：putIfAbsent 是 API 24 才有的 Map 方法
                combined.getOrPut(text) { Candidate(text, geometricMean(scores)) }
            }
        }

        return combined.values
            .sortedByDescending { it.confidence }
            .take(MAX_CANDIDATES)
    }

    // 首位一律采信整行结果，逐字结果只用于补充后续候选
    private fun mergeCandidates(line: List<Candidate>, perChar: List<Candidate>): List<Candidate> {
        if (perChar.isEmpty()) return line
        if (line.isEmpty()) return perChar

        val primary = line.first()
        val merged = LinkedHashMap<String, Candidate>()
        merged[primary.text] = primary
        line.drop(1).forEach { merged.getOrPut(it.text) { it } }
        perChar.forEach {
            merged.getOrPut(it.text) {
                it.copy(confidence = it.confidence * PER_CHAR_CONFIDENCE_WEIGHT)
            }
        }

        return listOf(primary) + merged.values
            .filter { it.text != primary.text }
            .sortedByDescending { it.confidence }
            .take(MAX_CANDIDATES - 1)
    }

    private fun geometricMean(values: List<Float>): Float {
        if (values.isEmpty()) return 0f
        var sum = 0.0
        for (value in values) sum += ln(value.coerceAtLeast(1e-6f).toDouble())
        return exp(sum / values.size).toFloat()
    }
}
