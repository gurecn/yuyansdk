package com.yuyan.imemodule.entity.handwriting

class ControlTimedPoints {
    var c1: TimedPoint? = null
    var c2: TimedPoint? = null

    fun set(c1: TimedPoint?, c2: TimedPoint?): ControlTimedPoints {
        this.c1 = c1
        this.c2 = c2
        return this
    }
}
