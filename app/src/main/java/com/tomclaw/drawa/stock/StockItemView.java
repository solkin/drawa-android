package com.tomclaw.drawa.stock;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;

import com.tomclaw.drawa.core.GlideApp;
import com.tomclaw.drawa.tools.AspectRatioImageView;
import com.tomclaw.drawa.R;
import com.tomclaw.drawa.dto.Image;
import com.tomclaw.drawa.dto.Size;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by solkin on 19/12/2017.
 */
@EViewGroup(R.layout.stock_item_view)
public class StockItemView extends FrameLayout {

    @ViewById
    AspectRatioImageView imageView;

    public StockItemView(@NonNull Context context) {
        super(context);
    }

    public void showImage(Image image) {
        Size remoteImageSize = image.getSize();
        float aspectRatio = (float) remoteImageSize.getHeight() / (float) remoteImageSize.getWidth();
        imageView.setAspectRatio(aspectRatio);

        String path = image.getName();

        GlideApp.with(getContext())
                .load(path)
                .placeholder(R.drawable.placeholder)
                .override(remoteImageSize.getWidth(), remoteImageSize.getHeight())
                .centerCrop()
                .into(imageView);
    }
}
