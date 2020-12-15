package com.tomclaw.drawa.draw.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.View
import com.tomclaw.drawa.R

class PaletteView(context: Context, attributes: AttributeSet) : View(context, attributes) {

    private val fillPaint = Paint(ANTI_ALIAS_FLAG)
    private val shadowPaint = Paint(ANTI_ALIAS_FLAG)

    private var cellSize: Float = 0f
    private var padding: Float = 0f

    private val columns = 7
    private val rows = 3
    private val effectiveArea: Float = 0.75f

    private val palette = ArrayList<Int>()
    private var shadowAlpha: Int = 0x55

    private var activeX: Float = -1f
    private var activeY: Float = -1f

    var colorClickListener: OnColorClickListener? = null

    init {
        var styledAttrs: TypedArray? = null
        var paletteArray: TypedArray? = null
        try {
            styledAttrs = context.theme.obtainStyledAttributes(
                    attributes,
                    R.styleable.PaletteView,
                    0,
                    0
            )
            val paletteResId = styledAttrs.getResourceId(R.styleable.PaletteView_palette, 0)
            if (paletteResId != 0) {
                paletteArray = context.resources.obtainTypedArray(paletteResId)
                (0 until paletteArray.length()).mapTo(palette) {
                    paletteArray.getColor(it, 0)
                }
            }
        } finally {
            styledAttrs?.recycle()
            paletteArray?.recycle()
        }

        fillPaint.style = Paint.Style.FILL
        fillPaint.isAntiAlias = true

        shadowPaint.style = Paint.Style.FILL
        shadowPaint.isAntiAlias = true

        setLayerType(LAYER_TYPE_SOFTWARE, fillPaint)
    }

    override fun onDraw(canvas: Canvas) {
        var position = 0
        for (y in 1..rows) {
            for (x in 1..columns) {
                val cellX = padding + (x - 1) * cellSize
                val cellY = padding + (y - 1) * cellSize
                val drawX = cellX + cellSize / 2
                val drawY = cellY + cellSize / 2
                val radius = cellSize * 3 / 8
                val isActive = cellX <= activeX && cellY <= activeY
                        && (cellX + cellSize) > activeX && (cellY + cellSize) > activeY
                val color = if (isActive) palette[position].darker() else palette[position]
                val shadowColor = shadowAlpha shl 24 or (color.darker() and 0x00ffffff)
                shadowPaint.color = shadowColor
                shadowPaint.setShadowLayer(padding / 2, 0f, padding / 2, shadowColor)
                canvas.drawCircle(drawX, drawY, radius, shadowPaint)
                fillPaint.color = color
                canvas.drawCircle(drawX, drawY, radius, fillPaint)
                position++
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

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            ACTION_DOWN -> onTouchDown(event.x, event.y)
            ACTION_MOVE -> onTouchMove(event.x, event.y)
            ACTION_UP -> onTouchUp(event.x, event.y)
        }
        return true
    }

    private fun onTouchDown(x: Float, y: Float) {
        this.activeX = x
        this.activeY = y
        invalidate()
    }

    private fun onTouchMove(x: Float, y: Float) {
        this.activeX = x
        this.activeY = y
        invalidate()
    }

    private fun onTouchUp(x: Float, y: Float) {
        val column = ((x - padding) / cellSize).toInt()
        val row = ((y - padding) / cellSize).toInt()
        if (column in 0 until columns && row in 0 until rows) {
            val color = palette[row * columns + column]
            colorClickListener?.onColorClicked(color)
        }
        this.activeX = -1f
        this.activeY = -1f
        invalidate()
    }

    private fun Int.darker(): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(this, hsv)
        hsv[2] *= 0.8f
        return Color.HSVToColor(hsv)
    }


    interface OnColorClickListener {

        fun onColorClicked(color: Int)

    }

}