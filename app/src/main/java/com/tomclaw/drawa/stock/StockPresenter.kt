package com.tomclaw.drawa.stock

import android.os.Bundle

interface StockPresenter {

    fun attachView(view: StockView)

    fun detachView()

    fun attachRouter(router: StockRouter)

    fun detachRouter()

    fun saveState(): Bundle

    interface StockRouter {

        fun openDrawingScreen()

    }

}

class StockPresenterImpl(state: Bundle?) : StockPresenter {

    override fun attachView(view: StockView) {

    }

    override fun detachView() {

    }

    override fun attachRouter(router: StockPresenter.StockRouter) {

    }

    override fun detachRouter() {

    }

    override fun saveState() = Bundle().apply {

    }

}