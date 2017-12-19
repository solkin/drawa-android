package com.tomclaw.drawa.stock;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by solkin on 19/12/2017.
 */
public class StockItemHolder extends RecyclerView.ViewHolder {

    private StockItemView itemView;

    public StockItemHolder(StockItemView itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    public void bind(StockItem item) {
        itemView.showImage(item.getImage());
    }
}
