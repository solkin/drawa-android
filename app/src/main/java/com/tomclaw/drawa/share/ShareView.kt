package com.tomclaw.drawa.share

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Toast
import android.widget.ViewFlipper
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.R
import com.tomclaw.drawa.util.CircleProgressView
import com.tomclaw.drawa.util.hideWithAlphaAnimation
import com.tomclaw.drawa.util.showWithAlphaAnimation
import io.reactivex.Observable
import java.util.concurrent.TimeUnit


interface ShareView {

    fun showProgress()

    fun showOverlayProgress()

    fun setOverlayProgress(value: Float = 0f)

    fun showContent()

    fun showMessage(text: String)

    fun navigationClicks(): Observable<Unit>

    fun itemClicks(): Observable<ShareItem>

}

class ShareViewImpl(view: View,
                    adapter: ShareAdapter) : ShareView {

    private val context = view.context
    private val toolbar: Toolbar = view.findViewById(R.id.toolbar)
    private val overlayProgress: View = view.findViewById(R.id.overlay_progress)
    private val progress: CircleProgressView = view.findViewById(R.id.progress)
    private val flipper: ViewFlipper = view.findViewById(R.id.flipper)
    private val recycler: RecyclerView = view.findViewById(R.id.recycler)

    private val navigationRelay = PublishRelay.create<Unit>()
    private val itemRelay = PublishRelay.create<ShareItem>()

    init {
        toolbar.setTitle(R.string.share)
        toolbar.setNavigationOnClickListener {
            navigationRelay.accept(Unit)
        }
        val layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
        )
        adapter.setHasStableIds(true)
        adapter.itemRelay = itemRelay
        recycler.adapter = adapter
        recycler.layoutManager = layoutManager
    }

    override fun showProgress() {
        flipper.displayedChild = 0
    }

    override fun showOverlayProgress() {
        overlayProgress.showWithAlphaAnimation(animateFully = true)
        progress.animation = RotateAnimation(
                0.0f,
                360.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f).apply {
            duration = TimeUnit.SECONDS.toMillis(1)
            repeatCount = Animation.INFINITE
            repeatMode = Animation.RESTART
            fillAfter = true
            interpolator = LinearInterpolator()
        }
    }

    override fun setOverlayProgress(value: Float) {
        progress.setProgressWithAnimation(value, 500)
    }

    override fun showContent() {
        flipper.displayedChild = 1
        overlayProgress.hideWithAlphaAnimation(
                animateFully = false,
                endCallback = { progress.clearAnimation() }
        )
    }

    override fun showMessage(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    override fun navigationClicks(): Observable<Unit> = navigationRelay

    override fun itemClicks(): Observable<ShareItem> = itemRelay

}