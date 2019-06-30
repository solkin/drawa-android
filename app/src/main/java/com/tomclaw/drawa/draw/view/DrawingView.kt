package com.tomclaw.drawa.draw.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.tomclaw.drawa.draw.BitmapDrawHost
import com.tomclaw.drawa.draw.BitmapHost
import com.tomclaw.drawa.draw.DrawHost
import kotlin.math.min

class DrawingView(
        context: Context,
        attributeSet: AttributeSet
) : View(context, attributeSet), BitmapHost by BitmapDrawHost(), DrawHost {

    private var dst: Rect? = null

    override val paint: Paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        isFilterBitmap = true
    }

    var drawingListener: DrawingListener? = null

    override fun onDraw(canvas: Canvas) {
        if (dst == null) {
            dst = Rect(0, 0, width, height)
        }
        val dst = dst ?: return
        canvas.drawBitmap(normalBitmap, src, dst, paint)
        drawingListener?.onDraw()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val eventX = (bitmap.width * event.x / width).toInt()
        val eventY = (bitmap.height * event.y / height).toInt()
        drawingListener?.onTouchEvent(TouchEvent(eventX, eventY, event.action))
        invalidate()
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = min(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(size, size)
    }

}
