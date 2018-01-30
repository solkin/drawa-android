package com.tomclaw.drawa.stock;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.tomclaw.drawa.R;
import com.tomclaw.drawa.dto.Image;
import com.tomclaw.drawa.dto.Size;
import com.tomclaw.drawa.util.AspectRatioImageView;

/**
 * Created by solkin on 19/12/2017.
 */
//@EViewGroup(R.layout.stock_item_view)
public class StockItemView extends FrameLayout {

//    @ViewById
    CardView cardView;

//    @ViewById
    AspectRatioImageView imageView;

    public StockItemView(@NonNull Context context) {
        super(context);
    }

    public void showImage(Image image) {
        if (image.isEmpty()) {
            imageView.setImageResource(R.drawable.plus);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
        } else {
            Size remoteImageSize = image.getSize();
            float aspectRatio = (float) remoteImageSize.getHeight() / (float) remoteImageSize.getWidth();
            imageView.setAspectRatio(aspectRatio);

            String path = image.getName();

//            GlideApp.with(getContext())
//                    .load(path)
//                    .centerCrop()
//                    .override(remoteImageSize.getWidth(), remoteImageSize.getHeight())
//                    .centerCrop()
//                    .into(imageView);
        }
    }

    public void setClickListener(OnClickListener listener) {
        cardView.setOnClickListener(listener);
    }
}
