package com.tomclaw.drawa.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.util.LongSparseArray
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.concurrent.Semaphore
import kotlin.math.max


@Suppress("MemberVisibilityCanBePrivate", "unused")
open class StreamDrawable<F>(
        private val bitmap: Bitmap,
        private val paint: Paint,
        private val decoder: StreamDecoder<F>,
        private val renderer: StreamRenderer<F>
) : Drawable(), Animatable {

    private val matrix = Matrix()

    fun getAnimState(): Int {
        return getState(this)
    }

    fun play() {
        when (getAnimState()) {
            STATE_PLAYING -> log(INFO, "already playing")
            STATE_STOPPED -> start(this)
            STATE_PAUSED -> resume(this)
        }
    }

    fun pause() {
        if (getAnimState() == STATE_PLAYING) {
            pause(this)
        } else {
            Log.w(TAG, "can't pause")
        }
    }

    override fun stop() {
        if (getAnimState() == STATE_PLAYING || getAnimState() == STATE_PAUSED) {
            stop(this)
        } else {
            Log.w(TAG, "can't stop")
        }
    }

    // Static dispatcher methods

    private val threads = LongSparseArray<ThreadInfo<F>>()
    private val mainHandler: Handler

    private class ThreadInfo<F>(
            val drawable: WeakReference<StreamDrawable<F>>,
            val pause: Semaphore = Semaphore(1),
            var paused: Boolean = false
    )

    private class ThreadParam(
            var threadId: Long = 0,
            var bitmap: Bitmap? = null
    )

    init {
        mainHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                mainThread(msg.what, msg.obj as ThreadParam)
            }
        }
    }

    @Synchronized
    private fun start(view: StreamDrawable<F>) {
        log(INFO, "start")

        val thread = Thread(Runnable { backgroundThread() })

        val info = ThreadInfo(drawable = WeakReference(view))
        threads.put(thread.id, info)

        thread.start()
    }

    @Synchronized
    private fun stop(view: StreamDrawable<F>) {
        log(INFO, "stop")
        val info = getThreadInfo(view)
        if (info != null) {
            stopThread(info)
        }
    }

    @Synchronized
    fun stopAll() {
        for (i in 0 until threads.size()) {
            stopThread(threads.valueAt(i))
        }
    }

    private fun stopThread(info: ThreadInfo<F>) {
        info.drawable.clear()
        if (info.paused) {
            info.pause.release()
            info.paused = false
        }
    }

    @Synchronized
    private fun pause(view: StreamDrawable<F>) {
        log(INFO, "pause")
        val info = getThreadInfo(view)
        if (info != null && !info.paused) {
            try {
                info.pause.acquire()
            } catch (ex: InterruptedException) {
            }

            info.paused = true
        }
    }

    @Synchronized
    private fun resume(view: StreamDrawable<F>) {
        log(INFO, "resume")
        val info = getThreadInfo(view)
        if (info != null && info.paused) {
            info.pause.release()
            info.paused = false
        }
    }

    @Synchronized
    private fun getState(view: StreamDrawable<F>): Int {
        val info = getThreadInfo(view) ?: return STATE_STOPPED
        return if (info.paused) STATE_PAUSED else STATE_PLAYING
    }

    @Synchronized
    private fun getThreadInfo(view: StreamDrawable<F>): ThreadInfo<F>? {
        for (i in 0 until threads.size()) {
            val info = threads.valueAt(i)
            val threadView = info.drawable.get()
            if (view == threadView) {
                return info
            }
        }
        return null
    }

    @Synchronized
    private fun getThreadInfo(threadId: Long): ThreadInfo<F>? {
        return threads.get(threadId)
    }

    @Synchronized
    private fun removeThread(threadId: Long) {
        threads.remove(threadId)
    }

    private fun backgroundThread() {
        val threadId = Thread.currentThread().id
        val info = getThreadInfo(threadId) ?: return

        log(DEBUG, "started thread $threadId")

        val startTime = System.currentTimeMillis()
        var infoTime = startTime + 10 * 1000
        var delay = 0

        val drawable = info.drawable.get()
        if (drawable != null) {
            val decoder = drawable.decoder
            try {
                while (decoder.hasFrame()) {
                    // decode frame
                    val frameStart = System.currentTimeMillis()

                    val frame = decoder.readFrame()
                    if (frame == null) {
                        log(DEBUG, "null frame, stopping")
                        break
                    }

                    val decodeTime = System.currentTimeMillis() - frameStart

                    log(VERBOSE, "decoded frame in $decodeTime delay $delay")

                    // wait until the end of delay set by previous frame
                    Thread.sleep(max(0, delay - decodeTime))

                    // check for pause
                    info.pause.acquire()
                    info.pause.release()

                    // check if drawable still exists
                    if (info.drawable.get() == null) {
                        break
                    }

                    // send frame to drawable
                    renderer.render(frame)
                    sendToMain(threadId, MSG_REDRAW, null)

                    delay = decoder.getDelay()

                    if (System.currentTimeMillis() > infoTime) {
                        log(INFO, "Drawa thread still running")
                        infoTime += (10 * 1000).toLong()
                    }
                    if (System.currentTimeMillis() > startTime + 4 * 60 * 60 * 1000) {
                        throw RuntimeException("Drawa thread leaked, fix your code")
                    }
                }
            } catch (ex: IOException) {
                Log.d(TAG, "gif drawable warn", ex)
            } catch (ex: InterruptedException) {
                Log.d(TAG, "gif drawable err", ex)
            } finally {
                log(DEBUG, "stopping decoder")
                decoder.stop()
            }
        }

        sendToMain(threadId, MSG_FINALIZE, null)
        log(DEBUG, "finished thread $threadId")
    }

    private fun sendToMain(threadId: Long, what: Int, bitmap: Bitmap?) {
        val param = ThreadParam()
        param.threadId = threadId
        param.bitmap = bitmap
        mainHandler.obtainMessage(what, param).sendToTarget()
    }

    private fun mainThread(what: Int, obj: ThreadParam) {
        if (what == MSG_FINALIZE) {
            log(DEBUG, "removing thread " + obj.threadId)
            removeThread(obj.threadId)
            return
        }

        val info = getThreadInfo(obj.threadId)
        if (info == null) {
            log(DEBUG, "no thread info")
            return
        }
        val view = info.drawable.get()
        if (view == null) {
            log(DEBUG, "no drawable")
            return
        }

        if (what == MSG_REDRAW) {
            view.invalidateSelf()
        }
    }

    override fun onBoundsChange(bounds: Rect) {
        matrix.setRectToRect(
                RectF(0f, 0f, intrinsicWidth.toFloat(), intrinsicHeight.toFloat()),
                RectF(getBounds()),
                Matrix.ScaleToFit.CENTER
        )
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, matrix, paint)
    }

    override fun getIntrinsicWidth(): Int {
        return bitmap.width
    }

    override fun getIntrinsicHeight(): Int {
        return bitmap.height
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    override fun start() {
        play()
    }

    override fun isRunning(): Boolean {
        return getAnimState() == STATE_PLAYING
    }

    fun log(level: Int, msg: String) {
        Log.d(TAG, "[$level] $msg")
    }

}

private const val TAG = "StreamDrawable"
private const val INFO = 1
private const val DEBUG = 2
private const val VERBOSE = 3

private const val STATE_STOPPED = 0
private const val STATE_PLAYING = 1
private const val STATE_PAUSED = 2

private const val MSG_REDRAW = 1
private const val MSG_FINALIZE = 2