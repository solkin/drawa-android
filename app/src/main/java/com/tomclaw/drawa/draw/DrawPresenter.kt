package com.tomclaw.drawa.draw

import android.os.Bundle
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.draw.tools.SIZE_M
import com.tomclaw.drawa.draw.tools.TYPE_BRUSH
import com.tomclaw.drawa.draw.tools.Tool
import com.tomclaw.drawa.util.SchedulersFactory
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import java.util.concurrent.TimeUnit

interface DrawPresenter {

    fun attachView(view: DrawView)

    fun detachView()

    fun attachRouter(router: DrawRouter)

    fun detachRouter()

    fun saveState(): Bundle

    fun onBackPressed()

    interface DrawRouter {

        fun showShareScreen()

        fun leaveScreen()

    }
}

class DrawPresenterImpl(private val interactor: DrawInteractor,
                        private val schedulers: SchedulersFactory,
                        private val toolProvider: ToolProvider,
                        private val history: History,
                        private val bitmapHolder: BitmapHolder,
                        state: Bundle?) : DrawPresenter {

    private var view: DrawView? = null
    private var router: DrawPresenter.DrawRouter? = null

    private val subscriptions = CompositeDisposable()

    private var toolColor = state?.getInt(KEY_TOOL_COLOR) ?: 0x2C82C9
    private var toolSize = state?.getInt(KEY_TOOL_SIZE) ?: SIZE_M
    private var toolType = state?.getInt(KEY_TOOL_TYPE) ?: TYPE_BRUSH

    private var tool: Tool = toolProvider.getTool(toolType)

    private val saveRelay = PublishRelay.create<Unit>()

    private var isSaved = true
    private var isClosing = false

    override fun attachView(view: DrawView) {
        this.view = view
        toolProvider.listTools().forEach { view.acceptTool(it) }
        subscriptions += view.touchEvents().subscribe { event ->
            val e = history.add(tool, event.eventX, event.eventY, event.action)
            processToolEvent(e)
            if (event.action == MotionEvent.ACTION_UP) {
                scheduleSaveHistory()
            }
        }
        subscriptions += view.drawEvents().subscribe { tool.onDraw() }
        subscriptions += view.navigationClicks().subscribe { onBackPressed() }
        subscriptions += view.undoClicks().subscribe { onUndo() }
        subscriptions += view.doneClicks().subscribe { onDone() }
        subscriptions += view.deleteClicks().subscribe { onDelete() }
        subscriptions += view.tuneClicks()
                .observeOn(schedulers.mainThread())
                .subscribe { id ->
                    when (id) {
                        ID_TOOL_CHOOSER -> view.showToolChooser()
                        ID_COLOR_CHOOSER -> view.showColorChooser()
                        ID_SIZE_CHOOSER -> view.showSizeChooser()
                    }
                }
        subscriptions += view.hideChooserClicks()
                .observeOn(schedulers.mainThread())
                .subscribe { view.hideChooser() }
        subscriptions += view.toolSelected().subscribe {
            changeTool(it)
            view.hideChooser(animate = true)
        }
        subscriptions += view.colorSelected().subscribe {
            changeColor(it)
            view.hideChooser(animate = true)
        }
        subscriptions += view.sizeSelected().subscribe {
            changeSize(it)
            view.hideChooser(animate = true)
        }
        subscriptions += saveRelay.debounce(SAVE_DEBOUNCE_DELAY, TimeUnit.MILLISECONDS).subscribe {
            saveHistory()
        }
        loadHistory()

        selectTool()
    }

    private fun selectTool() {
        tool = toolProvider.getTool(toolType).apply {
            color = toolColor
            size = toolSize
        }
        bindTool()
    }

    private fun changeTool(type: Int) {
        toolType = type
        selectTool()
    }

    private fun changeColor(color: Int) {
        toolColor = color
        selectTool()
    }

    private fun changeSize(size: Int) {
        toolSize = size
        selectTool()
    }

    private fun bindTool() {
        with(view ?: return) {
            setToolSelected(tool.type)
            setColorSelected(tool.color)
            setSizeSelected(tool.size)
        }
    }

    private fun onDelete() {
        subscriptions += interactor.delete()
                .observeOn(schedulers.mainThread())
                .doOnSubscribe { view?.showProgress() }
                .doAfterTerminate { view?.showContent() }
                .subscribe({ router?.leaveScreen() }, { })
    }

    private fun onUndo() {
        subscriptions += interactor.undo()
                .map { applyHistory() }
                .observeOn(schedulers.mainThread())
                .doOnSubscribe { view?.showOverlayProgress() }
                .doAfterTerminate { view?.showContent() }
                .subscribe({
                    invalidateDrawHost()
                    scheduleSaveHistory()
                    selectTool()
                }, { })
    }

    private fun onDone() {
        // TODO: check for history saved
        router?.showShareScreen()
    }

    private fun scheduleSaveHistory() {
        isSaved = false
        saveRelay.accept(Unit)
    }

    private fun saveHistory() {
        subscriptions += interactor.saveHistory()
                .observeOn(schedulers.mainThread())
                .subscribe(
                        { onHistorySaved() },
                        { }
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
        putInt(KEY_TOOL_TYPE, toolType)
        putInt(KEY_TOOL_COLOR, toolColor)
        putInt(KEY_TOOL_SIZE, toolSize)
    }

    override fun onBackPressed() {
        if (view?.isToolContainerShown == true) {
            view?.hideChooser()
        } else {
            isClosing = true
            if (isSaved) {
                router?.leaveScreen()
            } else {
                view?.showOverlayProgress()
            }
        }
    }

    private fun loadHistory() {
        subscriptions += interactor.loadHistory()
                .observeOn(schedulers.mainThread())
                .doOnSubscribe { view?.showProgress() }
                .doAfterTerminate { view?.showContent() }
                .subscribe({
                    invalidateDrawHost()
                }, {
                    onError()
                })
    }

    private fun onError() {
    }

    private fun invalidateDrawHost() {
        bitmapHolder.drawHost.invalidate()
    }

    private fun applyHistory() {
        val drawHost = bitmapHolder.drawHost
        try {
            drawHost.hidden = true
            drawHost.clearBitmap()
            history.getEvents().forEach { processToolEvent(it) }
        } finally {
            drawHost.hidden = false
        }
    }

    private fun processToolEvent(event: Event) {
        val tool = toolProvider.getTool(event.toolType)
        val x = event.x
        val y = event.y
        with(tool) {
            when (event.action) {
                ACTION_DOWN -> {
                    color = event.color
                    size = event.size
                    onTouchDown(x, y)
                }
                ACTION_MOVE -> onTouchMove(x, y)
                ACTION_UP -> onTouchUp(x, y)
            }
            onDraw()
        }
    }

}

private const val KEY_TOOL_TYPE = "tool_type"
private const val KEY_TOOL_COLOR = "tool_color"
private const val KEY_TOOL_SIZE = "tool_size"

private const val SAVE_DEBOUNCE_DELAY: Long = 500
