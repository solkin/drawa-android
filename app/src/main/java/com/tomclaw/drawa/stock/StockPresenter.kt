package com.tomclaw.drawa.stock

import android.os.Bundle
import com.tomclaw.drawa.draw.view.BITMAP_HEIGHT
import com.tomclaw.drawa.draw.view.BITMAP_WIDTH
import com.tomclaw.drawa.dto.Image
import com.tomclaw.drawa.dto.Record
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

    private var records: List<Record>? = state?.getParcelableArrayList(KEY_RECORDS)

    private val subscriptions = CompositeDisposable()

    override fun attachView(view: StockView) {
        this.view = view

        subscriptions.add(
                view.itemClicks()
                        .subscribeOn(schedulers.mainThread())
                        .subscribe { item ->
                            records?.find { it.name == item.name }?.let { record ->
                                router?.showDrawingScreen(record)
                            }
                        }
        )

        subscriptions.add(
                view.createClicks()
                        .subscribeOn(schedulers.mainThread())
                        .subscribe {
                            createStockItem()
                        }
        )

        val records = records
        if (records == null) {
            loadStockItems()
        } else {
            bindRecords(records)
        }
    }

    private fun createStockItem() {
        val records = LinkedList(records ?: emptyList())
        val prefix = "draw-" + records.size
        val image = Image(prefix + ".png", Size(BITMAP_WIDTH, BITMAP_HEIGHT))
        val record = Record(prefix + ".dat", image)
        records.add(record)
        subscriptions.add(
                interactor.saveJournal(records)
                        .observeOn(schedulers.mainThread())
                        .doOnSubscribe { view?.showProgress() }
                        .doAfterTerminate { view?.showContent() }
                        .subscribe({
                            bindRecords(records)
                            router?.showDrawingScreen(record)
                        }, {})
        )
    }

    private fun loadStockItems() {
        subscriptions.add(
                interactor.loadJournal()
                        .observeOn(schedulers.mainThread())
                        .doOnSubscribe { view?.showProgress() }
                        .doAfterTerminate { view?.showContent() }
                        .subscribe({ records ->
                            bindRecords(records)
                        }, {})
        )
    }

    private fun bindRecords(records: List<Record>) {
        this.records = records
        val items = records.map { recordConverter.convert(it) }
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
        putParcelableArrayList(KEY_RECORDS, ArrayList(records ?: emptyList()))
    }

}

private const val KEY_RECORDS = "records"
