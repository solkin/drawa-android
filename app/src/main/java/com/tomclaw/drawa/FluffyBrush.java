package com.tomclaw.drawa;

import android.graphics.*;

/**
 * Created by Solkin on 24.12.2014.
 */
public class FluffyBrush extends Line {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(80);
        paint.setColor(0x50E6B82A);
    }

    @Override
    public void draw(Canvas canvas) {
        // List<Point> path = getPath();
        paint.setPathEffect(new DashPathEffect(new float[] {2, 2, 2, 2}, 0));
        canvas.drawPath(getPath(), paint);
    }
}
