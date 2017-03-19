package com.tomclaw.drawa.tools;

/**
 * Created by solkin on 18.03.17.
 */

public abstract class Radiusable extends Tool {

    private int baseRadius;

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
