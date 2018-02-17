package com.tomclaw.drawa.draw

import android.annotation.SuppressLint
import android.support.v7.widget.Toolbar
import android.view.View
import com.tomclaw.drawa.R
import com.tomclaw.drawa.draw.view.DrawingListener
import com.tomclaw.drawa.draw.view.DrawingView

interface DrawView {

    fun setDrawingListener(listener: DrawingListener)

}

class DrawViewImpl(view: View) : DrawView {

    private val context = view.context
    private val toolbar: Toolbar = view.findViewById(R.id.toolbar)
    private val drawingView: DrawingView = view.findViewById(R.id.drawing_view)

    init {
        toolbar.setTitle(R.string.draw)
    }

    override fun setDrawingListener(listener: DrawingListener) {
        drawingView.drawingListener = listener
    }
}
