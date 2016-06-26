package com.tomclaw.kuva;

/**
 * Created by Solkin on 24.12.2014.
 */
public class Point {

    private float x, y;
    public long time;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(float x, float y) {
        this((int) x, (int) y);
    }

    public Point(float x, float y, long time){
        this.x = x;
        this.y = y;
        this.time = time;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    private float distanceTo(Point start) {
        return (float) (Math.sqrt(Math.pow((x - start.x), 2) + Math.pow((y - start.y), 2)));
    }

    public float velocityFrom(Point start) {
        return distanceTo(start) / (this.time - start.time);
    }
}
