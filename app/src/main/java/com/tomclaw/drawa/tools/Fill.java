package com.tomclaw.drawa.tools;

import android.graphics.Bitmap;
import android.graphics.Paint;

import com.tomclaw.drawa.QueueLinearFloodFiller;

import org.androidannotations.annotations.EBean;

/**
 * Created by solkin on 17.03.17.
 */
@EBean
public class Fill extends Tool {

    private static final int COLOR_DELTA = 0x32;

    Fill() {
    }

    @Override
    void onInitialize() {
    }

    @Override
    Paint initPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        return paint;
    }

    @Override
    int getAlpha() {
        return 0xff;
    }

    @Override
    public void onTouchDown(int x, int y) {
        int color = getColor();
        Bitmap bitmap = getBitmap();
        int pixel = bitmap.getPixel(x, y);
        QueueLinearFloodFiller filler = new QueueLinearFloodFiller(bitmap, pixel, color);
        filler.setTolerance(COLOR_DELTA);
        filler.floodFill(x, y);
    }

    @Override
    public void onTouchUp(int x, int y) {

    }

    @Override
    public void onDraw() {

    }

    @Override
    public byte getType() {
        return TYPE_FILL;
    }

    @Override
    public void onTouchMove(int x, int y) {

    }
}
