package com.tomclaw.drawa.util

interface StreamDecoder<F> {

    fun getWidth(): Int

    fun getHeight(): Int

    fun hasFrame(): Boolean

    fun readFrame(): F?

    fun getDelay(): Int

    fun stop()

}