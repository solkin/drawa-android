package com.tomclaw.drawa.play

import android.view.View
import androidx.appcompat.widget.Toolbar
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.R
import io.reactivex.Observable

interface PlayView {

    fun navigationClicks(): Observable<Unit>

}

class PlayViewImpl(view: View) : PlayView {

    private val toolbar: Toolbar = view.findViewById(R.id.toolbar)

    private val navigationRelay = PublishRelay.create<Unit>()

    init {
        toolbar.setTitle(R.string.info)
        toolbar.setNavigationOnClickListener { navigationRelay.accept(Unit) }
    }

    override fun navigationClicks(): Observable<Unit> = navigationRelay

}
