package com.tomclaw.drawa.draw.tools

import android.graphics.*
import com.tomclaw.drawa.draw.DrawHost

abstract class Tool {

    private lateinit var callback: DrawHost

    lateinit var paint: Paint
        private set

    var baseRadius: Int = 0
        set(value) {
            if (this.baseRadius != value) {
                field = value
                radius = value
            }
        }

    abstract val alpha: Int

    open var color: Int
        get() {
            val color = paint.color
            return Color.rgb(Color.red(color), Color.green(color), Color.blue(color))
        }
        set(color) {
            paint.color = -0x1
            paint.color = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
        }

    val bitmap: Bitmap
        get() = callback.bitmap

    val canvas: Canvas
        get() = callback.canvas

    abstract val type: Int

    var radius: Int
        get() = paint.strokeWidth.toInt()
        set(radius) {
            paint.strokeWidth = radius.toFloat()
        }

    fun initialize(callback: DrawHost) {
        this.callback = callback
        this.paint = initPaint()
    }

    abstract fun initPaint(): Paint

    abstract fun onTouchDown(x: Int, y: Int)

    abstract fun onTouchMove(x: Int, y: Int)

    abstract fun onTouchUp(x: Int, y: Int)

    abstract fun onDraw()

    fun drawPath(path: Path) {
        canvas.drawPath(path, paint)
    }

    fun resetRadius() {
        val baseRadius = baseRadius
        radius = baseRadius
    }

}

const val TYPE_PENCIL = 1
const val TYPE_BRUSH = 2
const val TYPE_MARKER = 3
const val TYPE_FLUFFY = 4
const val TYPE_FILL = 5
const val TYPE_ERASER = 6
