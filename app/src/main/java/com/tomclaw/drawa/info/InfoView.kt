package com.tomclaw.drawa.info

import android.support.v7.widget.Toolbar
import android.view.View
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.R
import io.reactivex.Observable

interface InfoView {

    fun navigationClicks(): Observable<Unit>

}

class InfoViewImpl(view: View) : InfoView {

    private val toolbar: Toolbar = view.findViewById(R.id.toolbar)

    private val navigationRelay = PublishRelay.create<Unit>()

    init {
        toolbar.setTitle(R.string.info)
        toolbar.setNavigationOnClickListener {
            navigationRelay.accept(Unit)
        }
    }

    override fun navigationClicks(): Observable<Unit> = navigationRelay

}