package com.tomclaw.drawa.draw.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.tomclaw.drawa.R


class PaletteView(context: Context, attributes: AttributeSet)
    : View(context, attributes) {

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var cellSize: Float = 0f
    private var padding: Float = 0f

    private val columns = 7
    private val rows = 3
    private val effectiveArea: Float = 0.75f

    private val palette = ArrayList<Int>()
    private var shadowAlpha: Int = 0x55

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
            if (styledAttrs != null) {
                val paletteResId = styledAttrs.getResourceId(R.styleable.PaletteView_palette, 0)
                if (paletteResId != 0) {
                    paletteArray = context.resources.obtainTypedArray(paletteResId)
                    (0 until paletteArray.length()).mapTo(palette) {
                        paletteArray.getColor(it, 0)
                    }
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
        setLayerType(LAYER_TYPE_SOFTWARE, shadowPaint)
    }

    override fun onDraw(canvas: Canvas) {
        var position = 0
        for (y in 1..rows) {
            for (x in 1..columns) {
                val shadowColor = shadowAlpha shl 24 or (palette[position].darker() and 0x00ffffff)
                shadowPaint.color = shadowColor
                shadowPaint.setShadowLayer(padding / 2, 0f, padding / 2, shadowColor)
                canvas.drawCircle(
                        padding + x * cellSize - cellSize / 2,
                        padding + y * cellSize - cellSize / 2,
                        cellSize * 3 / 8,
                        shadowPaint
                )
                fillPaint.color = palette[position]
                setLayerType(LAYER_TYPE_SOFTWARE, fillPaint)
                canvas.drawCircle(
                        padding + x * cellSize - cellSize / 2,
                        padding + y * cellSize - cellSize / 2,
                        cellSize * 3 / 8,
                        fillPaint
                )
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

    private fun Int.darker(): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(this, hsv)
        hsv[2] *= 0.8f
        return Color.HSVToColor(hsv)
    }
}