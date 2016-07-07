package com.tomclaw.drawa.stack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ivsolkin on 06.07.16.
 */

public class BitmapStackItem implements StackItem {

    private Bitmap bitmap;

    public BitmapStackItem() {
    }

    public BitmapStackItem(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public void write(OutputStream output) throws IOException {
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, output);
    }

    @Override
    public void read(InputStream input) throws IOException {
        bitmap = BitmapFactory.decodeStream(input);
    }
}
