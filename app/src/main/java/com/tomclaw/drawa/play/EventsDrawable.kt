package com.tomclaw.drawa.play

import com.tomclaw.drawa.draw.Event
import com.tomclaw.drawa.util.StreamDrawable

class EventsDrawable(
        decoder: EventsProvider,
        renderer: EventsRenderer
) : StreamDrawable<Event>(decoder, renderer)