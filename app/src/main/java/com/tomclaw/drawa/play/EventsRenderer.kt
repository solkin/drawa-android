package com.tomclaw.drawa.play

import android.view.MotionEvent
import com.tomclaw.drawa.core.BITMAP_HEIGHT
import com.tomclaw.drawa.core.BITMAP_WIDTH
import com.tomclaw.drawa.draw.DrawHost
import com.tomclaw.drawa.draw.Event
import com.tomclaw.drawa.draw.ToolProvider
import com.tomclaw.drawa.util.MetricsProvider
import com.tomclaw.drawa.util.StreamRenderer

class EventsRenderer(
        private val toolProvider: ToolProvider,
        private val metricsProvider: MetricsProvider,
        private val drawHost: DrawHost
) : StreamRenderer<Event> {

    init {
        toolProvider.listTools().forEach { it.initialize(drawHost, metricsProvider) }
    }

    override fun render(frame: Event) {
        processToolEvent(frame)
    }

    private fun processToolEvent(event: Event) {
        val tool = toolProvider.getTool(event.toolType)
        val x = (event.x * drawHost.bitmap.width / BITMAP_WIDTH)
        val y = (event.y * drawHost.bitmap.height / BITMAP_HEIGHT)
        with(tool) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    color = event.color
                    size = event.size
                    onTouchDown(x, y)
                }
                MotionEvent.ACTION_MOVE -> onTouchMove(x, y)
                MotionEvent.ACTION_UP -> onTouchUp(x, y)
            }
            onDraw()
        }
    }

}