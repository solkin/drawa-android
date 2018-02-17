package com.tomclaw.drawa.draw.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.tomclaw.drawa.draw.DrawHost

class DrawingView(context: Context,
                  attributeSet: AttributeSet)
    : View(context, attributeSet), DrawHost {

    override var bitmap: Bitmap? = null
    override var canvas: Canvas? = null

    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
        isDither = true
    }

    private var src: Rect? = null
    private var dst: Rect? = null

    var drawingListener: DrawingListener? = null

    override fun onDraw(canvas: Canvas) {
        if (bitmap == null) {
            initBitmap()
        }
        canvas.drawBitmap(bitmap, src, dst, paint)
        drawingListener?.onDraw()
    }

    private fun initBitmap() {
        val bitmap = Bitmap.createBitmap(
                (width / SCALE_FACTOR).toInt(),
                (height / SCALE_FACTOR).toInt(),
                Bitmap.Config.ARGB_8888
        )
        canvas = Canvas(bitmap)
        canvas?.drawColor(Color.WHITE)
        src = Rect(0, 0, bitmap.width, bitmap.height)
        dst = Rect(0, 0, width, height)
        this.bitmap = bitmap
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val eventX = (event.x / SCALE_FACTOR).toInt()
        val eventY = (event.y / SCALE_FACTOR).toInt()
        drawingListener?.onTouchEvent(eventX, eventY, event.action)
        invalidate()
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) =
            super.onMeasure(widthMeasureSpec, widthMeasureSpec)

}

const val SCALE_FACTOR = 1.0f
