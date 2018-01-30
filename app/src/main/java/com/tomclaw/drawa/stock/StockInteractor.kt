package com.tomclaw.drawa.stock

import io.reactivex.Observable

interface StockInteractor {

    fun loadStockItems(): Observable<StockItem>

}

class StockInteractorImpl() : StockInteractor {

    override fun loadStockItems(): Observable<StockItem> {
        return Observable.empty()
    }

}