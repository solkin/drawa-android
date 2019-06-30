package com.tomclaw.drawa.util

interface StreamDecoder {

    fun getWidth(): Int

    fun getHeight(): Int

    fun hasFrame(): Boolean

    fun readFrame(): Frame?

    fun getDelay(): Int

    fun stop()

    interface Frame

}