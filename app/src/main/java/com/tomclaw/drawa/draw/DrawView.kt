package com.tomclaw.drawa.draw

import android.support.v7.widget.Toolbar
import android.view.View
import com.tomclaw.drawa.R

interface DrawView {

}

class DrawViewImpl(view: View) : DrawView {

    private val context = view.context
    private val toolbar: Toolbar = view.findViewById(R.id.toolbar)

    init {
        toolbar.setTitle(R.string.draw)
    }
}