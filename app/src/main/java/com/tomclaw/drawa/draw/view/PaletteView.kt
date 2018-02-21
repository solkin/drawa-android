package com.tomclaw.drawa.draw.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


class PaletteView(context: Context, attributes: AttributeSet)
    : View(context, attributes) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var cellSize: Float = 0f
    private var padding: Float = 0f

    private val columns = 7
    private val rows = 3
    private val effectiveArea: Float = 0.75f

    init {
        paint.style = Paint.Style.FILL
        paint.color = Color.RED
    }

    override fun onDraw(canvas: Canvas) {
        for (y in 1..rows) {
            for (x in 1..columns) {
                canvas.drawCircle(
                        padding + x * cellSize - cellSize / 2,
                        padding + y * cellSize - cellSize / 2,
                        cellSize * 3 / 8,
                        paint
                )
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width: Float = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec).toFloat()
        cellSize = width / (columns + 1 - effectiveArea)
        padding = (width - cellSize * columns) / 2
        val height: Float = cellSize * rows + padding * 2
        setMeasuredDimension(width.toInt(), height.toInt())
    }
}