package com.tomclaw.drawa.stock

import com.tomclaw.drawa.dto.Image
import com.tomclaw.drawa.dto.Size
import io.reactivex.Observable

interface StockInteractor {

    fun loadStockItems(): Observable<List<StockItem>>

}

class StockInteractorImpl() : StockInteractor {

    override fun loadStockItems(): Observable<List<StockItem>> {
        return Observable.just(listOf(
                StockItem(Image("", Size(10, 10))),
                StockItem(Image("", Size(10, 10))),
                StockItem(Image("", Size(10, 10))),
                StockItem(Image("", Size(10, 10)))
        ))
    }

}