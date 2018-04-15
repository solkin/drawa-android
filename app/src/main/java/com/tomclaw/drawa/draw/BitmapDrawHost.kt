package com.tomclaw.drawa.draw

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.tomclaw.drawa.core.BITMAP_HEIGHT
import com.tomclaw.drawa.core.BITMAP_WIDTH

class BitmapDrawHost(width: Int = BITMAP_WIDTH, height: Int = BITMAP_HEIGHT) : BitmapHost {

    private val hiddenBitmap: Bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.RGB_565
    )

    override val normalBitmap: Bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.RGB_565
    )

    private val hiddenCanvas: Canvas = Canvas(hiddenBitmap)
    private val normalCanvas: Canvas = Canvas(normalBitmap)

    override val canvas: Canvas
        get() = if (hidden) hiddenCanvas else normalCanvas
    override val bitmap: Bitmap
        get() = if (hidden) hiddenBitmap else normalBitmap

    override val src: Rect = Rect(0, 0, normalBitmap.width, normalBitmap.height)

    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
        isDither = true
    }

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

    override fun applyBitmap(bitmap: Bitmap) {
        val dst = this.src
        val src = Rect(0, 0, bitmap.width, bitmap.height)
        canvas.drawBitmap(bitmap, src, dst, paint)
    }

    override fun clearBitmap() {
        canvas.drawColor(Color.WHITE)
    }

}