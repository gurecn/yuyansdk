package com.yuyan.imemodule.handwriting.view

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import java.util.Vector

/**
 * Author:gurecn@gmail.com
 * Time:2019/4/22
 * Description:This is Strokes
 */
class Strokes {
    //当前笔划列表
    @JvmField
    val pathsVector: Vector<Stroke> = Vector()

    val pathsVectorSize: Int
        get() = pathsVector.size

    fun addPathsVector() {
        pathsVector.add(Stroke())
    }

    fun draw(canvas: Canvas, paint: Paint) {
        if (pathsVector.size == 0) return
        for (i in pathsVector.indices) {
            val stroke = pathsVector.elementAt(i)
            val path = stroke.getStroke()
            val alpha = stroke.alpha
            paint.setAlpha(alpha)
            if (!path.isEmpty) {
                canvas.drawPath(path, paint)
            }
        }
    }

    inner class Stroke {
        private var stroke: Path
        @JvmField
        var alpha = 255

        init {
            stroke = Path()
        }

        fun getStroke(): Path {
            return stroke
        }

        fun setStroke(path: Path?) {
            stroke = Path(path)
        }
    }
}
