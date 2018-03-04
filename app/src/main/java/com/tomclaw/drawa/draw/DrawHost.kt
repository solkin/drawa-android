package com.tomclaw.drawa.draw

import android.graphics.Bitmap
import android.graphics.Canvas

interface DrawHost {

    val bitmap: Bitmap

    val canvas: Canvas

    fun applyBitmap(bitmap: Bitmap)

    fun clearBitmap()

    fun invalidate()

}
