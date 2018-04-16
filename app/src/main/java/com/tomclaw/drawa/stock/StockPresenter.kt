package com.tomclaw.drawa.stock

import android.os.Bundle
import com.tomclaw.drawa.dto.Record
import com.tomclaw.drawa.util.DataProvider
import com.tomclaw.drawa.util.SchedulersFactory
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

interface StockPresenter {

    fun attachView(view: StockView)

    fun detachView()

    fun attachRouter(router: StockRouter)

    fun detachRouter()

    fun saveState(): Bundle

    fun onUpdate()

    interface StockRouter {

        fun showDrawingScreen(record: Record)

    }

}

class StockPresenterImpl(private val interactor: StockInteractor,
                         private val dataProvider: DataProvider<StockItem>,
                         private val recordConverter: RecordConverter,
                         private val schedulers: SchedulersFactory,
                         state: Bundle?) : StockPresenter {

    private var view: StockView? = null
    private var router: StockPresenter.StockRouter? = null

    private val subscriptions = CompositeDisposable()

    override fun attachView(view: StockView) {
        this.view = view

        subscriptions += view.itemClicks().subscribe { item ->
            interactor.get(item.id)?.let { record ->
                router?.showDrawingScreen(record)
            }
        }

        subscriptions += view.createClicks().subscribe { createStockItem() }

        if (interactor.isLoaded()) {
            bindRecords(interactor.get())
        } else {
            loadStockItems()
        }
    }

    private fun createStockItem() {
        val record = interactor.create()
        val records = interactor.add(record)
        subscriptions += interactor.saveJournal()
                .observeOn(schedulers.mainThread())
                .doOnSubscribe { view?.showProgress() }
                .doAfterTerminate { view?.showContent() }
                .subscribe({
                    bindRecords(records)
                    router?.showDrawingScreen(record)
                }, {})
    }

    private fun loadStockItems() {
        subscriptions += interactor.loadJournal()
                .observeOn(schedulers.mainThread())
                .doOnSubscribe { view?.showProgress() }
                .doAfterTerminate { view?.showContent() }
                .subscribe({ records ->
                    bindRecords(records)
                }, {})
    }

    private fun bindRecords(records: List<Record>) {
        val items = records
                .sortedBy { it.time }
                .reversed()
                .map { recordConverter.convert(it) }
        dataProvider.setData(items)
        view?.updateList()
        view?.showContent()
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

    override fun saveState() = Bundle().apply {}

    override fun onUpdate() {
        loadStockItems()
    }

}
