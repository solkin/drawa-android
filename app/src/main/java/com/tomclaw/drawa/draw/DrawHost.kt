package com.tomclaw.drawa.draw

interface DrawHost : BitmapHost {

    fun getWidth(): Int

    fun invalidate()

}
