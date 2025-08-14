package com.yuyan.imemodule.entity.handwriting

import kotlin.math.pow
import kotlin.math.sqrt

class TimedPoint {
    var x: Float = 0f
    var y: Float = 0f
    var timestamp: Long = 0

    fun set(x: Float, y: Float): TimedPoint {
        this.x = x
        this.y = y
        this.timestamp = System.currentTimeMillis()
        return this
    }

    fun velocityFrom(start: TimedPoint): Float {
        var diff = this.timestamp - start.timestamp
        if (diff <= 0) {
            diff = 1
        }
        var velocity = distanceTo(start) / diff
        if (velocity.isInfinite() || velocity.isNaN()) {
            velocity = 0f
        }
        return velocity
    }

    fun distanceTo(point: TimedPoint): Float {
        return sqrt(
            (point.x - this.x).toDouble().pow(2.0) + (point.y - this.y).toDouble().pow(2.0)
        ).toFloat()
    }
}