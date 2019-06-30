package com.tomclaw.drawa.play

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.R
import io.reactivex.Observable


interface PlayView {

    fun navigationClicks(): Observable<Unit>

    fun showDrawable(drawable: Drawable)

    fun destroy()

}

class PlayViewImpl(view: View) : PlayView {

    private val toolbar: Toolbar = view.findViewById(R.id.toolbar)
    private val imageView: ImageView = view.findViewById(R.id.image_view)

    private val navigationRelay = PublishRelay.create<Unit>()

    init {
        toolbar.setTitle(R.string.play)
        toolbar.setNavigationOnClickListener { navigationRelay.accept(Unit) }
    }

    override fun navigationClicks(): Observable<Unit> = navigationRelay

    override fun showDrawable(drawable: Drawable) {
        imageView.setImageDrawable(drawable)
        (drawable as? Animatable)?.start()
    }

    override fun destroy() {
        (imageView.drawable as? Animatable)?.stop()
    }

}
