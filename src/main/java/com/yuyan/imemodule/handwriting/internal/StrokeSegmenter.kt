package com.yuyan.imemodule.handwriting.internal

import com.yuyan.imemodule.handwriting.Stroke

internal object StrokeSegmenter {

    private const val GAP_RATIO = 0.05f
    private const val MIN_GAP_PX = 8f
    private const val SINGLE_CHAR_ASPECT = 0.9f
    private const val MAX_CHARS = 8
    private const val FRAGMENT_WIDTH_RATIO = 0.35f

    fun segment(strokes: List<Stroke>): List<List<Stroke>> {
        val totalPoints = strokes.sumOf { it.points.size }
        if (strokes.isEmpty() || totalPoints == 0) return emptyList()

        var minX = Float.MAX_VALUE
        var maxX = -Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var maxY = -Float.MAX_VALUE
        for (stroke in strokes) {
            for (point in stroke.points) {
                if (point.x < minX) minX = point.x
                if (point.x > maxX) maxX = point.x
                if (point.y < minY) minY = point.y
                if (point.y > maxY) maxY = point.y
            }
        }

        val width = maxX - minX
        val height = maxY - minY
        if (width < 1f || height < 1f) return listOf(strokes)
        if (width / height < SINGLE_CHAR_ASPECT) return listOf(strokes)

        val columns = width.toInt() + 2
        val occupied = BooleanArray(columns)
        markOccupied(strokes, minX, columns, occupied)

        val gapThreshold = maxOf(MIN_GAP_PX, height * GAP_RATIO)
        val cuts = findCuts(occupied, gapThreshold, minX)
        if (cuts.isEmpty()) return listOf(strokes)

        val groups = mergeFragments(groupByCuts(strokes, cuts), height)
        return if (groups.size <= 1 || groups.size > MAX_CHARS) listOf(strokes) else groups
    }

    private fun markOccupied(strokes: List<Stroke>, minX: Float, columns: Int, occupied: BooleanArray) {
        for (stroke in strokes) {
            val points = stroke.points
            if (points.size == 1) {
                occupied[(points[0].x - minX).toInt().coerceIn(0, columns - 1)] = true
                continue
            }
            for (i in 1 until points.size) {
                val x0 = points[i - 1].x
                val x1 = points[i].x
                val from = minOf(x0, x1)
                val to = maxOf(x0, x1)
                var x = from
                while (x <= to) {
                    occupied[(x - minX).toInt().coerceIn(0, columns - 1)] = true
                    x += 1f
                }
            }
        }
    }

    private fun findCuts(occupied: BooleanArray, gapThreshold: Float, minX: Float): List<Float> {
        val cuts = mutableListOf<Float>()
        var runStart = -1

        for (i in occupied.indices) {
            if (!occupied[i]) {
                if (runStart < 0) runStart = i
            } else {
                if (runStart >= 0) {
                    val runLength = i - runStart
                    if (runLength >= gapThreshold) {
                        cuts.add(minX + (runStart + i) / 2f)
                    }
                    runStart = -1
                }
            }
        }
        return cuts
    }

    private fun groupByCuts(strokes: List<Stroke>, cuts: List<Float>): List<List<Stroke>> {
        val groups = List(cuts.size + 1) { mutableListOf<Stroke>() }

        for (stroke in strokes) {
            val centerX = strokeCenterX(stroke) ?: continue
            val index = cuts.indexOfFirst { centerX < it }
            groups[if (index < 0) cuts.size else index].add(stroke)
        }

        return groups.filter { it.isNotEmpty() }
    }

    private fun mergeFragments(
        groups: List<List<Stroke>>,
        globalHeight: Float
    ): List<List<Stroke>> {
        if (groups.size <= 1) return groups

        val minWidth = globalHeight * FRAGMENT_WIDTH_RATIO
        val merged = mutableListOf<MutableList<Stroke>>()

        for (group in groups) {
            val previous = merged.lastOrNull()
            if (previous != null && groupWidth(group) < minWidth) {
                previous.addAll(group)
            } else {
                merged.add(group.toMutableList())
            }
        }

        while (merged.size > 1 && groupWidth(merged[0]) < minWidth) {
            merged[1].addAll(0, merged[0])
            merged.removeAt(0)
        }

        return merged
    }

    private fun groupWidth(group: List<Stroke>): Float {
        var min = Float.MAX_VALUE
        var max = -Float.MAX_VALUE
        for (stroke in group) {
            for (point in stroke.points) {
                if (point.x < min) min = point.x
                if (point.x > max) max = point.x
            }
        }
        return if (max < min) 0f else max - min
    }

    private fun strokeCenterX(stroke: Stroke): Float? {
        if (stroke.points.isEmpty()) return null
        var min = Float.MAX_VALUE
        var max = -Float.MAX_VALUE
        for (point in stroke.points) {
            if (point.x < min) min = point.x
            if (point.x > max) max = point.x
        }
        return (min + max) / 2f
    }
}
