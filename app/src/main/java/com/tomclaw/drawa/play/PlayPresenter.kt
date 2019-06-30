package com.tomclaw.drawa.play

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
        private val drawable: EventsDrawable
) : PlayPresenter {

    private var view: PlayView? = null
    private var router: PlayPresenter.PlayRouter? = null

    private val subscriptions = CompositeDisposable()

    override fun attachView(view: PlayView) {
        this.view = view

        subscriptions += view.navigationClicks().subscribe { router?.leaveScreen() }

        showDrawable()
    }

    override fun detachView() {
        subscriptions.clear()
        view?.destroy()
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

}