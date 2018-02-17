package com.tomclaw.drawa.draw

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.*
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.draw.tools.TYPE_BRUSH
import com.tomclaw.drawa.draw.tools.Tool
import com.tomclaw.drawa.util.SchedulersFactory
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

interface DrawPresenter {

    fun attachView(view: DrawView)

    fun detachView()

    fun attachRouter(router: DrawRouter)

    fun detachRouter()

    fun saveState(): Bundle

    interface DrawRouter {

        fun showStockScreen()

        fun leaveScreen()

    }

}

class DrawPresenterImpl(private val interactor: DrawInteractor,
                        private val schedulers: SchedulersFactory,
                        private val toolProvider: ToolProvider,
                        private val history: History,
                        state: Bundle?) : DrawPresenter {

    private var view: DrawView? = null
    private var router: DrawPresenter.DrawRouter? = null

    private val subscriptions = CompositeDisposable()

    private var tool: Tool? = null

    private val saveRelay = PublishRelay.create<Unit>()

    override fun attachView(view: DrawView) {
        this.view = view
        toolProvider.listTools().forEach { view.acceptTool(it) }
        subscriptions.add(
                view.touchEvents().subscribe { event ->
                    tool?.let { tool ->
                        val e = history.add(tool, event.eventX, event.eventY, event.action)
                        processToolEvent(e)
                        if (event.action == MotionEvent.ACTION_UP) {
                            saveRelay.accept(Unit)
                        }
                    }
                }
        )
        subscriptions.add(
                view.drawEvents().subscribe {
                    tool?.onDraw()
                }
        )
        subscriptions.add(
                view.navigationClicks().subscribe {
                    router?.leaveScreen()
                }
        )
        subscriptions.add(
                saveRelay.debounce(1, TimeUnit.SECONDS)
                        .subscribe {
                            saveHistory()
                        }
        )
        loadHistory()

        tool = toolProvider.getTool(TYPE_BRUSH)?.apply {
            color = 0x2C82C9
        }
    }

    private fun saveHistory() {
        subscriptions.add(
                interactor.saveHistory()
                        .observeOn(schedulers.mainThread())
                        .subscribe()
        )
    }

    override fun detachView() {
        subscriptions.clear()
        this.view = null
    }

    override fun attachRouter(router: DrawPresenter.DrawRouter) {
        this.router = router
    }

    override fun detachRouter() {
        this.router = null
    }

    override fun saveState() = Bundle().apply {
    }

    var time: Long = 0
    private fun loadHistory() {
        time = System.currentTimeMillis()
        subscriptions.add(
                interactor.loadHistory()
                        .observeOn(schedulers.mainThread())
                        .doOnSubscribe { view?.showProgress() }
                        .doAfterTerminate { view?.showContent() }
                        .map {
                            history.getEvents().forEach { processToolEvent(it) }
                        }
                        .subscribe({
                            onHistoryLoaded()
                        }, {
                            onError()
                        }))
    }

    private fun onHistoryLoaded() {
        Log.d("Drawa", "load time: " + (System.currentTimeMillis() - time))
    }

    private fun onError() {

    }

    private fun processToolEvent(event: Event) {
        val tool = toolProvider.getTool(event.toolType) ?: return
        val x = event.x
        val y = event.y
        with(tool) {
            color = event.color
            radius = event.radius
            when (event.action) {
                ACTION_DOWN -> onTouchDown(x, y)
                ACTION_MOVE -> onTouchMove(x, y)
                ACTION_UP -> onTouchUp(x, y)
            }
            onDraw()
        }
    }

}