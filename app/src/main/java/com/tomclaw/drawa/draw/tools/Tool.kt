package com.tomclaw.drawa.draw.tools

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import com.tomclaw.drawa.draw.DrawHost
import com.tomclaw.drawa.util.MetricsProvider

abstract class Tool {

    private lateinit var callback: DrawHost
    private lateinit var metricsProvider: MetricsProvider

    lateinit var paint: Paint
        private set

    var size: Int = SIZE_M

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

    var strokeSize: Int
        get() = paint.strokeWidth.toInt()
        set(strokeSize) {
            paint.strokeWidth = strokeSize.toFloat()
        }

    fun initialize(callback: DrawHost, metricsProvider: MetricsProvider) {
        this.callback = callback
        this.metricsProvider = metricsProvider
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
        val pixelSize = metricsProvider.convertDpToPixel(dp = size.toFloat()).toInt()
        strokeSize = pixelSize * callback.bitmap.width / callback.getWidth()
    }

}

const val TYPE_PENCIL = 1
const val TYPE_BRUSH = 2
const val TYPE_MARKER = 3
const val TYPE_FLUFFY = 4
const val TYPE_FILL = 5
const val TYPE_ERASER = 6
