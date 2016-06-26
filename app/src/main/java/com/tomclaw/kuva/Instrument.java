package com.tomclaw.kuva;

import android.graphics.Canvas;

/**
 * Created by Solkin on 24.12.2014.
 */
public interface Instrument {

    public void draw(Canvas canvas);

    public void onEvent(float x, float y);
}
