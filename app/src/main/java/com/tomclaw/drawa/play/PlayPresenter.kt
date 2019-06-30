package com.tomclaw.drawa.play

import com.tomclaw.drawa.draw.DrawHost
import com.tomclaw.drawa.util.StreamDrawable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

interface PlayPresenter {

    fun attachView(view: PlayView)

    fun detachView()

    fun attachRouter(router: PlayRouter)

    fun detachRouter()

    interface PlayRouter {

        fun leaveScreen()

    }

}

class PlayPresenterImpl(
        private val drawHost: DrawHost,
        private val drawable: EventsDrawable,
        private val eventsProvider: EventsProvider
) : PlayPresenter {

    private var view: PlayView? = null
    private var router: PlayPresenter.PlayRouter? = null

    private var isReplay: Boolean = false

    private val subscriptions = CompositeDisposable()

    override fun attachView(view: PlayView) {
        this.view = view

        subscriptions += view.navigationClicks().subscribe { router?.leaveScreen() }
        subscriptions += view.replayClicks().subscribe { onReplay() }

        drawable.listener = object : StreamDrawable.AnimationListener {

            override fun onAnimationStart() {
                view.hideReplayButton()
                drawHost.clearBitmap()
            }

            override fun onAnimationEnd() {
                view.showReplayButton()
            }
        }

        showDrawable()
    }

    override fun detachView() {
        subscriptions.clear()
        drawable.stop()
        this.view = null
    }

    override fun attachRouter(router: PlayPresenter.PlayRouter) {
        this.router = router
    }

    override fun detachRouter() {
        this.router = null
    }

    private fun showDrawable() {
        view?.showDrawable(drawable)
    }

    private fun onReplay() {
        eventsProvider.reset()
        drawable.start()
    }

}