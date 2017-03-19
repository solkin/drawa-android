package com.tomclaw.drawa.tools;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Parcel;

import com.tomclaw.drawa.QueueLinearFloodFiller;

/**
 * Created by solkin on 17.03.17.
 */
public class Fill extends Tool {

    private static final int COLOR_DELTA = 0x32;

    public Fill() {
    }

    protected Fill(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Fill> CREATOR = new Creator<Fill>() {
        @Override
        public Fill createFromParcel(Parcel in) {
            return new Fill(in);
        }

        @Override
        public Fill[] newArray(int size) {
            return new Fill[size];
        }
    };

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
    public void onTouchMove(int x, int y) {

    }
}
