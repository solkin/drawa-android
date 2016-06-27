package com.tomclaw.drawa;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Solkin on 25.12.2014.
 */
public class PaletteImageView extends ImageView {
    public PaletteImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaletteImageView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
