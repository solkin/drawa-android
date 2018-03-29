package com.tomclaw.drawa.share

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.R
import com.tomclaw.drawa.util.DataProvider

class ShareTypeAdapter(
        private val context: Context,
        private val dataProvider: DataProvider<ShareTypeItem>
) : RecyclerView.Adapter<ShareTypeItemHolder>() {

    var itemRelay: PublishRelay<ShareTypeItem>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareTypeItemHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.share_type_view, parent, false)
        return ShareTypeItemHolder(view, itemRelay)
    }

    override fun onBindViewHolder(holder: ShareTypeItemHolder, position: Int) {
        val item = dataProvider.getItem(position)
        holder.bind(item)
    }

    override fun getItemId(position: Int): Long = dataProvider.getItem(position).id.toLong()

    override fun getItemCount(): Int = dataProvider.size()

}