package com.tomclaw.drawa;

import com.tomclaw.drawa.tools.Tool;

/**
 * Created by solkin on 19.03.17.
 */
public class Event {

    private int index;
    private Tool tool;
    private int color;
    private int x;
    private int y;
    private int action;

    public Event(int index, Tool tool, int color, int x, int y, int action) {
        this.index = index;
        this.tool = tool;
        this.color = color;
        this.x = x;
        this.y = y;
        this.action = action;
    }

    public int getIndex() {
        return index;
    }

    public Tool getTool() {
        return tool;
    }

    public int getColor() {
        return color;
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
