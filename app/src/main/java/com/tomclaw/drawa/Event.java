package com.tomclaw.drawa;

import android.os.Parcel;
import android.os.Parcelable;

import com.tomclaw.drawa.tools.Tool;

/**
 * Created by solkin on 19.03.17.
 */
public class Event implements Parcelable {

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

    protected Event(Parcel in) {
        index = in.readInt();
        tool = in.readParcelable(Tool.class.getClassLoader());
        color = in.readInt();
        x = in.readInt();
        y = in.readInt();
        action = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(index);
        dest.writeParcelable(tool, flags);
        dest.writeInt(color);
        dest.writeInt(x);
        dest.writeInt(y);
        dest.writeInt(action);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

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
