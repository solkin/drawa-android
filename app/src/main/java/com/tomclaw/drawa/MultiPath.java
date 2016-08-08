package com.tomclaw.drawa;

import android.graphics.Path;

/**
 * Created by ivsolkin on 08.08.16.
 */
public class MultiPath {

    private final Path[] paths;

    public MultiPath(int count) {
        paths = new Path[count];
        for (int c = 0; c < count; c++) {
            paths[c] = new Path();
        }
    }

    public Path get(int index) {
        return paths[index];
    }

    public void moveTo(float x, float y) {
        for (Path path : paths) {
            path.moveTo(x, y);
        }
    }

    public void lineTo(float x, float y) {
        for (Path path : paths) {
            path.lineTo(x, y);
        }
    }
}
