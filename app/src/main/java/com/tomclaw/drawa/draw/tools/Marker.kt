package com.tomclaw.drawa.draw.tools

import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import java.util.Random

class Marker : Tool() {

    private var startX: Int = 0
    private var startY: Int = 0
    private var prevX: Int = 0
    private var prevY: Int = 0
    private var path = Path()
    private var random = Random()

    override val alpha = 0x50
    override val type = TYPE_MARKER

    override fun initPaint() = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.MITER
        strokeCap = Paint.Cap.BUTT
        pathEffect = DashPathEffect(floatArrayOf(2f, 0f), 0f)
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
        path.lineTo(x.toFloat(), y.toFloat())

        prevX = x
        prevY = y

        drawPath(path)
    }

    override fun onTouchUp(x: Int, y: Int) {
        if (path.isEmpty) {
            path.moveTo(prevX.toFloat(), prevY.toFloat())
        }
        if (x == startX && y == startY) {
            for (c in 0..2) {
                path.lineTo(randomizeCoordinate(x).toFloat(), randomizeCoordinate(y).toFloat())
                drawPath(path)
            }
        } else {
            path.lineTo(x.toFloat(), y.toFloat())
        }

        drawPath(path)

        prevX = 0
        prevY = 0
    }

    override fun onDraw() = path.reset()

    private fun randomizeCoordinate(value: Int) =
            value + random.nextInt(DOT_RADIUS + 1) - DOT_RADIUS / 2

}

private const val DOT_RADIUS = 4
