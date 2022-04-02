package com.tomclaw.drawa.draw.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.tomclaw.drawa.R
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
        drawTransparency(canvas)
        canvas.drawBitmap(normalBitmap, src, dst, paint)

        drawingListener?.onDraw()
    }

    private fun drawTransparency(canvas: Canvas) {
        canvas.drawColor(resources.getColor(R.color.transparent_chess_light))
        paint.color = resources.getColor(R.color.transparent_chess_dark)

        val size = resources.getDimensionPixelSize(R.dimen.transparent_chess_size)

        val colCount = width / size + 1
        val rowCount = height / size + 1

        for (vrt in 0 until colCount step 1) {
            val start = vrt % 2
            for (hrz in start until rowCount step 2) {
                val hrzPxl = size * hrz.toFloat()
                val vrtPxl = size * vrt.toFloat()
                canvas.drawRect(hrzPxl, vrtPxl, hrzPxl + size, vrtPxl + size, paint)
            }
        }
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
