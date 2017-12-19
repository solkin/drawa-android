package com.tomclaw.drawa.draw;

/**
 * Created by solkin on 19.03.17.
 */
public class Event {

    private int index;
    private int toolType;
    private int color;
    private int radius;
    private int x;
    private int y;
    private int action;

    public Event(int index, int toolType, int color, int radius, int x, int y, int action) {
        this.index = index;
        this.toolType = toolType;
        this.color = color;
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.action = action;
    }

    public int getIndex() {
        return index;
    }

    public int getToolType() {
        return toolType;
    }

    public int getColor() {
        return color;
    }

    public int getRadius() {
        return radius;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getAction() {
        return action;
    }
}
