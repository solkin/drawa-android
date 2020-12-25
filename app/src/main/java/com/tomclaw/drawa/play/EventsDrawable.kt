package com.tomclaw.drawa.play

import com.tomclaw.drawa.draw.DrawHost
import com.tomclaw.drawa.draw.Event
import com.tomclaw.drawa.util.StreamDrawable

class EventsDrawable(
        drawHost: DrawHost,
        decoder: EventsProvider,
        renderer: EventsRenderer
) : StreamDrawable<Event>(drawHost.bitmap, drawHost.paint, decoder, renderer)
