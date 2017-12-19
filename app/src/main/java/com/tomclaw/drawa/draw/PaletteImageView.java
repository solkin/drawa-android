package com.tomclaw.drawa.draw;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by Solkin on 25.12.2014.
 */
public class PaletteImageView extends AppCompatImageView {
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
