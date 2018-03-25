package com.tomclaw.drawa.share

import android.os.Bundle
import com.tomclaw.drawa.util.SchedulersFactory
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

interface SharePresenter {

    fun attachView(view: ShareView)

    fun detachView()

    fun attachRouter(router: ShareRouter)

    fun detachRouter()

    fun saveState(): Bundle

    interface ShareRouter {

        fun leaveScreen()

    }

}

class SharePresenterImpl(private val interactor: ShareInteractor,
                         private val schedulers: SchedulersFactory,
                         state: Bundle?) : SharePresenter {

    private var view: ShareView? = null
    private var router: SharePresenter.ShareRouter? = null

    private val subscriptions = CompositeDisposable()

    override fun attachView(view: ShareView) {
        this.view = view

        subscriptions += view.navigationClicks().subscribe {
            router?.leaveScreen()
        }

        loadHistory()
    }

    override fun detachView() {
        subscriptions.clear()
        this.view = null
    }

    override fun attachRouter(router: SharePresenter.ShareRouter) {
        this.router = router
    }

    override fun detachRouter() {
        this.router = null
    }

    override fun saveState() = Bundle().apply {}

    private fun loadHistory() {
        subscriptions += interactor.loadHistory()
                .observeOn(schedulers.mainThread())
                .doOnSubscribe { view?.showProgress() }
                .doAfterTerminate { view?.showContent() }
                .subscribe({
                    onLoaded()
                }, {
                    onError()
                })
    }

    private fun onLoaded() {
    }

    private fun onError() {
    }

}