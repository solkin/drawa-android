package com.tomclaw.drawa.draw

import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ViewFlipper
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.R
import com.tomclaw.drawa.draw.tools.Tool
import com.tomclaw.drawa.draw.view.DrawingListener
import com.tomclaw.drawa.draw.view.DrawingView
import com.tomclaw.drawa.draw.view.TouchEvent
import com.tomclaw.drawa.util.MetricsProvider
import io.reactivex.Observable

interface DrawView : ToolsView {

    fun setDrawingListener(listener: DrawingListener)

    fun acceptTool(tool: Tool)

    fun showProgress()

    fun showSaveProgress()

    fun showContent()

    fun touchEvents(): Observable<TouchEvent>

    fun drawEvents(): Observable<Unit>

    fun navigationClicks(): Observable<Unit>

    fun undoClicks(): Observable<Unit>

    fun deleteClicks(): Observable<Unit>

}

class DrawViewImpl(view: View,
                   bitmapHolder: BitmapHolder,
                   private val metricsProvider: MetricsProvider
) : DrawView, ToolsView by ToolsViewImpl(view) {

    private val toolbar: Toolbar = view.findViewById(R.id.toolbar)
    private val drawingView: DrawingView = view.findViewById(R.id.drawing_view)
    private val flipper: ViewFlipper = view.findViewById(R.id.flipper)

    private val touchRelay = PublishRelay.create<TouchEvent>()
    private val drawRelay = PublishRelay.create<Unit>()
    private val navigationRelay = PublishRelay.create<Unit>()
    private val undoRelay = PublishRelay.create<Unit>()
    private val deleteRelay = PublishRelay.create<Unit>()

    init {
        bitmapHolder.drawHost = drawingView
        toolbar.setTitle(R.string.draw)
        toolbar.setNavigationOnClickListener {
            navigationRelay.accept(Unit)
        }
        toolbar.inflateMenu(R.menu.main_draw)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_undo -> undoRelay.accept(Unit)
                R.id.menu_delete -> deleteRelay.accept(Unit)
            }
            true
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
        tool.initialize(drawingView, metricsProvider)
    }

    override fun showProgress() {
        flipper.displayedChild = 0
    }

    override fun showSaveProgress() {
    }

    override fun showContent() {
        flipper.displayedChild = 1
    }

    override fun touchEvents(): Observable<TouchEvent> = touchRelay

    override fun drawEvents(): Observable<Unit> = drawRelay

    override fun navigationClicks(): Observable<Unit> = navigationRelay

    override fun undoClicks(): Observable<Unit> = undoRelay

    override fun deleteClicks(): Observable<Unit> = deleteRelay

}
