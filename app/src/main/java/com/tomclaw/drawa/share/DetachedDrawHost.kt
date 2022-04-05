package com.tomclaw.drawa.share

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.tomclaw.drawa.draw.BitmapDrawHost
import com.tomclaw.drawa.draw.BitmapHost
import com.tomclaw.drawa.draw.DrawHost

class DetachedDrawHost(width: Int, height: Int) : DrawHost, BitmapHost by BitmapDrawHost() {

    private var dst: Rect = Rect(0, 0, width, height)

    override val paint: Paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        isFilterBitmap = true
    }

    override val bitmap: Bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
    )

    override val canvas: Canvas = Canvas(bitmap)

    override fun invalidate() {
        canvas.drawBitmap(normalBitmap, src, dst, paint)
    }

    override fun clearBitmap() {
        canvas.drawColor(Color.TRANSPARENT)
    }

}
