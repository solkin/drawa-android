package com.tomclaw.drawa.draw

import android.os.Bundle
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
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

    fun onBackPressed()

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

    private var isSaved = true
    private var isClosing = false

    override fun attachView(view: DrawView) {
        this.view = view
        toolProvider.listTools().forEach { view.acceptTool(it) }
        subscriptions.add(
                view.touchEvents().subscribe { event ->
                    tool?.let { tool ->
                        val e = history.add(tool, event.eventX, event.eventY, event.action)
                        processToolEvent(e)
                        isSaved = false
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
                    onBackPressed()
                }
        )
        subscriptions.add(
                saveRelay.debounce(500, TimeUnit.MILLISECONDS)
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
                        .subscribe(
                                { onHistorySaved() },
                                {}
                        )
        )
    }

    private fun onHistorySaved() {
        isSaved = true
        if (isClosing) {
            router?.leaveScreen()
        }
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

    override fun onBackPressed() {
        isClosing = true
        if (isSaved) {
            router?.leaveScreen()
        } else {
            view?.showSaveProgress()
        }
    }

    private fun loadHistory() {
        subscriptions.add(
                interactor.loadHistory()
                        .observeOn(schedulers.mainThread())
                        .doOnSubscribe { view?.showProgress() }
                        .doAfterTerminate { view?.showContent() }
                        .subscribe({
                            onHistoryLoaded()
                        }, {
                            onError()
                        }))
    }

    private fun onHistoryLoaded() {
    }

    private fun onError() {
    }

    private fun applyHistory() {
        history.getEvents().forEach { processToolEvent(it) }
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