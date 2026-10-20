package com.yuyan.imemodule.handwriting.internal

import kotlin.collections.iterator
import kotlin.math.exp
import kotlin.math.ln

internal class CtcDecoder(private val dictionary: List<String>) {

    companion object {
        private const val BLANK_INDEX = 0
        private const val DEFAULT_BEAM_WIDTH = 10
        private const val DEFAULT_TOP_K = 3
        const val DEFAULT_MAX_CANDIDATES = 8
        private const val SPACE_CHARS = " \u3000\t"
        private val NEG_INF = Double.NEGATIVE_INFINITY

        private fun stripSpaces(text: String): String =
            if (text.none { it in SPACE_CHARS }) text else text.filter { it !in SPACE_CHARS }
    }

    data class Decoded(val text: String, val confidence: Float)

    private fun labelOf(index: Int): String = when {
        index <= BLANK_INDEX -> ""
        index <= dictionary.size -> dictionary[index - 1]
        else -> " "
    }

    fun decodeBeam(output: FloatArray, timesteps: Int, numClasses: Int, beamWidth: Int = DEFAULT_BEAM_WIDTH, topK: Int = DEFAULT_TOP_K, maxCandidates: Int = DEFAULT_MAX_CANDIDATES): List<Decoded> {
        if (timesteps <= 0 || numClasses <= 1) return emptyList()
        if (output.size < timesteps * numClasses) return emptyList()

        var beam: LinkedHashMap<CharsKey, DoubleArray> =
            linkedMapOf(CharsKey(IntArray(0)) to doubleArrayOf(0.0, NEG_INF))

        for (t in 0 until timesteps) {
            val offset = t * numClasses
            val topIndices = topKIndices(output, offset, numClasses, topK)
            val next = HashMap<CharsKey, DoubleArray>(beam.size * (topIndices.size + 1))

            for ((key, probs) in beam) {
                val pBlank = probs[0]
                val pNonBlank = probs[1]
                val pTotal = logAdd(pBlank, pNonBlank)
                accumulate(next, key, logProb(output[offset + BLANK_INDEX]) + pTotal, toBlank = true)
                val last = if (key.chars.isEmpty()) -1 else key.chars[key.chars.size - 1]
                for (index in topIndices) {
                    if (index == BLANK_INDEX) continue
                    val logP = logProb(output[offset + index])
                    if (index == last) {
                        accumulate(next, key.plus(index), logP + pBlank, toBlank = false)
                        accumulate(next, key, logP + pNonBlank, toBlank = false)
                    } else {
                        accumulate(next, key.plus(index), logP + pTotal, toBlank = false)
                    }
                }
            }

            val ordered = LinkedHashMap<CharsKey, DoubleArray>(beamWidth)
            next.entries
                .sortedByDescending { logAdd(it.value[0], it.value[1]) }
                .take(beamWidth)
                .forEach { ordered[it.key] = it.value }
            beam = ordered
        }

        return beam.entries
            .map { (key, probs) ->
                val logScore = logAdd(probs[0], probs[1])
                Decoded(
                    text = stripSpaces(key.chars.joinToString("") { labelOf(it) }),
                    confidence = exp(logScore / timesteps).toFloat()
                )
            }
            .filter { it.text.isNotBlank() }
            .sortedByDescending { it.confidence }
            .distinctBy { it.text }
            .take(maxCandidates)
    }

    private class CharsKey(val chars: IntArray) {
        override fun equals(other: Any?): Boolean =
            other is CharsKey && chars.contentEquals(other.chars)

        override fun hashCode(): Int = chars.contentHashCode()

        fun plus(index: Int): CharsKey {
            val next = IntArray(chars.size + 1)
            chars.copyInto(next)
            next[chars.size] = index
            return CharsKey(next)
        }
    }

    private fun accumulate(
        target: HashMap<CharsKey, DoubleArray>,
        key: CharsKey,
        logP: Double,
        toBlank: Boolean
    ) {
        if (logP == NEG_INF) return
        val slot = target.getOrPut(key) { doubleArrayOf(NEG_INF, NEG_INF) }
        if (toBlank) {
            slot[0] = logAdd(slot[0], logP)
        } else {
            slot[1] = logAdd(slot[1], logP)
        }
    }

    private fun logAdd(a: Double, b: Double): Double {
        if (a == NEG_INF) return b
        if (b == NEG_INF) return a
        val max = if (a > b) a else b
        return max + ln(exp(a - max) + exp(b - max))
    }

    private fun logProb(p: Float): Double = ln(p.toDouble().coerceAtLeast(1e-12))

    private fun topKIndices(output: FloatArray, offset: Int, numClasses: Int, k: Int): IntArray {
        val size = k.coerceIn(1, numClasses)
        val indices = IntArray(size) { -1 }
        val probs = FloatArray(size) { -1f }

        for (c in 0 until numClasses) {
            val p = output[offset + c]
            if (p <= probs[size - 1]) continue
            var i = size - 1
            while (i > 0 && probs[i - 1] < p) {
                probs[i] = probs[i - 1]
                indices[i] = indices[i - 1]
                i--
            }
            probs[i] = p
            indices[i] = c
        }

        return indices.filter { it >= 0 }.toIntArray()
    }
}
