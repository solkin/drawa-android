package com.tomclaw.drawa.draw.tools;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by solkin on 17.03.17.
 */
//@EBean
public class Brush extends Tool {

    private static final float RADIUS_MULTIPLIER = 2;

    private int startX, startY;
    private int prevX, prevY;
    private Path path;

    Brush() {
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
    public void onTouchDown(int x, int y) {
        startX = x;
        startY = y;
        resetRadius();

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
        path.quadTo(prevX, prevY, (x + prevX) / 2, (y + prevY) / 2);

        int deltaX = Math.abs(x - prevX);
        int deltaY = Math.abs(y - prevY);
        double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        int radius = getRadius();
        if (length < getBaseRadius() / 5) {
            radius += 2;

            path.reset();
            path.moveTo(prevX, prevY);
            path.lineTo(x, y);
        } else {
            radius -= 2;
        }
        if (radius > (getBaseRadius() / RADIUS_MULTIPLIER) && radius < (getBaseRadius() * RADIUS_MULTIPLIER)) {
            setRadius(radius);
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
        if (x == startX && y == startY) {
            path.lineTo(x + 0.1f, y);
        } else {
            path.lineTo(x, y);
        }

        drawPath(path);

        prevX = 0;
        prevY = 0;

        path.reset();
    }

    @Override
    public void onDraw() {
    }

    @Override
    public byte getType() {
        return TYPE_BRUSH;
    }
}
