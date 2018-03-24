package com.tomclaw.drawa.draw.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.tomclaw.drawa.draw.DrawHost

class DrawingView(context: Context,
                  attributeSet: AttributeSet)
    : View(context, attributeSet), DrawHost {

    private val hiddenBitmap: Bitmap = Bitmap.createBitmap(
            BITMAP_WIDTH,
            BITMAP_HEIGHT,
            Bitmap.Config.ARGB_8888
    )

    private val normalBitmap: Bitmap = Bitmap.createBitmap(
            BITMAP_WIDTH,
            BITMAP_HEIGHT,
            Bitmap.Config.ARGB_8888
    )

    private val hiddenCanvas: Canvas = Canvas(hiddenBitmap)
    private val normalCanvas: Canvas = Canvas(normalBitmap)

    override val canvas: Canvas
        get() = if (hidden) hiddenCanvas else normalCanvas
    override val bitmap: Bitmap
        get() = if (hidden) hiddenBitmap else normalBitmap

    private var src: Rect = Rect(0, 0, bitmap.width, bitmap.height)
    private var dst: Rect? = null

    override var hidden = false
        set(value) {
            if (!value) {
                normalCanvas.drawBitmap(hiddenBitmap, src, src, paint)
            }
            field = value
        }

    init {
        clearBitmap()
    }

    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
        isDither = true
    }

    var drawingListener: DrawingListener? = null

    override fun onDraw(canvas: Canvas) {
        if (dst == null) {
            dst = Rect(0, 0, width, height)
        }
        canvas.drawBitmap(normalBitmap, src, dst, paint)
        drawingListener?.onDraw()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val eventX = (BITMAP_WIDTH * event.x / width).toInt()
        val eventY = (BITMAP_HEIGHT * event.y / height).toInt()
        drawingListener?.onTouchEvent(TouchEvent(eventX, eventY, event.action))
        invalidate()
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = Math.min(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(size, size)
    }

    override fun applyBitmap(bitmap: Bitmap) {
        val dst = this.src
        val src = Rect(0, 0, bitmap.width, bitmap.height)
        canvas.drawBitmap(bitmap, src, dst, paint)
    }

    override fun clearBitmap() {
        canvas.drawColor(Color.WHITE)
    }

}

const val BITMAP_WIDTH = 720
const val BITMAP_HEIGHT = 720
