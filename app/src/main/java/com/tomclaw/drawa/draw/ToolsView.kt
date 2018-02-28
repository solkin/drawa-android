package com.tomclaw.drawa.draw

import android.view.View
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.R
import com.tomclaw.drawa.util.hide
import com.tomclaw.drawa.util.show
import com.tomclaw.drawa.util.toggle
import io.reactivex.Observable

interface ToolsView {

    fun showToolChooser()

    fun showColorChooser()

    fun showSizeChooser()

    fun tuneToolClicks(): Observable<Unit>

    fun tuneColorClicks(): Observable<Unit>

    fun tuneSizeClicks(): Observable<Unit>

}

class ToolsViewImpl(view: View) : ToolsView {

    private val tuneTool: View = view.findViewById(R.id.tune_tool)
    private val tuneColor: View = view.findViewById(R.id.tune_color)
    private val tuneSize: View = view.findViewById(R.id.tune_size)
    private val toolsContainer: View = view.findViewById(R.id.tools_container)
    private val toolChooser: View = view.findViewById(R.id.tool_chooser)
    private val colorChooser: View = view.findViewById(R.id.color_chooser)
    private val sizeChooser: View = view.findViewById(R.id.size_chooser)

    private val tuneToolRelay = PublishRelay.create<Unit>()
    private val tuneColorRelay = PublishRelay.create<Unit>()
    private val tuneSizeRelay = PublishRelay.create<Unit>()

    init {
        tuneTool.setOnClickListener { tuneToolRelay.accept(Unit) }
        tuneColor.setOnClickListener { tuneColorRelay.accept(Unit) }
        tuneSize.setOnClickListener { tuneSizeRelay.accept(Unit) }
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

    override fun tuneToolClicks(): Observable<Unit> = tuneToolRelay

    override fun tuneColorClicks(): Observable<Unit> = tuneColorRelay

    override fun tuneSizeClicks(): Observable<Unit> = tuneSizeRelay

}