package com.tomclaw.drawa.stock

import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ViewFlipper
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.R
import io.reactivex.Observable

interface StockView {

    fun showProgress()

    fun showContent()

    fun updateList()

    fun itemClicks(): Observable<StockItem>

    fun createClicks(): Observable<Unit>

}

class StockViewImpl(view: View,
                    val adapter: StockAdapter) : StockView {

    private val context = view.context
    private val toolbar: Toolbar = view.findViewById(R.id.toolbar)
    private val createButton: FloatingActionButton = view.findViewById(R.id.create_button)
    private val flipper: ViewFlipper = view.findViewById(R.id.flipper)
    private val recycler: RecyclerView = view.findViewById(R.id.recycler)

    private val createRelay = PublishRelay.create<Unit>()

    init {
        val layoutManager = GridLayoutManager(
                context,
                2,
                GridLayoutManager.VERTICAL,
                false
        )
        adapter.setHasStableIds(true)
        recycler.adapter = adapter
        recycler.layoutManager = layoutManager

        toolbar.setTitle(R.string.stock)

        createButton.setOnClickListener({
            createRelay.accept(Unit)
        })
    }

    override fun showProgress() {
        flipper.displayedChild = 0
    }

    override fun showContent() {
        flipper.displayedChild = 1
    }

    override fun updateList() {
        adapter.notifyDataSetChanged()
    }

    override fun itemClicks(): Observable<StockItem> {
        return Observable.empty()
    }

    override fun createClicks(): Observable<Unit> {
        return createRelay
    }

}