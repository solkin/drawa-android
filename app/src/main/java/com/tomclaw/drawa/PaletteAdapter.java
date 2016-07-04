package com.tomclaw.drawa;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivsolkin on 27.06.16.
 */
public class PaletteAdapter extends RecyclerView.Adapter<PaletteAdapter.PaletteItem> {

    private List<Integer> colors = new ArrayList<>();
    private LayoutInflater inflater;
    private PaletteClickListener listener;

    public PaletteAdapter(Context context, List<Integer> colors) {
        this.colors.addAll(colors);
        this.inflater = LayoutInflater.from(context);
    }

    public void setListener(PaletteClickListener listener) {
        this.listener = listener;
    }

    @Override
    public PaletteItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.palette_item, parent, false);
        return new PaletteItem(view);
    }

    @Override
    public void onBindViewHolder(PaletteItem holder, int position) {
        holder.bind(colors.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public interface PaletteClickListener {

        void onColorClicked(int color);
    }

    public static class PaletteItem extends RecyclerView.ViewHolder {

        private PaletteImageView paletteImage;
        private View itemView;

        public PaletteItem(View itemView) {
            super(itemView);
            this.itemView = itemView;
            paletteImage = (PaletteImageView) itemView.findViewById(R.id.palette_image);
            paletteImage.setImageResource(R.drawable.palette_item);
        }

        public void bind(final int color, final PaletteClickListener listener) {
            paletteImage.setColorFilter(color);
            paletteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onColorClicked(color);
                    }
                }
            });
        }
    }
}
