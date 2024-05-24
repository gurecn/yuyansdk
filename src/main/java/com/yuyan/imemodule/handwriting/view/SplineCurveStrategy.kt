package com.yuyan.imemodule.handwriting.view

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * Author:gurecn@gmail.com
 * Time:2019/4/22
 * Description:This is SplineCurveStrategy
 */
class SplineCurveStrategy(
    var splineCurve: SplineCurve,
    var startWidth: Float,
    var endWidth: Float,
    canvas: Canvas,
    mPaint: Paint
) {
    var curveIndex = 2
    var blurryPaint: Paint
    var mMosaicPaint: Paint
    var eraserPaint: Paint
    val eraserWidth = 50
    protected var canvas: Canvas
    protected var mPaint: Paint
    var lastTop: TimePoint? = null
    var lastBottom: TimePoint? = null
    protected var mPath: Path

    init {
        blurryPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mMosaicPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        eraserPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        eraserPaint.setColor(Color.WHITE)
        eraserPaint.strokeWidth = eraserWidth.toFloat()
        eraserPaint.strokeCap = Paint.Cap.ROUND
        eraserPaint.strokeJoin = Paint.Join.ROUND
        mMosaicPaint.setAlpha(80)
        mMosaicPaint.strokeCap = Paint.Cap.BUTT
        mMosaicPaint.strokeJoin = Paint.Join.ROUND
        mMosaicPaint.strokeWidth = 12f
        this.canvas = canvas
        this.mPaint = mPaint
        mPath = Path()
    }

    fun updateData(startWidth: Float, endWidth: Float, splineCurve: SplineCurve) {
        this.splineCurve = splineCurve
        this.startWidth = startWidth
        this.endWidth = endWidth
    }

    fun initLastPoint(lastTop: TimePoint?, lastBottom: TimePoint?) {
        this.lastTop = lastTop
        this.lastBottom = lastBottom
    }

    fun drawPen(drawingStrokes: DrawingStrokes) {
//        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setColor(Color.BLACK);
        //获得笔在两点间不同宽度的差值
        var drawSteps = floor(splineCurve.length().toDouble()).toInt()
        if (drawingStrokes.isUp) {
            curveIndex = if (drawSteps > 2) (drawSteps - 2) / 2 else 1
            if (curveIndex < 1) curveIndex = 1
            if (drawSteps == 0) drawSteps = 2
        } else if (drawingStrokes.isDown) {
            curveIndex = 1
            if (drawSteps == 0) drawSteps = 2
        } else {
            curveIndex =
                if (drawSteps > 100) 40 else if (drawSteps > 80) 35 else if (drawSteps > 70) 30 else if (drawSteps > 60) 25 else if (drawSteps > 50) 20 else if (drawSteps > 40) 15 else if (drawSteps > 30) 13 else if (drawSteps > 20) 9 else if (drawSteps > 10) 7 else if (drawSteps >= 4) 3 else 1
        }
        val widthDelta = endWidth - startWidth
        //两点间实际轨迹距离
        var k = 0f
        var myPointC: TimePoint
        var myPointD: TimePoint
        var myPointA: TimePoint
        var myPointB: TimePoint
        if (drawSteps == 0) drawSteps = 1
        var i = 0
        var num = 1
        while (i < drawSteps) {
            mPath.reset()
            val t = i.toFloat() / drawSteps
            val tt = t * t
            val ttt = tt * t
            val u = 1 - t
            val uu = u * u
            val uuu = uu * u
            var x = uuu * splineCurve.point1.x / 6.0f
            x += (3 * ttt - 6 * tt + 4) * splineCurve.point2.x / 6.0f
            x += (-3 * ttt + 3 * tt + 3 * t + 1) * splineCurve.point3.x / 6.0f
            x += ttt * splineCurve.point4.x / 6.0f
            var y = uuu * splineCurve.point1.y / 6.0f
            y += (3 * ttt - 6 * tt + 4) * splineCurve.point2.y / 6.0f
            y += (-3 * ttt + 3 * tt + 3 * t + 1) * splineCurve.point3.y / 6.0f
            y += ttt * splineCurve.point4.y / 6.0f
            var currentWidth = startWidth + t * widthDelta
            if (!drawingStrokes.isUp) if (abs((t * widthDelta).toDouble()) > 0.2f * num) {
                currentWidth =
                    if (t * widthDelta > 0) startWidth + 0.2f * num else startWidth - 0.2f * num
            }
            if (x != drawingStrokes.mLastX) {
                k = (y - drawingStrokes.mLastY) / (x - drawingStrokes.mLastX)
                //上个点的上下端点MyPointA,MyPointB
                myPointA = TimePoint(
                    drawingStrokes.mLastWidth / 2 * -k / sqrt((k * k + 1).toDouble())
                        .toFloat() + drawingStrokes.mLastX,
                    drawingStrokes.mLastWidth / 2 / sqrt((k * k + 1).toDouble())
                        .toFloat() + drawingStrokes.mLastY
                )
                myPointB = TimePoint(
                    -drawingStrokes.mLastWidth / 2 * -k / sqrt((k * k + 1).toDouble())
                        .toFloat() + drawingStrokes.mLastX,
                    -drawingStrokes.mLastWidth / 2 / sqrt((k * k + 1).toDouble())
                        .toFloat() + drawingStrokes.mLastY
                )
                //当前点的上下端点MyPointC,MyPointD
                myPointC = TimePoint(
                    currentWidth / 2 * -k / sqrt((k * k + 1).toDouble())
                        .toFloat() + x,
                    currentWidth / 2 / sqrt((k * k + 1).toDouble()).toFloat() + y
                )
                myPointD = TimePoint(
                    -currentWidth / 2 * -k / sqrt((k * k + 1).toDouble())
                        .toFloat() + x,
                    -currentWidth / 2 / sqrt((k * k + 1).toDouble()).toFloat() + y
                )
            } else {
                myPointA = TimePoint(
                    drawingStrokes.mLastWidth / 2 + drawingStrokes.mLastX,
                    drawingStrokes.mLastY
                )
                myPointB = TimePoint(
                    -drawingStrokes.mLastWidth / 2 + drawingStrokes.mLastX,
                    drawingStrokes.mLastY
                )
                myPointC = TimePoint(currentWidth / 2 + x, y)
                myPointD = TimePoint(-currentWidth / 2 + x, y)
            }
            if (drawingStrokes.isDown) { //起点  需要算AB
                //算出矩形的四个点
                var A: TimePoint
                var B: TimePoint
                var C: TimePoint
                var D: TimePoint
                if (myPointA.x != myPointB.x) {
                    k = (myPointA.y - myPointB.y) / (myPointA.x - myPointB.x)
                    A = TimePoint(
                        drawingStrokes.mLastWidth * -k / sqrt((k * k + 1).toDouble())
                            .toFloat() + myPointA.x,
                        drawingStrokes.mLastWidth / sqrt((k * k + 1).toDouble())
                            .toFloat() + myPointA.y
                    )
                    B = TimePoint(
                        -drawingStrokes.mLastWidth * -k / sqrt((k * k + 1).toDouble())
                            .toFloat() + myPointA.x,
                        -drawingStrokes.mLastWidth / sqrt((k * k + 1).toDouble())
                            .toFloat() + myPointA.y
                    )
                    //当前点的上下端点MyPointC,MyPointD
                    C = TimePoint(
                        drawingStrokes.mLastWidth * -k / sqrt((k * k + 1).toDouble())
                            .toFloat() + myPointB.x,
                        drawingStrokes.mLastWidth / sqrt((k * k + 1).toDouble())
                            .toFloat() + myPointB.y
                    )
                    D = TimePoint(
                        -drawingStrokes.mLastWidth * -k / sqrt((k * k + 1).toDouble())
                            .toFloat() + myPointB.x,
                        -drawingStrokes.mLastWidth / sqrt((k * k + 1).toDouble())
                            .toFloat() + myPointB.y
                    )
                } else {
                    A = TimePoint(drawingStrokes.mLastWidth + myPointA.x, myPointA.y)
                    B = TimePoint(-drawingStrokes.mLastWidth + myPointA.x, myPointA.y)
                    C = TimePoint(drawingStrokes.mLastWidth + myPointB.x, myPointB.y)
                    D = TimePoint(-drawingStrokes.mLastWidth / 2 + myPointB.x, myPointB.y)
                }
                val centerAC = TimePoint((A.x + C.x) / 2, (A.y + C.y) / 2)
                val centerBD = TimePoint((B.x + D.x) / 2, (B.y + D.y) / 2)
                var isAC: Boolean
                isAC = if (myPointA.x != myPointB.x) {
                    val b = myPointA.y - k * myPointA.x
                    if ((centerAC.y - k * centerAC.x - b) * (drawingStrokes.mPoint[3].y - k * drawingStrokes.mPoint[3].x - b) <= 0) {
                        true
                    } else {
                        false
                    }
                } else {
                    if ((centerAC.y - myPointA.y) * (drawingStrokes.mPoint[3].y - myPointA.y) <= 0) {
                        true
                    } else {
                        false
                    }
                }
                mPath.moveTo(myPointB.x, myPointB.y)
                if (isAC) mPath.quadTo(
                    centerAC.x,
                    centerAC.y,
                    myPointA.x,
                    myPointA.y
                ) else mPath.quadTo(centerBD.x, centerBD.y, myPointA.x, myPointA.y)
                mPath.lineTo(myPointC.x, myPointC.y)
                mPath.lineTo(myPointD.x, myPointD.y)
                mPath.lineTo(myPointB.x, myPointB.y)
                canvas.drawPath(mPath, mPaint)
                mPath.reset()
                drawingStrokes.isDown = false
                drawingStrokes.strokesPath?.moveTo(myPointB.x, myPointB.y)
                if (isAC) {
                    drawingStrokes.strokesPath?.quadTo(
                        centerAC.x,
                        centerAC.y,
                        myPointA.x,
                        myPointA.y
                    )
                } else {
                    drawingStrokes.strokesPath?.quadTo(
                        centerBD.x,
                        centerBD.y,
                        myPointA.x,
                        myPointA.y
                    )
                }
                drawingStrokes.strokesPath?.lineTo(myPointC.x, myPointC.y)
                drawingStrokes.lastLineX = myPointC.x
                drawingStrokes.lastLineY = myPointC.y
                drawingStrokes.timePoints.add(TimePoint(myPointB.x, myPointB.y))
                drawingStrokes.timePoints.add(TimePoint(myPointD.x, myPointD.y))
            } else {
                //相交为180
                if (!(drawingStrokes.mLLastX == drawingStrokes.mLastX && drawingStrokes.mLastX == x || drawingStrokes.mLLastX != drawingStrokes.mLastX && drawingStrokes.mLastX != x && (k == drawingStrokes.mLastK || -k == drawingStrokes.mLastK))) {
                    //判断外端点画弧
                    val degereeA = drawingStrokes.calculateDegree(
                        drawingStrokes.mLastX, drawingStrokes.mLastY,
                        drawingStrokes.mLLastX, drawingStrokes.mLLastY, myPointA.x, myPointA.y
                    )
                    val degereeB = drawingStrokes.calculateDegree(
                        drawingStrokes.mLastX, drawingStrokes.mLastY,
                        drawingStrokes.mLLastX, drawingStrokes.mLLastY, myPointB.x, myPointB.y
                    )
                    val degereeLT = drawingStrokes.calculateDegree(
                        drawingStrokes.mLastX, drawingStrokes.mLastY,
                        x, y, lastTop!!.x, lastTop!!.y
                    )
                    val degereeLB = drawingStrokes.calculateDegree(
                        drawingStrokes.mLastX, drawingStrokes.mLastY,
                        x, y, lastBottom!!.x, lastBottom!!.y
                    )
                    //谁大谁是外端点
                    if (degereeA >= degereeB && degereeLT >= degereeLB || degereeA <= degereeB && degereeLT <= degereeLB) {
                        //填充
                        mPath.moveTo(myPointA.x, myPointA.y)
                        mPath.lineTo(lastTop!!.x, lastTop!!.y)
                        mPath.lineTo(lastBottom!!.x, lastBottom!!.y)
                        mPath.lineTo(myPointB.x, myPointB.y)
                        mPath.lineTo(myPointA.x, myPointA.y)
                        canvas.drawPath(mPath, mPaint)
                        mPath.reset()
                        if (drawingStrokes.lastLineX == lastTop!!.x && drawingStrokes.lastLineY == lastTop!!.y) {
                            drawingStrokes.strokesPath?.lineTo(myPointA.x, myPointA.y)
                            drawingStrokes.timePoints.add(TimePoint(myPointB.x, myPointB.y))
                            drawingStrokes.lastLineX = myPointA.x
                            drawingStrokes.lastLineY = myPointA.y
                        } else {
                            drawingStrokes.strokesPath?.lineTo(myPointB.x, myPointB.y)
                            drawingStrokes.timePoints.add(TimePoint(myPointA.x, myPointA.y))
                            drawingStrokes.lastLineX = myPointB.x
                            drawingStrokes.lastLineY = myPointB.y
                        }
                    } else {
                        //填充
                        mPath.moveTo(myPointA.x, myPointA.y)
                        mPath.lineTo(lastBottom!!.x, lastBottom!!.y)
                        mPath.lineTo(lastTop!!.x, lastTop!!.y)
                        mPath.lineTo(myPointB.x, myPointB.y)
                        mPath.lineTo(myPointA.x, myPointA.y)
                        canvas.drawPath(mPath, mPaint)
                        mPath.reset()
                        if (drawingStrokes.lastLineX == lastBottom!!.x && drawingStrokes.lastLineY == lastBottom!!.y) {
                            drawingStrokes.strokesPath?.lineTo(myPointA.x, myPointA.y)
                            drawingStrokes.timePoints.add(TimePoint(myPointB.x, myPointB.y))
                            drawingStrokes.lastLineX = myPointA.x
                            drawingStrokes.lastLineY = myPointA.y
                        } else {
                            drawingStrokes.strokesPath?.lineTo(myPointB.x, myPointB.y)
                            drawingStrokes.timePoints.add(TimePoint(myPointA.x, myPointA.y))
                            drawingStrokes.lastLineX = myPointB.x
                            drawingStrokes.lastLineY = myPointB.y
                        }
                    }
                }
                //填充
                mPath.moveTo(myPointA.x, myPointA.y)
                mPath.lineTo(myPointC.x, myPointC.y)
                mPath.lineTo(myPointD.x, myPointD.y)
                mPath.lineTo(myPointB.x, myPointB.y)
                mPath.lineTo(myPointA.x, myPointA.y)
                canvas.drawPath(mPath, mPaint)
                mPath.reset()
                if (drawingStrokes.lastLineX == myPointA.x && drawingStrokes.lastLineY == myPointA.y) {
                    drawingStrokes.strokesPath?.lineTo(myPointC.x, myPointC.y)
                    drawingStrokes.timePoints.add(TimePoint(myPointD.x, myPointD.y))
                    drawingStrokes.lastLineX = myPointC.x
                    drawingStrokes.lastLineY = myPointC.y
                } else {
                    drawingStrokes.strokesPath?.lineTo(myPointD.x, myPointD.y)
                    drawingStrokes.timePoints.add(TimePoint(myPointC.x, myPointC.y))
                    drawingStrokes.lastLineX = myPointD.x
                    drawingStrokes.lastLineY = myPointD.y
                }
            }
            if (drawingStrokes.isUp && i >= drawSteps - curveIndex) {
                var A: TimePoint
                var B: TimePoint
                var C: TimePoint
                var D: TimePoint
                if (myPointC.x != myPointD.x) {
                    k = (myPointC.y - myPointD.y) / (myPointC.x - myPointD.x)
                    A = TimePoint(
                        currentWidth * -k / sqrt((k * k + 1).toDouble()).toFloat() + myPointC.x,
                        currentWidth / sqrt((k * k + 1).toDouble()).toFloat() + myPointC.y
                    )
                    B = TimePoint(
                        -currentWidth * -k / sqrt((k * k + 1).toDouble()).toFloat() + myPointD.x,
                        -currentWidth / sqrt((k * k + 1).toDouble()).toFloat() + myPointD.y
                    )
                    //当前点的上下端点MyPointC,MyPointD
                    C = TimePoint(
                        currentWidth * -k / sqrt((k * k + 1).toDouble()).toFloat() + myPointC.x,
                        currentWidth / sqrt((k * k + 1).toDouble()).toFloat() + myPointC.y
                    )
                    D = TimePoint(
                        -currentWidth * -k / sqrt((k * k + 1).toDouble()).toFloat() + myPointD.x,
                        -currentWidth / sqrt((k * k + 1).toDouble()).toFloat() + myPointD.y
                    )
                } else {
                    A = TimePoint(
                        currentWidth + myPointC.x,
                        myPointC.y
                    )
                    B = TimePoint(
                        -currentWidth + myPointD.x,
                        myPointD.y
                    )
                    C = TimePoint(
                        currentWidth + myPointC.x,
                        myPointC.y
                    )
                    D = TimePoint(
                        -currentWidth + myPointD.x,
                        myPointD.y
                    )
                }
                val centerAC = TimePoint((A.x + C.x) / 2, (A.y + C.y) / 2)
                val centerBD = TimePoint((B.x + D.x) / 2, (B.y + D.y) / 2)
                var isAC: Boolean
                isAC = if (myPointC.x != myPointD.x) {
                    val b = myPointC.y - k * myPointC.x
                    if ((centerAC.y - k * centerAC.x - b) * (drawingStrokes.mLastY - k * drawingStrokes.mLastX - b) <= 0) {
                        true
                    } else {
                        false
                    }
                } else {
                    if ((centerAC.y - myPointC.y) * (drawingStrokes.mLastY - myPointC.y) <= 0) {
                        true
                    } else {
                        false
                    }
                }
                mPath.moveTo(myPointC.x, myPointC.y)
                if (isAC) {
                    mPath.quadTo(centerAC.x, centerAC.y, myPointD.x, myPointD.y)
                    if (drawingStrokes.lastLineX == myPointC.x && drawingStrokes.lastLineY == myPointC.y) {
                        drawingStrokes.strokesPath?.quadTo(
                            centerAC.x,
                            centerAC.y,
                            myPointD.x,
                            myPointD.y
                        )
                    } else {
                        drawingStrokes.strokesPath?.quadTo(
                            centerAC.x,
                            centerAC.y,
                            myPointC.x,
                            myPointC.y
                        )
                    }
                } else {
                    mPath.quadTo(centerBD.x, centerBD.y, myPointD.x, myPointD.y)
                    if (drawingStrokes.lastLineX == myPointC.x && drawingStrokes.lastLineY == myPointC.y) {
                        drawingStrokes.strokesPath?.quadTo(
                            centerBD.x,
                            centerBD.y,
                            myPointD.x,
                            myPointD.y
                        )
                    } else {
                        drawingStrokes.strokesPath?.quadTo(
                            centerBD.x,
                            centerBD.y,
                            myPointC.x,
                            myPointC.y
                        )
                    }
                }
                mPath.lineTo(myPointC.x, myPointC.y)
                canvas.drawPath(mPath, mPaint)
                mPath.reset()
            }
            lastTop!!.x = myPointC.x
            lastTop!!.y = myPointC.y
            lastBottom!!.x = myPointD.x
            lastBottom!!.y = myPointD.y
            drawingStrokes.mLastWidth = currentWidth
            drawingStrokes.mLLastX = drawingStrokes.mLastX
            drawingStrokes.mLLastY = drawingStrokes.mLastY
            drawingStrokes.mLastX = x
            drawingStrokes.mLastY = y
            drawingStrokes.mLastK = k
            i += curveIndex
            num++
        }
    }
}
