package com.tomclaw.drawa.draw.tools

import android.graphics.Paint
import com.tomclaw.drawa.util.QueueLinearFloodFiller

class Fill : Tool() {

    override val alpha = 0xff
    override val type = TYPE_FILL

    override fun initPaint() = Paint().apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    override fun onTouchDown(x: Int, y: Int) {
        val color = color
        val pixel = bitmap.getPixel(x, y)
        QueueLinearFloodFiller(bitmap, pixel, color).run {
            setTolerance(COLOR_DELTA)
            floodFill(x, y)
        }
    }

    override fun onTouchUp(x: Int, y: Int) {}

    override fun onDraw() {}

    override fun onTouchMove(x: Int, y: Int) {}

}

private const val COLOR_DELTA = 0x32
