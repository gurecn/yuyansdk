package com.yuyan.imemodule.entity.handwriting

import kotlin.math.pow
import kotlin.math.sqrt

class TimedPoint(val x: Float, val y: Float) {
    val timestamp = System.currentTimeMillis()

    fun velocityFrom(start: TimedPoint): Float {
        val velocity = distanceTo(start) / (this.timestamp - start.timestamp)
        if (velocity.isNaN()) return 0f
        return velocity
    }

    fun distanceTo(point: TimedPoint): kotlin.Float {
        return sqrt(
            (point.x - this.x).toDouble().pow(2.0) + (point.y - this.y).toDouble().pow(2.0)
        ).toFloat()
    }
}