package com.tomclaw.drawa.draw

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

class BitmapDrawHost : BitmapHost {

    private val hiddenBitmap: Bitmap = Bitmap.createBitmap(
            BITMAP_WIDTH,
            BITMAP_HEIGHT,
            Bitmap.Config.ARGB_8888
    )

    override val normalBitmap: Bitmap = Bitmap.createBitmap(
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

    override val src: Rect = Rect(0, 0, bitmap.width, bitmap.height)

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

const val BITMAP_WIDTH = 720
const val BITMAP_HEIGHT = 720