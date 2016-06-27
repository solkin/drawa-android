package com.tomclaw.drawa;

import android.graphics.Path;

/**
 * Created by Solkin on 24.12.2014.
 */
public abstract class Line implements Instrument {

    protected Path path = new Path();

    public void addPoint(Point point) {
        if(path.isEmpty()) {
            path.moveTo(point.getX(), point.getY());
        } else {
            path.lineTo(point.getX(), point.getY());
        }
    }

    @Override
    public void onEvent(float x, float y) {
        addPoint(new Point(x, y));
    }

    public Path getPath() {
        return path;
    }
}
