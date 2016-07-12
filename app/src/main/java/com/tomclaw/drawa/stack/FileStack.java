package com.tomclaw.drawa.stack;

import android.util.Log;
import android.util.SparseArray;

import com.tomclaw.drawa.HexHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

/**
 * Created by ivsolkin on 05.07.16.
 */
public abstract class FileStack<E extends StackItem> implements Stack<E> {

    private File file;
    private final List<Integer> offsets;
    private RandomAccessFile stream;

    protected FileStack(File file) {
        this.file = file;
        this.offsets = new ArrayList<>();
    }

    private RandomAccessFile getStream() throws StackException {
        try {
            if (stream == null) {
                stream = new RandomAccessFile(file, "rw");
                scan();
            }
            long offset = getLastOffset();
            stream.seek(offset);
        } catch (IOException ex) {
            throw new StackException(ex);
        }
        return stream;
    }

    private int getLastOffset() {
        int offset = 0;
        if (!offsets.isEmpty()) {
            offset = offsets.get(offsets.size() - 1);
        }
        return offset;
    }

    private void scan() throws IOException {
        synchronized (offsets) {
            stream.seek(0);
            offsets.clear();
            int size, offset = 0;
            try {
                offsets.add(0);
                while ((size = stream.readInt()) != -1) {
                    offset += 4;
                    offset += size;
                    offsets.add(offset);
                    stream.seek(offset);
                }
            } catch (EOFException ignored) {
            }
        }
    }

    @Override
    public void push(E item) throws StackException {
        try {
            RandomAccessFile output = getStream();

            ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
            item.write(arrayStream);

            byte[] data = arrayStream.toByteArray();

            output.writeInt(data.length);
            output.write(data);

            int offset = getLastOffset();
            offset += 4 + data.length;
            offsets.add(offset);

            arrayStream.close();
        } catch (IOException ex) {
            throw new StackException(ex);
        }
    }

    @Override
    public E pop() throws StackException {
        synchronized (offsets) {
            if (isEmpty()) {
                throw new StackException(new EmptyStackException());
            }
            E item = createItem();
            try {
                int offset = offsets.remove(offsets.size() - 1);
                RandomAccessFile input = getStream();

                int size = input.readInt();
                byte[] data = new byte[size];
                input.readFully(data);

                ByteArrayInputStream arrayStream = new ByteArrayInputStream(data);

                item.read(arrayStream);

                arrayStream.close();

                input.getChannel().truncate(offset - 4 - size);
            } catch (IOException ex) {
                throw new StackException(ex);
            }
            return item;
        }
    }

    @Override
    public E peek() throws StackException {
        synchronized (offsets) {
            E item = createItem();
            try {
                RandomAccessFile input = getStream();
                if (isEmpty()) {
                    throw new StackException(new EmptyStackException());
                }
                int offset = offsets.get(offsets.size() - 2);
                input.seek(offset);

                int size = input.readInt();
                byte[] data = new byte[size];
                input.readFully(data);

                ByteArrayInputStream arrayStream = new ByteArrayInputStream(data);

                item.read(arrayStream);

                arrayStream.close();
            } catch (IOException ex) {
                throw new StackException(ex);
            }
            return item;
        }
    }

    public void clear() throws StackException {
        synchronized (offsets) {
            RandomAccessFile stream = getStream();
            offsets.clear();
            offsets.add(0);
            try {
                stream.getChannel().truncate(0);
            } catch (IOException ex) {
                throw new StackException(ex);
            }
        }
    }

    public boolean isEmpty() {
        int offset = getLastOffset();
        return offset == 0;
    }

    public abstract E createItem();
}
