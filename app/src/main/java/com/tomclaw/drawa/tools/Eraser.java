package com.tomclaw.drawa.tools;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.tomclaw.drawa.DrawHost;

/**
 * Created by solkin on 17.03.17.
 */
public class Eraser extends Radiusable {

    private int startX, startY;
    private int prevX, prevY;
    private Path path;
    private int externalColor;

    public Eraser() {
    }

    @Override
    public void onInitialize() {
        this.path = new Path();
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
    public void setColor(int color) {
        externalColor = color;
        super.setColor(0xffffffff);
    }

    @Override
    public int getColor() {
        return externalColor;
    }

    @Override
    public void onTouchDown(int x, int y) {
        startX = x;
        startY = y;
        path.moveTo(x, y);
        path.lineTo(x, y);

        prevX = x;
        prevY = y;

        drawPath(path);
    }

    @Override
    public void onTouchMove(int x, int y) {
        if (path.isEmpty()) {
            path.moveTo(prevX, prevY);
        }
        if (x == startX && y == startY) {
            path.lineTo(x + 0.1f, y);
        } else {
            path.lineTo(x, y);
        }

        prevX = x;
        prevY = y;

        drawPath(path);
    }

    @Override
    public void onTouchUp(int x, int y) {
        if (path.isEmpty()) {
            path.moveTo(prevX, prevY);
        }
        path.lineTo(x, y);

        drawPath(path);

        prevX = 0;
        prevY = 0;
    }

    @Override
    public void onDraw() {
        path.reset();
    }

}
