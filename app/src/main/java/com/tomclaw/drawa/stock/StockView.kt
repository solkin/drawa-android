package com.tomclaw.drawa.stock

import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
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

    fun infoClicks(): Observable<Unit>

}

class StockViewImpl(view: View,
                    private val adapter: StockAdapter) : StockView {

    private val context = view.context
    private val toolbar: Toolbar = view.findViewById(R.id.toolbar)
    private val createButton: FloatingActionButton = view.findViewById(R.id.create_button)
    private val flipper: ViewFlipper = view.findViewById(R.id.flipper)
    private val recycler: androidx.recyclerview.widget.RecyclerView = view.findViewById(R.id.recycler)

    private val itemsRelay = PublishRelay.create<StockItem>()
    private val createRelay = PublishRelay.create<Unit>()
    private val infoRelay = PublishRelay.create<Unit>()

    init {
        val layoutManager = androidx.recyclerview.widget.GridLayoutManager(
                context,
                2,
                androidx.recyclerview.widget.GridLayoutManager.VERTICAL,
                false
        )
        adapter.setHasStableIds(true)
        adapter.itemsRelay = itemsRelay
        recycler.adapter = adapter
        recycler.layoutManager = layoutManager

        toolbar.setTitle(R.string.stock)
        toolbar.inflateMenu(R.menu.stock)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_info -> infoRelay.accept(Unit)
            }
            true
        }

        createButton.setOnClickListener {
            createRelay.accept(Unit)
        }
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
        return itemsRelay
    }

    override fun createClicks(): Observable<Unit> {
        return createRelay
    }

    override fun infoClicks(): Observable<Unit> {
        return infoRelay
    }

}