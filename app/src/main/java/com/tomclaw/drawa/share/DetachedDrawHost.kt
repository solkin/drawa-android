package com.tomclaw.drawa.share

import com.tomclaw.drawa.draw.BitmapDrawHost
import com.tomclaw.drawa.draw.BitmapHost
import com.tomclaw.drawa.draw.DrawHost

class DetachedDrawHost : DrawHost, BitmapHost by BitmapDrawHost() {

    override fun getWidth(): Int = normalBitmap.width

    override fun invalidate() {}

}