package com.tomclaw.drawa.share

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.R

class ShareTypeItemHolder(view: View,
                          private val itemRelay: PublishRelay<ShareTypeItem>?) : RecyclerView.ViewHolder(view) {

    private val imageView: ImageView = view.findViewById(R.id.type_image)
    private val titleView: TextView = view.findViewById(R.id.type_title)
    private val descriptionView: TextView = view.findViewById(R.id.type_description)
    private val selectButton: View = view.findViewById(R.id.select_button)

    fun bind(item: ShareTypeItem) {
        imageView.setImageResource(item.image)
        titleView.setText(item.title)
        descriptionView.setText(item.description)

        selectButton.setOnClickListener {
            itemRelay?.accept(item)
        }
    }
}