package com.tomclaw.drawa;

import android.graphics.Paint;

import java.util.List;

/**
 * Created by ivsolkin on 08.08.16.
 */
@SuppressWarnings("WeakerAccess")
public class History {

    private Paint paint;
    private List<Point> points;

    public History(Paint paint, List<Point> points) {
        this.paint = paint;
        this.points = points;
    }

    public Paint getPaint() {
        return paint;
    }

    public List<Point> getPoints() {
        return points;
    }
}
