package com.tomclaw.drawa.draw.view

interface DrawingListener {

    fun onTouchEvent(eventX: Int, eventY: Int, action: Int)

    fun onDraw()

}