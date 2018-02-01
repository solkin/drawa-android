package com.tomclaw.drawa.stock

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.tomclaw.drawa.R
import com.tomclaw.drawa.util.DataProvider

class StockAdapter(
        private val context: Context,
        private val dataProvider: DataProvider<StockItem>
) : RecyclerView.Adapter<StockItemHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockItemHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.stock_item_view, parent, false)
        return StockItemHolder(view)

    }

    override fun onBindViewHolder(holder: StockItemHolder, position: Int) {
        val item = dataProvider.getItem(position)
        holder.bind(item)
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemCount(): Int = dataProvider.size()
}
