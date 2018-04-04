package com.tomclaw.drawa.share

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.R
import com.tomclaw.drawa.util.DataProvider

class ShareAdapter(
        private val layoutInflater: LayoutInflater,
        private val dataProvider: DataProvider<ShareItem>
) : RecyclerView.Adapter<ShareItemHolder>() {

    var itemRelay: PublishRelay<ShareItem>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareItemHolder {
        val view = layoutInflater.inflate(R.layout.share_item_view, parent, false)
        return ShareItemHolder(view, itemRelay)
    }

    override fun onBindViewHolder(holder: ShareItemHolder, position: Int) {
        val item = dataProvider.getItem(position)
        holder.bind(item)
    }

    override fun getItemId(position: Int): Long = dataProvider.getItem(position).id.toLong()

    override fun getItemCount(): Int = dataProvider.size()

}