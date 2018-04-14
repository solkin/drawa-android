package com.tomclaw.drawa.share.plugin

import android.view.MotionEvent
import com.tomclaw.drawa.R
import com.tomclaw.drawa.draw.DrawHost
import com.tomclaw.drawa.draw.Event
import com.tomclaw.drawa.draw.History
import com.tomclaw.drawa.draw.ToolProvider
import com.tomclaw.drawa.draw.view.BITMAP_HEIGHT
import com.tomclaw.drawa.draw.view.BITMAP_WIDTH
import com.tomclaw.drawa.share.SharePlugin
import com.tomclaw.drawa.util.MetricsProvider
import com.waynejo.androidndkgif.GifEncoder
import com.waynejo.androidndkgif.GifEncoder.EncodingType
import io.reactivex.Single
import java.io.File

class AnimSharePlugin(
        private val toolProvider: ToolProvider,
        private val metricsProvider: MetricsProvider,
        private val history: History,
        private val drawHost: DrawHost,
        private val outputDirectory: File
) : SharePlugin {

    init {
        toolProvider.listTools().forEach { it.initialize(drawHost, metricsProvider) }
    }

    override val image: Int
        get() = R.drawable.animation
    override val title: Int
        get() = R.string.anim_share_title
    override val description: Int
        get() = R.string.anim_share_description

    override val operation: Single<File> = Single.create { emitter ->
        outputDirectory.mkdirs()
        val file: File = createTempFile("anim", ".gif", outputDirectory).apply {
            deleteOnExit()
        }
        applyHistory(file)
        emitter.onSuccess(file)
    }

    private fun applyHistory(file: File) {
        /*var stream: OutputStream? = null
        try {
            stream = FileOutputStream(file)
            val encoder = AnimatedGifEncoder().apply {
                setDelay(100)
                start(stream)
            }
            drawHost.clearBitmap()
            history.getEvents().forEach {
                processToolEvent(it)
                if (it.action == MotionEvent.ACTION_UP) {
                    encoder.setDelay(100)
                    encoder.addFrame(drawHost.bitmap)
                }
            }
            encoder.finish()
        } finally {
            stream.safeClose()
        }*/

        val encoder = GifEncoder()
        try {
            encoder.init(
                    drawHost.bitmap.width,
                    drawHost.bitmap.height,
                    file.path,
                    EncodingType.ENCODING_TYPE_FAST
            )
            drawHost.clearBitmap()
            history.getEvents().forEach {
                processToolEvent(it)
                if (it.action == MotionEvent.ACTION_UP) {
                    encoder.encodeFrame(drawHost.bitmap, 100)
                }
            }
        } finally {
            encoder.close()
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