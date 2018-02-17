package com.tomclaw.drawa.draw.view

interface DrawingListener {

    fun onTouchEvent(event: TouchEvent)

    fun onDraw()

}