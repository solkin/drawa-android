package com.tomclaw.drawa.play

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.R
import com.tomclaw.drawa.util.show
import com.tomclaw.drawa.util.toggle
import io.reactivex.Observable


interface PlayView {

    fun navigationClicks(): Observable<Unit>

    fun replayClicks(): Observable<Unit>

    fun showDrawable(drawable: Drawable)

    fun showReplayButton()

    fun hideReplayButton()

}

class PlayViewImpl(view: View) : PlayView {

    private val toolbar: Toolbar = view.findViewById(R.id.toolbar)
    private val imageView: ImageView = view.findViewById(R.id.image_view)

    private val navigationRelay = PublishRelay.create<Unit>()
    private val replayRelay = PublishRelay.create<Unit>()

    init {
        toolbar.setTitle(R.string.play)
        toolbar.setNavigationOnClickListener { navigationRelay.accept(Unit) }
        toolbar.inflateMenu(R.menu.play)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_replay -> replayRelay.accept(Unit)
            }
            true
        }
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        imageView.setOnClickListener { toolbar.toggle() }
    }

    override fun navigationClicks(): Observable<Unit> = navigationRelay

    override fun replayClicks(): Observable<Unit> = replayRelay

    override fun showDrawable(drawable: Drawable) {
        imageView.setImageDrawable(drawable)
        (drawable as? Animatable)?.start()
    }

    override fun showReplayButton() {
        toolbar.menu.findItem(R.id.menu_replay).isVisible = true
        toolbar.show()
    }

    override fun hideReplayButton() {
        toolbar.menu.findItem(R.id.menu_replay).isVisible = false
    }

}
