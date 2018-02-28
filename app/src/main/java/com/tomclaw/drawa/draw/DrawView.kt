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
import com.tomclaw.drawa.util.convertDpToPixel
import com.tomclaw.drawa.util.hide
import com.tomclaw.drawa.util.show
import com.tomclaw.drawa.util.toggle
import io.reactivex.Observable


interface DrawView {

    fun setDrawingListener(listener: DrawingListener)

    fun acceptTool(tool: Tool)

    fun showProgress()

    fun showSaveProgress()

    fun showContent()

    fun showToolChooser()

    fun showColorChooser()

    fun showSizeChooser()

    fun touchEvents(): Observable<TouchEvent>

    fun drawEvents(): Observable<Unit>

    fun navigationClicks(): Observable<Unit>

    fun undoClicks(): Observable<Unit>

    fun deleteClicks(): Observable<Unit>

    fun tuneToolClicks(): Observable<Unit>

    fun tuneColorClicks(): Observable<Unit>

    fun tuneSizeClicks(): Observable<Unit>

}

class DrawViewImpl(view: View,
                   bitmapHolder: BitmapHolder) : DrawView {

    private val context = view.context
    private val toolbar: Toolbar = view.findViewById(R.id.toolbar)
    private val drawingView: DrawingView = view.findViewById(R.id.drawing_view)
    private val flipper: ViewFlipper = view.findViewById(R.id.flipper)
    private val tuneTool: View = view.findViewById(R.id.tune_tool)
    private val tuneColor: View = view.findViewById(R.id.tune_color)
    private val tuneSize: View = view.findViewById(R.id.tune_size)
    private val toolsContainer: View = view.findViewById(R.id.tools_container)
    private val toolChooser: View = view.findViewById(R.id.tool_chooser)
    private val colorChooser: View = view.findViewById(R.id.color_chooser)
    private val sizeChooser: View = view.findViewById(R.id.size_chooser)

    private val touchRelay = PublishRelay.create<TouchEvent>()
    private val drawRelay = PublishRelay.create<Unit>()
    private val navigationRelay = PublishRelay.create<Unit>()
    private val undoRelay = PublishRelay.create<Unit>()
    private val deleteRelay = PublishRelay.create<Unit>()
    private val tuneToolRelay = PublishRelay.create<Unit>()
    private val tuneColorRelay = PublishRelay.create<Unit>()
    private val tuneSizeRelay = PublishRelay.create<Unit>()

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
        tuneTool.setOnClickListener { tuneToolRelay.accept(Unit) }
        tuneColor.setOnClickListener { tuneColorRelay.accept(Unit) }
        tuneSize.setOnClickListener { tuneSizeRelay.accept(Unit) }
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

    override fun showSaveProgress() {
    }

    override fun showContent() {
        flipper.displayedChild = 1
    }

    override fun showToolChooser() {
        toolsContainer.toggle()
        toolChooser.show()
        colorChooser.hide()
        sizeChooser.hide()
    }

    override fun showColorChooser() {
        toolsContainer.toggle()
        toolChooser.hide()
        colorChooser.show()
        sizeChooser.hide()
    }

    override fun showSizeChooser() {
        toolsContainer.toggle()
        toolChooser.hide()
        colorChooser.hide()
        sizeChooser.show()
    }

    override fun touchEvents(): Observable<TouchEvent> = touchRelay

    override fun drawEvents(): Observable<Unit> = drawRelay

    override fun navigationClicks(): Observable<Unit> = navigationRelay

    override fun undoClicks(): Observable<Unit> = undoRelay

    override fun deleteClicks(): Observable<Unit> = deleteRelay

    override fun tuneToolClicks(): Observable<Unit> = tuneToolRelay

    override fun tuneColorClicks(): Observable<Unit> = tuneColorRelay

    override fun tuneSizeClicks(): Observable<Unit> = tuneSizeRelay

}
