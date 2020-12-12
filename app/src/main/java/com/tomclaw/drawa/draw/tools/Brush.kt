package com.tomclaw.drawa.draw.tools

import android.graphics.Paint
import android.graphics.Path
import kotlin.math.abs
import kotlin.math.sqrt

class Brush : Tool() {

    private var startX: Int = 0
    private var startY: Int = 0
    private var prevX: Int = 0
    private var prevY: Int = 0
    private var path = Path()

    private var startStrokeSize: Float = 0.0f

    override val alpha = 0xff
    override val type = TYPE_BRUSH

    override fun initPaint() = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    override fun onTouchDown(x: Int, y: Int) {
        resetRadius()
        startStrokeSize = strokeSize

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
        path.quadTo(
                prevX.toFloat(),
                prevY.toFloat(),
                ((x + prevX).toFloat() / 2),
                ((y + prevY).toFloat() / 2)
        )

        val deltaX = abs(x - prevX)
        val deltaY = abs(y - prevY)
        val length = sqrt((deltaX * deltaX + deltaY * deltaY).toDouble())
        val sizeStep = defaultRadius / 20
        var size = strokeSize
        if (length * 5 < defaultRadius) {
            size += sizeStep

            path.reset()
            path.moveTo(prevX.toFloat(), prevY.toFloat())
            path.lineTo(x.toFloat(), y.toFloat())
        } else {
            size -= sizeStep
        }
        if (size > startStrokeSize / SIZE_MULTIPLIER && size < startStrokeSize * SIZE_MULTIPLIER) {
            strokeSize = size
        }

        prevX = x
        prevY = y

        drawPath(path)
    }

    override fun onTouchUp(x: Int, y: Int) {
        if (path.isEmpty) {
            path.moveTo(prevX.toFloat(), prevY.toFloat())
        }
        if (x == startX && y == startY) {
            path.lineTo(x + 0.1f, y.toFloat())
        } else {
            path.lineTo(x.toFloat(), y.toFloat())
        }

        drawPath(path)

        prevX = 0
        prevY = 0

        path.reset()
    }

    override fun onDraw() {}

}

private const val SIZE_MULTIPLIER = 2f
