package com.tomclaw.drawa.share

import android.os.Bundle
import com.tomclaw.drawa.util.DataProvider
import com.tomclaw.drawa.util.Logger
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
                         private val dataProvider: DataProvider<ShareItem>,
                         private val sharePlugins: Set<SharePlugin>,
                         private val logger: Logger,
                         private val schedulers: SchedulersFactory,
                         state: Bundle?) : SharePresenter {

    private var view: ShareView? = null
    private var router: SharePresenter.ShareRouter? = null

    private val subscriptions = CompositeDisposable()

    private var itemsMap: Map<Int, SharePlugin> = emptyMap()

    override fun attachView(view: ShareView) {
        this.view = view

        subscriptions += view.navigationClicks().subscribe {
            router?.leaveScreen()
        }
        subscriptions += view.itemClicks().subscribe { shareItem ->
            itemsMap[shareItem.id]?.let { runPlugin(it) }
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
        var id = 0
        itemsMap = sharePlugins.associate {
            Pair(id++, it)
        }
        val shareItems = itemsMap.entries.map { entry ->
            val plugin = entry.value
            ShareItem(
                    id = entry.key,
                    image = plugin.image,
                    title = plugin.title,
                    description = plugin.description
            )
        }
        dataProvider.setData(shareItems)
    }

    private fun runPlugin(plugin: SharePlugin) {
        // TODO: implement plugin invocation
        logger.log("run plugin $plugin")
        subscriptions += plugin.operation
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.mainThread())
                .doOnSubscribe { view?.showOverlayProgress() }
                .doAfterTerminate { view?.showContent() }
                .subscribe({
                    logger.log("plugin operation completed")
                }, {
                    onError()
                })
    }

    private fun onError() {
    }

}