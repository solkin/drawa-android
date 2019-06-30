package com.tomclaw.drawa.play

interface DrawaDecoder {

    fun getWidth(): Int

    fun getHeight(): Int

    fun hasFrame(): Boolean

    fun readFrame(): IntArray?

    fun getDelay(): Int

    fun stop()

}