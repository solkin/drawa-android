package com.tomclaw.drawa.draw

import android.os.Bundle
import com.tomclaw.drawa.util.SchedulersFactory

interface DrawPresenter {

    fun attachView(view: DrawView)

    fun detachView()

    fun attachRouter(router: DrawRouter)

    fun detachRouter()

    fun saveState(): Bundle

    interface DrawRouter {

        fun showStockScreen()

    }

}

class DrawPresenterImpl(private val interactor: DrawInteractor,
                        private val schedulers: SchedulersFactory,
                        state: Bundle?) : DrawPresenter {

    override fun attachView(view: DrawView) {
    }

    override fun detachView() {
    }

    override fun attachRouter(router: DrawPresenter.DrawRouter) {
    }

    override fun detachRouter() {
    }

    override fun saveState() = Bundle.EMPTY

}