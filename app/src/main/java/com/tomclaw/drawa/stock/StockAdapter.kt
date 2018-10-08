package com.tomclaw.drawa.stock

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.R
import com.tomclaw.drawa.util.DataProvider

class StockAdapter(
        private val context: Context,
        private val dataProvider: DataProvider<StockItem>
) : androidx.recyclerview.widget.RecyclerView.Adapter<StockItemHolder>() {

    var itemsRelay: PublishRelay<StockItem>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockItemHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.stock_item_view, parent, false)
        return StockItemHolder(view, itemsRelay)
    }

    override fun onBindViewHolder(holder: StockItemHolder, position: Int) {
        val item = dataProvider.getItem(position)
        holder.bind(item)
    }

    override fun getItemId(position: Int): Long = dataProvider.getItem(position).id.toLong()

    override fun getItemCount(): Int = dataProvider.size()

}
