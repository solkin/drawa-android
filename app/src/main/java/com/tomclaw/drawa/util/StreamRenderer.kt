package com.tomclaw.drawa.util

import android.graphics.Bitmap

interface StreamRenderer<F> {

    fun render(bitmap: Bitmap, frame: F)

}