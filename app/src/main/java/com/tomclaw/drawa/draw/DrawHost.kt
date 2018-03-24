package com.tomclaw.drawa.draw

import android.graphics.Bitmap
import android.graphics.Canvas

interface DrawHost {

    val bitmap: Bitmap

    val canvas: Canvas

    var hidden: Boolean

    fun getWidth(): Int

    fun applyBitmap(bitmap: Bitmap)

    fun clearBitmap()

    fun invalidate()

}
