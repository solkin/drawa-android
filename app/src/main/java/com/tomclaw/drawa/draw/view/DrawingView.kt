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

    override val bitmap: Bitmap = Bitmap.createBitmap(
            BITMAP_WIDTH,
            BITMAP_HEIGHT,
            Bitmap.Config.ARGB_8888
    )
    override val canvas: Canvas

    private var src: Rect
    private var dst: Rect? = null

    init {
        canvas = Canvas(bitmap)
        clearBitmap()
        src = Rect(0, 0, bitmap.width, bitmap.height)
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
        canvas.drawBitmap(bitmap, src, dst, paint)
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
        val src = Rect(0, 0, bitmap.width, bitmap.height)
        val dst = Rect(0, 0, this.bitmap.width, this.bitmap.height)
        canvas.drawBitmap(bitmap, src, dst, paint)
    }

    override fun clearBitmap() {
        canvas.drawColor(Color.WHITE)
    }
}

const val BITMAP_WIDTH = 720
const val BITMAP_HEIGHT = 720
