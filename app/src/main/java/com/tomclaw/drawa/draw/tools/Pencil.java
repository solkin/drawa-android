package com.tomclaw.drawa.draw.tools;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by solkin on 17.03.17.
 */
//@EBean
public class Pencil extends Tool {

    private int startX, startY;
    private int prevX, prevY;
    private Path path;

    Pencil() {
    }

    @Override
    void onInitialize() {
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
            path.quadTo(prevX, prevY, (x + prevX) / 2, (y + prevY) / 2);
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
        path.quadTo(prevX, prevY, x, y);

        path.reset();

        drawPath(path);

        prevX = 0;
        prevY = 0;
    }

    @Override
    public void onDraw() {
    }

    @Override
    public byte getType() {
        return TYPE_PENCIL;
    }

}
