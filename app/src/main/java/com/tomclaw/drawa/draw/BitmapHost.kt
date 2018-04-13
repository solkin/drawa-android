package com.tomclaw.drawa.draw

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect

interface BitmapHost {

    val bitmap: Bitmap

    val normalBitmap: Bitmap

    val src: Rect

    val canvas: Canvas

    var hidden: Boolean

    fun applyBitmap(bitmap: Bitmap)

    fun clearBitmap()

}