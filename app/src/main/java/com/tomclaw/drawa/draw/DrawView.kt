package com.tomclaw.drawa.draw

import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ViewFlipper
import com.tomclaw.drawa.R
import com.tomclaw.drawa.draw.tools.Tool
import com.tomclaw.drawa.draw.view.DrawingListener
import com.tomclaw.drawa.draw.view.DrawingView
import com.tomclaw.drawa.util.convertDpToPixel

interface DrawView {

    fun setDrawingListener(listener: DrawingListener)

    fun acceptTool(tool: Tool)

    fun showProgress()

    fun showContent()

}

class DrawViewImpl(view: View) : DrawView {

    private val context = view.context
    private val toolbar: Toolbar = view.findViewById(R.id.toolbar)
    private val drawingView: DrawingView = view.findViewById(R.id.drawing_view)
    private val flipper: ViewFlipper = view.findViewById(R.id.flipper)

    init {
        toolbar.setTitle(R.string.draw)
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

}
