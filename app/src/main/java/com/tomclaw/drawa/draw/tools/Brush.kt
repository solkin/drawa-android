package com.tomclaw.drawa.draw.tools

import android.graphics.Paint
import android.graphics.Path

class Brush : Tool() {

    private var startX: Int = 0
    private var startY: Int = 0
    private var prevX: Int = 0
    private var prevY: Int = 0
    private var path = Path()

    override val alpha = 0xff
    override val type = TYPE_BRUSH

    override fun initPaint() = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    override fun onTouchDown(x: Int, y: Int) {
        startX = x
        startY = y
        resetRadius()

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
        path.quadTo(prevX.toFloat(), prevY.toFloat(), ((x + prevX) / 2).toFloat(), ((y + prevY) / 2).toFloat())

        val deltaX = Math.abs(x - prevX)
        val deltaY = Math.abs(y - prevY)
        val length = Math.sqrt((deltaX * deltaX + deltaY * deltaY).toDouble())
        var r = radius
        if (length < baseRadius / 5) {
            r += 2

            path.reset()
            path.moveTo(prevX.toFloat(), prevY.toFloat())
            path.lineTo(x.toFloat(), y.toFloat())
        } else {
            r -= 2
        }
        if (r > baseRadius / RADIUS_MULTIPLIER && r < baseRadius * RADIUS_MULTIPLIER) {
            radius = r
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

private const val RADIUS_MULTIPLIER = 2f
