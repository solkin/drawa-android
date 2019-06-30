package com.tomclaw.drawa.play

import com.tomclaw.drawa.core.BITMAP_HEIGHT
import com.tomclaw.drawa.core.BITMAP_WIDTH
import com.tomclaw.drawa.draw.Event
import com.tomclaw.drawa.util.StreamDecoder

class EventsProvider(
        private val events: Iterator<Event>
) : StreamDecoder<Event> {

    override fun getWidth(): Int = BITMAP_WIDTH

    override fun getHeight(): Int = BITMAP_HEIGHT

    override fun hasFrame(): Boolean = events.hasNext()

    override fun readFrame(): Event? = events.next()

    override fun getDelay(): Int = 100

    override fun stop() {}

}