package com.tomclaw.drawa.stock;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solkin on 19/12/2017.
 */
public class StockAdapter extends RecyclerView.Adapter<StockItemHolder> {

    private Context context;
    private final List<StockItem> items;
    private StockItemClickListener listener;

    public StockAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
        setHasStableIds(true);
    }

    public void setItems(List<StockItem> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public void setClickListener(StockItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public StockItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        StockItemView stockView = null; // StockItemView_.build(context);
        return new StockItemHolder(stockView);

    }

    @Override
    public void onBindViewHolder(StockItemHolder holder, int position) {
        StockItem item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
