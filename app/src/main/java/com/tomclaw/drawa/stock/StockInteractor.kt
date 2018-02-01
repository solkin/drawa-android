package com.tomclaw.drawa.stock

import io.reactivex.Observable

interface StockInteractor {

    fun loadStockItems(): Observable<List<StockItem>>

}

class StockInteractorImpl() : StockInteractor {

    override fun loadStockItems(): Observable<List<StockItem>> {
        return Observable.empty()
    }

}