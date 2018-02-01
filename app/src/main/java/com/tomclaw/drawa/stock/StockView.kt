package com.tomclaw.drawa.stock

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ViewFlipper
import com.tomclaw.drawa.R
import io.reactivex.Observable

interface StockView {

    fun showProgress()

    fun showContent()

    fun updateList()

    fun itemClicks(): Observable<StockItem>

}

class StockViewImpl(view: View,
                    val adapter: StockAdapter) : StockView {

    private val context = view.context
    private val flipper: ViewFlipper = view.findViewById(R.id.flipper)
    private val recycler: RecyclerView = view.findViewById(R.id.recycler)

    init {
        val layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
        )
        adapter.setHasStableIds(true)
        recycler.adapter = adapter
        recycler.layoutManager = layoutManager
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

}