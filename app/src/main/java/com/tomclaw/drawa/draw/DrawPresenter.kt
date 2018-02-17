package com.tomclaw.drawa.draw

import android.os.Bundle
import android.view.MotionEvent
import com.tomclaw.drawa.draw.tools.Tool
import com.tomclaw.drawa.draw.view.DrawingListener
import com.tomclaw.drawa.util.SchedulersFactory
import io.reactivex.disposables.CompositeDisposable

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
                        private val toolProvider: ToolProvider,
                        private val history: History,
                        state: Bundle?) : DrawPresenter {

    private var view: DrawView? = null
    private var router: DrawPresenter.DrawRouter? = null

    private val subscriptions = CompositeDisposable()

    private var tool: Tool? = null

    override fun attachView(view: DrawView) {
        this.view = view
        view.setDrawingListener(object : DrawingListener {

            override fun onTouchEvent(eventX: Int, eventY: Int, action: Int) {
                tool?.let { tool ->
                    val e = history.add(tool, eventX, eventY, action)
                    processToolEvent(e)
                }
            }

            override fun onDraw() {
                tool?.onDraw()
            }

        })
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

    private fun processToolEvent(event: Event) {
        val tool = toolProvider.getTool(event.toolType)
        val x = event.x
        val y = event.y
        with(tool) {
            color = event.color
            baseRadius = event.radius
            when (event.action) {
                MotionEvent.ACTION_DOWN -> onTouchDown(x, y)
                MotionEvent.ACTION_MOVE -> onTouchMove(x, y)
                MotionEvent.ACTION_UP -> onTouchUp(x, y)
            }
            onDraw()
        }
    }

}