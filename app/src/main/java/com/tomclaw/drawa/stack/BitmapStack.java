package com.tomclaw.drawa.stack;

import java.io.File;

/**
 * Created by ivsolkin on 05.07.16.
 */
public class BitmapStack extends FileStack<BitmapStackItem> {

    public BitmapStack(File file) {
        super(file);
    }

    @Override
    public BitmapStackItem createItem() {
        return new BitmapStackItem();
    }
}
