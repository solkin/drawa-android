package com.tomclaw.drawa.stock

import android.os.Bundle
import com.tomclaw.drawa.dto.Image
import com.tomclaw.drawa.dto.Size
import com.tomclaw.drawa.util.DataProvider
import com.tomclaw.drawa.util.SchedulersFactory
import io.reactivex.disposables.CompositeDisposable
import java.util.*

interface StockPresenter {

    fun attachView(view: StockView)

    fun detachView()

    fun attachRouter(router: StockRouter)

    fun detachRouter()

    fun saveState(): Bundle

    interface StockRouter {

        fun showDrawingScreen()

        fun showDrawingScreen(item: StockItem)

    }

}

class StockPresenterImpl(private val interactor: StockInteractor,
                         private val dataProvider: DataProvider<StockItem>,
                         private val schedulers: SchedulersFactory,
                         state: Bundle?) : StockPresenter {

    private var view: StockView? = null
    private var router: StockPresenter.StockRouter? = null

    private var items: List<StockItem>? = state?.getParcelableArrayList(KEY_ITEMS)

    private val subscriptions = CompositeDisposable()

    override fun attachView(view: StockView) {
        this.view = view

        subscriptions.add(
                view.itemClicks()
                        .subscribeOn(schedulers.mainThread())
                        .subscribe { item ->
                            router?.showDrawingScreen(item)
                        }
        )

        subscriptions.add(
                view.createClicks()
                        .subscribeOn(schedulers.mainThread())
                        .subscribe {
                            createStockItem()
                        }
        )

        val items = items
        if (items == null) {
            loadStockItems()
        } else {
            bindStockItems(items)
        }
    }

    private fun createStockItem() {
        val items = LinkedList(items ?: emptyList())
        val prefix = "draw-" + items.size
        val image = Image(prefix + ".png", Size(10, 10))
        val item = StockItem(prefix + ".dat", image)
        items.add(item)
        subscriptions.add(
                interactor.saveStockItems(items)
                        .observeOn(schedulers.mainThread())
                        .doOnSubscribe { view?.showProgress() }
                        .doAfterTerminate { view?.showContent() }
                        .subscribe({
                            bindStockItems(items)
                            router?.showDrawingScreen(item)
                        }, {})
        )
    }

    private fun loadStockItems() {
        subscriptions.add(
                interactor.loadStockItems()
                        .observeOn(schedulers.mainThread())
                        .doOnSubscribe { view?.showProgress() }
                        .doAfterTerminate { view?.showContent() }
                        .subscribe({ items ->
                            bindStockItems(items)
                        }, {})
        )
    }

    private fun bindStockItems(items: List<StockItem>) {
        this.items = items
        dataProvider.setData(items)
        view?.updateList()
    }

    override fun detachView() {
        subscriptions.clear()
        this.view = null
    }

    override fun attachRouter(router: StockPresenter.StockRouter) {
        this.router = router
    }

    override fun detachRouter() {
        this.router = null
    }

    override fun saveState() = Bundle().apply {
        putParcelableArrayList(KEY_ITEMS, ArrayList(items ?: emptyList()))
    }

}

private const val KEY_ITEMS = "items"
