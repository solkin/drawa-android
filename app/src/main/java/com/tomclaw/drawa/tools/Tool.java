package com.tomclaw.drawa.tools;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.tomclaw.drawa.DrawHost;

/**
 * Created by solkin on 17.03.17.
 */
public abstract class Tool {

    public static final byte TYPE_PENCIL = 0x01;
    public static final byte TYPE_BRUSH = 0x02;
    public static final byte TYPE_MARKER = 0x03;
    public static final byte TYPE_FLUFFY = 0x04;
    public static final byte TYPE_FILL = 0x05;
    public static final byte TYPE_ERASER = 0x06;

    private Canvas canvas;
    private DrawHost callback;
    private Paint paint;

    public final void initialize(Canvas canvas, DrawHost callback) {
        this.canvas = canvas;
        this.callback = callback;
        this.paint = initPaint();
        onInitialize();
    }

    public boolean isInitialized() {
        return canvas != null && callback != null;
    }

    abstract void onInitialize();

    abstract Paint initPaint();

    Paint getPaint() {
        return paint;
    }

    abstract int getAlpha();

    public void setColor(int color) {
        paint.setColor(0xffffffff);
        paint.setColor(Color.argb(getAlpha(), Color.red(color), Color.green(color), Color.blue(color)));
    }

    public int getColor() {
        int color = paint.getColor();
        return Color.rgb(Color.red(color), Color.green(color), Color.blue(color));
    }

    public Bitmap getBitmap() {
        return callback.getBitmap();
    }

    public abstract void onTouchDown(int x, int y);

    public abstract void onTouchMove(int x, int y);

    public abstract void onTouchUp(int x, int y);

    void drawPath(Path path) {
        canvas.drawPath(path, paint);
    }

    public abstract void onDraw();

    public abstract byte getType();

}
