package com.tomclaw.drawa.info

import android.net.Uri
import com.tomclaw.drawa.util.SchedulersFactory
import io.reactivex.disposables.CompositeDisposable

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

class InfoPresenterImpl : InfoPresenter {

    private var view: InfoView? = null
    private var router: InfoPresenter.InfoRouter? = null

    private val subscriptions = CompositeDisposable()

    override fun attachView(view: InfoView) {
        this.view = view

        view.navigationClicks().subscribe { router?.leaveScreen() }
        view.rateClicks().subscribe { router?.openRate() }
        view.projectsClicks().subscribe { router?.openProjects() }
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