package com.tomclaw.drawa.share

import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ViewFlipper
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.R
import io.reactivex.Observable

interface ShareView {

    fun showProgress()

    fun showContent()

    fun navigationClicks(): Observable<Unit>

}

class ShareViewImpl(view: View) : ShareView {

    private val context = view.context
    private val toolbar: Toolbar = view.findViewById(R.id.toolbar)
    private val flipper: ViewFlipper = view.findViewById(R.id.flipper)

    private val navigationRelay = PublishRelay.create<Unit>()

    init {
        toolbar.setTitle(R.string.share)
        toolbar.setNavigationOnClickListener {
            navigationRelay.accept(Unit)
        }
    }

    override fun showProgress() {
        flipper.displayedChild = 0
    }

    override fun showContent() {
        flipper.displayedChild = 1
    }

    override fun navigationClicks(): Observable<Unit> = navigationRelay

}