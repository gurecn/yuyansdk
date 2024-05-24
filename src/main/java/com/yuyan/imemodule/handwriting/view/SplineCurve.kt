package com.yuyan.imemodule.handwriting.view

import kotlin.math.sqrt

/**
 * Author:gurecn@gmail.com
 * Time:2019/4/22
 * Description:This is SplineCurve
 */
class SplineCurve(
    var point1: TimePoint, //起点
    @JvmField var point2: TimePoint,
    //结束点
    @JvmField var point3: TimePoint, var point4: TimePoint
) {
    @JvmField
    var steps = 10 //设定在曲线上取的点数

    //获得贝塞尔曲线中取得的10个点的相邻点距离和
    fun length(): Float {
        var length = 0
        var perStep: Float
        var cx: Double
        var cy: Double
        var px = 0.0
        var py = 0.0
        var xdiff: Double
        var ydiff: Double
        for (i in 0..steps) {
            perStep = i.toFloat() / steps
            cx = point(
                perStep, point1.x, point2.x, point3.x,
                point4.x
            )
            cy = point(
                perStep, point1.y, point2.y, point3.y,
                point4.y
            )
            if (i > 0) { //计算与上一个点的距离
                xdiff = cx - px
                ydiff = cy - py
                length += sqrt(xdiff * xdiff + ydiff * ydiff).toInt()
            }
            px = cx
            py = cy
        }
        return length.toFloat()
    }

    //通过贝塞尔算法返回每个点的x或者y值
    fun point(perStep: Float, point1: Float, point2: Float, point3: Float, point4: Float): Double {
        return point1 * (1.0 - perStep) * (1.0 - perStep) * (1.0 - perStep) / 6.0 + point2 * (3 * perStep * perStep * perStep - 6 * perStep * perStep + 4) / 6.0 + point3 * (-3 * perStep * perStep * perStep + 3 * perStep * perStep + 3 * perStep + 1) / 6.0 + point4 * perStep * perStep * perStep / 6.0
    }
}
