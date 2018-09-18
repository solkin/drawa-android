package com.tomclaw.drawa.draw.tools

import android.graphics.Paint
import android.graphics.Path

class Eraser : Tool() {

    private var startX: Int = 0
    private var startY: Int = 0
    private var prevX: Int = 0
    private var prevY: Int = 0
    private var path = Path()

    override var color = ERASER_COLOR
    override val alpha = 0xff
    override val type = TYPE_ERASER

    override fun initPaint() = Paint().apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        color = ERASER_COLOR
    }

    override fun onTouchDown(x: Int, y: Int) {
        resetRadius()

        startX = x
        startY = y

        path.moveTo(x.toFloat(), y.toFloat())
        path.lineTo(x.toFloat(), y.toFloat())

        prevX = x
        prevY = y

        drawPath(path)
    }

    override fun onTouchMove(x: Int, y: Int) {
        if (path.isEmpty) {
            path.moveTo(prevX.toFloat(), prevY.toFloat())
        }
        if (x == startX && y == startY) {
            path.lineTo(x + 0.1f, y.toFloat())
        } else {
            path.quadTo(
                    prevX.toFloat(),
                    prevY.toFloat(),
                    ((x + prevX) / 2).toFloat(),
                    ((y + prevY) / 2).toFloat()
            )
        }

        prevX = x
        prevY = y

        drawPath(path)
    }

    override fun onTouchUp(x: Int, y: Int) {
        if (path.isEmpty) {
            path.moveTo(prevX.toFloat(), prevY.toFloat())
        }
        path.quadTo(prevX.toFloat(), prevY.toFloat(), x.toFloat(), y.toFloat())

        path.reset()

        drawPath(path)

        prevX = 0
        prevY = 0
    }

    override fun onDraw() {}

}

const val ERASER_COLOR = -0x1
