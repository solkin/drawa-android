package com.tomclaw.drawa.share.plugin

import android.view.MotionEvent
import com.tomclaw.cache.DiskLruCache
import com.tomclaw.drawa.R
import com.tomclaw.drawa.core.BITMAP_HEIGHT
import com.tomclaw.drawa.core.BITMAP_WIDTH
import com.tomclaw.drawa.draw.DrawHost
import com.tomclaw.drawa.draw.Event
import com.tomclaw.drawa.draw.History
import com.tomclaw.drawa.draw.ToolProvider
import com.tomclaw.drawa.gif.GifEncoder
import com.tomclaw.drawa.share.SharePlugin
import com.tomclaw.drawa.share.ShareResult
import com.tomclaw.drawa.util.MetricsProvider
import com.tomclaw.drawa.util.safeClose
import io.reactivex.Single
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class AnimSharePlugin(
        private val toolProvider: ToolProvider,
        private val metricsProvider: MetricsProvider,
        private val history: History,
        private val drawHost: DrawHost,
        private val cache: DiskLruCache
) : SharePlugin {

    init {
        toolProvider.listTools().forEach { it.initialize(drawHost, metricsProvider) }
    }

    override val weight: Int
        get() = 2
    override val image: Int
        get() = R.drawable.animation
    override val title: Int
        get() = R.string.anim_share_title
    override val description: Int
        get() = R.string.anim_share_description

    override val operation: Single<ShareResult> = Single.create { emitter ->
        val animFile: File = createTempFile("anim", ".gif")
        applyHistory(animFile)
        val key = animFile.absolutePath
        val file = cache.put(key, animFile)
        emitter.onSuccess(ShareResult(file, "image/gif"))
    }

    private fun applyHistory(file: File) {
        var stream: OutputStream? = null
        try {
            stream = FileOutputStream(file)
            val encoder = GifEncoder().apply {
                start(stream)
                setRepeat(0)
            }
            drawHost.clearBitmap()
            var eventCount = 0
            history.getEvents().forEach { event ->
                processToolEvent(event)
                eventCount++
                if ((eventCount % 10) == 0 || event.action == MotionEvent.ACTION_UP) {
                    encoder.setDelay(100)
                    encoder.addFrame(drawHost.bitmap)
                }
            }
            encoder.finish()
        } finally {
            stream.safeClose()
        }
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