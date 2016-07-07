package com.tomclaw.drawa.stack;

import java.io.IOException;

/**
 * Created by ivsolkin on 05.07.16.
 */
public interface Stack<E extends StackItem> {

    void push(E item) throws StackException;
    E pop() throws StackException;
    E peek() throws StackException;
    void clear() throws StackException;
    boolean isEmpty();

    class StackException extends Exception {

        public StackException(Throwable cause) {
            super(cause);
        }
    }
}
