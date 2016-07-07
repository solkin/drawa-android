package com.tomclaw.drawa.stack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ivsolkin on 05.07.16.
 */

public interface StackItem {

    void write(OutputStream output) throws IOException;

    void read(InputStream input) throws IOException;
}
