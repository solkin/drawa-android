package com.tomclaw.drawa;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by ivsolkin on 08.08.16.
 */

public class Stroke {

    private Path path;
    private Paint paint;

    public Stroke(Path path, Paint paint) {
        this.path = path;
        this.paint = paint;
    }

    public Path getPath() {
        return path;
    }

    public Paint getPaint() {
        return paint;
    }
}
