package com.tomclaw.drawa.stock

import android.view.View
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.R
import com.tomclaw.drawa.core.GlideApp
import com.tomclaw.drawa.util.AspectRatioImageView

class StockItemHolder(
        view: View,
        private val itemsRelay: PublishRelay<StockItem>?
) : RecyclerView.ViewHolder(view) {

    private val cardView: CardView = view.findViewById(R.id.card_view)
    private val imageView: AspectRatioImageView = view.findViewById(R.id.image_view)

    fun bind(item: StockItem) {
        val aspectRatio = item.height.toFloat() / item.width.toFloat()
        imageView.aspectRatio = aspectRatio

        cardView.setOnClickListener {
            itemsRelay?.accept(item)
        }

        GlideApp.with(imageView)
                .load(item.image)
                .centerCrop()
                .into(imageView)
    }
}
