package com.tomclaw.drawa.tools;

import android.graphics.Canvas;

import com.tomclaw.drawa.DrawHost;

/**
 * Created by solkin on 18.03.17.
 */

public abstract class Radiusable extends Tool {

    private int baseRadius;

    public Radiusable(Canvas canvas, DrawHost callback) {
        super(canvas, callback);
    }

    public int getBaseRadius() {
        return baseRadius;
    }

    public int getRadius() {
        return (int) getPaint().getStrokeWidth();
    }

    public void setBaseRadius(int radius) {
        this.baseRadius = radius;
        setRadius(radius);
    }

    public void setRadius(int radius) {
        getPaint().setStrokeWidth(radius);
    }

    public void resetRadius() {
        int baseRadius = getBaseRadius();
        setRadius(baseRadius);
    }
}
