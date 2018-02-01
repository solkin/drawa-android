package com.tomclaw.drawa.stock

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import com.tomclaw.drawa.R
import com.tomclaw.drawa.util.AspectRatioImageView

/**
 * Created by solkin on 19/12/2017.
 */
class StockItemHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val cardView: CardView = view.findViewById(R.id.card_view)
    private val imageView: AspectRatioImageView = view.findViewById(R.id.image_view)

    fun bind(item: StockItem) {
        val remoteImageSize = item.image.size
        val aspectRatio = remoteImageSize.height.toFloat() / remoteImageSize.width.toFloat()
        imageView.aspectRatio = aspectRatio

        val path = item.image.name

        //            GlideApp.with(getContext())
        //                    .load(path)
        //                    .centerCrop()
        //                    .override(remoteImageSize.getWidth(), remoteImageSize.getHeight())
        //                    .centerCrop()
        //                    .into(imageView);
    }
}
