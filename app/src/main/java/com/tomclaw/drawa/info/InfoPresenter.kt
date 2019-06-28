package com.tomclaw.drawa.info

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

interface InfoPresenter {

    fun attachView(view: InfoView)

    fun detachView()

    fun attachRouter(router: InfoRouter)

    fun detachRouter()

    interface InfoRouter {

        fun openRate()

        fun openProjects()

        fun leaveScreen()

    }

}

class InfoPresenterImpl(private val resourceProvider: InfoResourceProvider) : InfoPresenter {

    private var view: InfoView? = null
    private var router: InfoPresenter.InfoRouter? = null

    private val subscriptions = CompositeDisposable()

    override fun attachView(view: InfoView) {
        this.view = view

        subscriptions += view.navigationClicks().subscribe { router?.leaveScreen() }
        subscriptions += view.rateClicks().subscribe { router?.openRate() }
        subscriptions += view.projectsClicks().subscribe { router?.openProjects() }

        bindVersion()
    }

    private fun bindVersion() {
        view?.setVersion(resourceProvider.provideVersion())
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