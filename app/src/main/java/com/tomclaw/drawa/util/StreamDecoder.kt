package com.tomclaw.drawa.util

interface StreamDecoder {

    fun getWidth(): Int

    fun getHeight(): Int

    fun hasFrame(): Boolean

    fun readFrame(): IntArray?

    fun getDelay(): Int

    fun stop()

}