package com.tomclaw.drawa.share

import com.tomclaw.drawa.draw.BitmapDrawHost
import com.tomclaw.drawa.draw.BitmapHost
import com.tomclaw.drawa.draw.DrawHost

class DetachedDrawHost : DrawHost, BitmapHost by BitmapDrawHost(BITMAP_WIDTH, BITMAP_HEIGHT) {

    override fun invalidate() {}

}

const val BITMAP_WIDTH = 256
const val BITMAP_HEIGHT = 256