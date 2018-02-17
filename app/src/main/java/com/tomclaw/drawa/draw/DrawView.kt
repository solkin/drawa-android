package com.tomclaw.drawa.draw

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MotionEvent
import android.view.View
import android.widget.ViewFlipper
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.R
import com.tomclaw.drawa.draw.tools.Tool
import com.tomclaw.drawa.draw.view.DrawingListener
import com.tomclaw.drawa.draw.view.DrawingView
import com.tomclaw.drawa.draw.view.TouchEvent
import com.tomclaw.drawa.util.convertDpToPixel
import io.reactivex.Observable


interface DrawView {

    fun setDrawingListener(listener: DrawingListener)

    fun acceptTool(tool: Tool)

    fun showProgress()

    fun showContent()

    fun touchEvents(): Observable<TouchEvent>

    fun drawEvents(): Observable<Unit>

    fun navigationClicks(): Observable<Unit>

}

class DrawViewImpl(view: View) : DrawView {

    private val context = view.context
    private val toolbar: Toolbar = view.findViewById(R.id.toolbar)
    private val drawingView: DrawingView = view.findViewById(R.id.drawing_view)
    private val flipper: ViewFlipper = view.findViewById(R.id.flipper)

    private val touchRelay = PublishRelay.create<TouchEvent>()
    private val drawRelay = PublishRelay.create<Unit>()
    private val navigationRelay = PublishRelay.create<Unit>()

    init {
        toolbar.setTitle(R.string.draw)
        toolbar.setNavigationOnClickListener {
            navigationRelay.accept(Unit)
        }
        drawingView.drawingListener = object : DrawingListener {
            override fun onTouchEvent(event: TouchEvent) {
                touchRelay.accept(event)
            }

            override fun onDraw() {
                drawRelay.accept(Unit)
            }
        }
    }

    override fun setDrawingListener(listener: DrawingListener) {
        drawingView.drawingListener = listener
    }

    override fun acceptTool(tool: Tool) {
        tool.initialize(drawingView)
        tool.baseRadius = context.resources.convertDpToPixel(8f).toInt()
    }

    override fun showProgress() {
        flipper.displayedChild = 0
    }

    override fun showContent() {
        flipper.displayedChild = 1
    }

    override fun touchEvents(): Observable<TouchEvent> = touchRelay

    override fun drawEvents(): Observable<Unit> = drawRelay

    override fun navigationClicks(): Observable<Unit> = navigationRelay

}
