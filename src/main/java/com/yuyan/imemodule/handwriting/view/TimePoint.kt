package com.yuyan.imemodule.handwriting.view

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Author:gurecn@gmail.com
 * Time:2019/4/22
 * Description:This is TimePoint
 */
class TimePoint {
    @JvmField
    var x = 0f
    @JvmField
    var y = 0f
    var timestamp: Long = 0

    constructor()
    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
        timestamp = System.currentTimeMillis()
    }

    //获得两个点间的速度
    fun velocityFrom(start: TimePoint): Float {
        return distanceTo(start) / (timestamp - start.timestamp)
    }

    //获得两个点间的距离
    fun distanceTo(point: TimePoint): Float {
        return sqrt((point.x - x).pow(2.0f) + (point.y - y).pow(2.0f)).toFloat()
    }
}
