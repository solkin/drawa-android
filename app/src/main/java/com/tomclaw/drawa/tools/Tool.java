package com.tomclaw.drawa.tools;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.tomclaw.drawa.DrawCallback;

/**
 * Created by solkin on 17.03.17.
 */
public abstract class Tool {

    private Canvas canvas;
    private DrawCallback callback;
    private Paint paint;

    public Tool(Canvas canvas, DrawCallback callback) {
        this.canvas = canvas;
        this.callback = callback;
        this.paint = initPaint();
    }

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

    public abstract void onTouchDown(int x, int y);

    public abstract void onTouchMove(int x, int y);

    public abstract void onTouchUp(int x, int y);

    void drawPath(Path path) {
        canvas.drawPath(path, paint);
        callback.invalidate();
    }

    public abstract void onDraw();

}
