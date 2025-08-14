package com.yuyan.imemodule.entity.handwriting

import kotlin.math.sqrt

class Bezier(var startPoint: TimedPoint, var control1: TimedPoint, var control2: TimedPoint, var endPoint: TimedPoint) {

    fun length(): Float {
        val steps = 10
        var length = 0f
        var cx: Double
        var cy: Double
        var px = 0.0
        var py = 0.0
        var xDiff: Double
        var yDiff: Double

        for (i in 0..steps) {
            val t = i.toFloat() / steps
            cx = point(
                t, this.startPoint.x, this.control1.x,
                this.control2.x, this.endPoint.x
            )
            cy = point(
                t, this.startPoint.y, this.control1.y,
                this.control2.y, this.endPoint.y
            )
            if (i > 0) {
                xDiff = cx - px
                yDiff = cy - py
                length += sqrt(xDiff * xDiff + yDiff * yDiff).toFloat()
            }
            px = cx
            py = cy
        }
        return length
    }

    fun point(t: Float, start: Float, c1: Float, c2: Float, end: Float): Double {
        return (start * (1.0 - t) * (1.0 - t) * (1.0 - t) + 3.0 * c1 * (1.0 - t) * (1.0 - t) * t + 3.0 * c2 * (1.0 - t) * t * t + end * t * t * t)
    }
}
