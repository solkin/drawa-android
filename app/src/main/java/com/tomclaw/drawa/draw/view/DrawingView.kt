package com.tomclaw.drawa.draw.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.tomclaw.drawa.draw.DrawHost
import com.tomclaw.drawa.draw.Event
import com.tomclaw.drawa.draw.History
import com.tomclaw.drawa.draw.ToolProvider
import com.tomclaw.drawa.draw.tools.Tool

class DrawingView(context: Context,
                  attributeSet: AttributeSet)
    : View(context, attributeSet), DrawHost {

    override var bitmap: Bitmap? = null
    private lateinit var canvas: Canvas
    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
        isDither = true
    }

    private lateinit var history: History
    private lateinit var toolProvider: ToolProvider

    private var tool: Tool? = null
    private var src: Rect? = null
    private var dst: Rect? = null

    private var selectedColor = 0xcd0219
    private var selectedRadius = BASE_RADIUS / SCALE_FACTOR

    fun init(history: History,
             toolProvider: ToolProvider) {
        this.history = history
        this.toolProvider = toolProvider
    }

    override fun onDraw(canvas: Canvas) {
        if (bitmap == null) {
            initBitmap()
        }
        canvas.drawBitmap(bitmap, src, dst, paint)
        tool?.onDraw()
    }

    private fun initBitmap() {
        val bitmap = Bitmap.createBitmap(
                (width / SCALE_FACTOR).toInt(),
                (height / SCALE_FACTOR).toInt(),
                Bitmap.Config.ARGB_8888
        )
        canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        src = Rect(0, 0, bitmap.width, bitmap.height)
        dst = Rect(0, 0, width, height)
        this.bitmap = bitmap
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val tool = this.tool ?: return false
        val eventX = (event.x / SCALE_FACTOR).toInt()
        val eventY = (event.y / SCALE_FACTOR).toInt()
        val e = history.add(tool, eventX, eventY, event.action)
        processToolEvent(e)
        invalidate()
        return true
    }

    private fun processToolEvent(event: Event) {
        val tool = toolProvider.getTool(event.toolType)
        tool.initialize(canvas, this)
        val x = event.x
        val y = event.y
        with(tool) {
            color = event.color
            baseRadius = event.radius
            when (event.action) {
                MotionEvent.ACTION_DOWN -> onTouchDown(x, y)
                MotionEvent.ACTION_MOVE -> onTouchMove(x, y)
                MotionEvent.ACTION_UP -> onTouchUp(x, y)
            }
            onDraw()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

}

private const val BASE_RADIUS = 60
private const val SCALE_FACTOR = 1.0f
