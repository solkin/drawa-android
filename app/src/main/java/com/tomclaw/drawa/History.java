package com.tomclaw.drawa;

import android.util.Log;
import android.view.MotionEvent;

import com.tomclaw.drawa.tools.Tool;

import org.androidannotations.annotations.EBean;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

/**
 * Created by solkin on 20.03.17.
 */
@EBean
public class History {

    private static final int BACKUP_VERSION = 0x01;

    private Stack<Event> events = new Stack<>();
    private int eventIndex = 0;

    public Event add(Tool tool, int x, int y, int action) {
        if (action == MotionEvent.ACTION_DOWN) {
            eventIndex++;
        }
        Event e = new Event(eventIndex, tool.getType(), tool.getColor(), tool.getBaseRadius(), x, y, action);
        return events.push(e);
    }

    public void undo() {
        while (!events.empty() && events.peek().getIndex() == eventIndex) {
            events.pop();
        }
        eventIndex--;
    }

    public void clear() {
        eventIndex = 0;
        events.clear();
    }

    public Collection<Event> getEvents() {
        return events;
    }

    public void save(File file) {
        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(file));
            output.writeInt(BACKUP_VERSION);
            output.writeInt(eventIndex);
            output.writeInt(events.size());
            for (Event event : events) {
                output.writeInt(event.getIndex());
                output.writeByte(event.getToolType());
                output.writeInt(event.getColor());
                output.writeInt(event.getRadius());
                output.writeInt(event.getX());
                output.writeInt(event.getY());
                output.writeInt(event.getAction());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ignored) {
                }
            }
        }
        Log.d("Drawa", String.format("total %d bytes written", file.length()));
    }

    public void load(File file) {
        clear();
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(file));
            int backupVersion = input.readInt();
            if (backupVersion == 0x01) {
                int eventIndex = input.readInt();
                int eventsCount = input.readInt();
                List<Event> eventList = new ArrayList<>();
                for (int c = 0; c < eventsCount; c++) {
                    int index = input.readInt();
                    byte toolType = input.readByte();
                    int color = input.readInt();
                    int radius = input.readInt();
                    int x = input.readInt();
                    int y = input.readInt();
                    int action = input.readInt();
                    Event event = new Event(index, toolType, color, radius, x, y, action);
                    eventList.add(event);
                }
                this.eventIndex = eventIndex;
                this.events.addAll(eventList);
            } else {
                throw new IOException("backup format of unknown version");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
