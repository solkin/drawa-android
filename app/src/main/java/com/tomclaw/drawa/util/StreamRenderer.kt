package com.tomclaw.drawa.util

import android.graphics.Bitmap

interface StreamRenderer {

    fun render(bitmap: Bitmap, frame: StreamDecoder.Frame)

}