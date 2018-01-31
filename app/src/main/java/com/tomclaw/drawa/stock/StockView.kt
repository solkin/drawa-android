package com.tomclaw.drawa.stock

import android.view.View
import io.reactivex.Observable

interface StockView {

    fun showProgress()

    fun showContent()

    fun updateList()

    fun itemClicks(): Observable<StockItem>

}

class StockViewImpl(view: View) : StockView {

    override fun showProgress() {

    }

    override fun showContent() {

    }

    override fun updateList() {

    }

    override fun itemClicks(): Observable<StockItem> {
        return Observable.empty()
    }

}