package com.tomclaw.drawa.info

import com.tomclaw.drawa.util.SchedulersFactory
import io.reactivex.disposables.CompositeDisposable

interface InfoPresenter {

    fun attachView(view: InfoView)

    fun detachView()

    fun attachRouter(router: InfoRouter)

    fun detachRouter()

    interface InfoRouter {

        fun leaveScreen()

    }

}

class InfoPresenterImpl(private val schedulers: SchedulersFactory) : InfoPresenter {

    private var view: InfoView? = null
    private var router: InfoPresenter.InfoRouter? = null

    private val subscriptions = CompositeDisposable()

    override fun attachView(view: InfoView) {
        this.view = view
    }

    override fun detachView() {
        subscriptions.clear()
        this.view = null
    }

    override fun attachRouter(router: InfoPresenter.InfoRouter) {
        this.router = router
    }

    override fun detachRouter() {
        this.router = null
    }

}