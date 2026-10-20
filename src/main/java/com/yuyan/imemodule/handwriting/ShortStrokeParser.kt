package com.yuyan.imemodule.handwriting

object ShortStrokeParser {

    private const val SEPARATOR_X = -1

    fun parse(raw: List<Short?>): List<Stroke> {
        if (raw.size < 2) return emptyList()

        val strokes = ArrayList<Stroke>()
        var points = ArrayList<Stroke.Point>()
        var i = 0

        while (i + 1 < raw.size) {
            val x = raw[i]?.toInt()
            val y = raw[i + 1]?.toInt()
            i += 2

            if (x == null || y == null) continue

            if (x == SEPARATOR_X) {
                if (points.isNotEmpty()) {
                    strokes.add(Stroke(points))
                    points = ArrayList()
                }
                continue
            }

            points.add(Stroke.Point(x.toFloat(), y.toFloat()))
        }

        if (points.isNotEmpty()) strokes.add(Stroke(points))

        return strokes
    }
}