package com.tomclaw.drawa.share.plugin

import android.view.MotionEvent
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.cache.DiskLruCache
import com.tomclaw.drawa.R
import com.tomclaw.drawa.core.BITMAP_HEIGHT
import com.tomclaw.drawa.core.BITMAP_WIDTH
import com.tomclaw.drawa.core.Journal
import com.tomclaw.drawa.draw.DrawHost
import com.tomclaw.drawa.draw.Event
import com.tomclaw.drawa.draw.History
import com.tomclaw.drawa.draw.ToolProvider
import com.tomclaw.drawa.gif.GifEncoder
import com.tomclaw.drawa.share.SharePlugin
import com.tomclaw.drawa.share.ShareResult
import com.tomclaw.drawa.util.MetricsProvider
import com.tomclaw.drawa.util.safeClose
import com.tomclaw.drawa.util.uniqueKey
import io.reactivex.Observable
import io.reactivex.Single
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class AnimSharePlugin(
        private val recordId: Int,
        private val toolProvider: ToolProvider,
        private val metricsProvider: MetricsProvider,
        private val journal: Journal,
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

    override val progress: Observable<Float>
        get() = progressRelay
    private val progressRelay = PublishRelay.create<Float>()

    override val operation: Single<ShareResult> = journal.load()
            .map { journal.get(recordId) }
            .flatMap { record ->
                val key = "anim-${record.uniqueKey()}"
                val cached = cache.get(key)
                val result = when {
                    cached != null -> {
                        updateProgress(value = 1f)
                        Single.just(ShareResult(cached, MIME_TYPE))
                    }
                    else -> Single.create { emitter ->
                        val animFile: File = createTempFile("anim", ".gif")
                        applyHistory(animFile)
                        val file = cache.put(key, animFile)
                        emitter.onSuccess(ShareResult(file, MIME_TYPE))
                    }
                }
                result
            }

    private fun applyHistory(file: File) {
        var stream: OutputStream? = null
        try {
            stream = FileOutputStream(file)
            val encoder = GifEncoder().apply {
                setQuality(15)
                start(stream)
                setRepeat(1)
            }
            drawHost.clearBitmap()
            val totalEventsCount = history.getEventsCount()
            var eventCount = 0
            history.getEvents().forEach { event ->
                processToolEvent(event)
                eventCount++
                if ((eventCount % 10) == 0 || event.action == MotionEvent.ACTION_UP) {
                    encoder.setDelay(100)
                    encoder.addFrame(drawHost.bitmap)
                    updateProgress(value = eventCount.toFloat() / totalEventsCount.toFloat())
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

    private fun updateProgress(value: Float) {
        progressRelay.accept(value)
    }

}

private const val MIME_TYPE = "image/gif"