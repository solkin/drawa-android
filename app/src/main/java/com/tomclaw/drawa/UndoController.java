package com.tomclaw.drawa;

import android.content.Context;
import android.graphics.Bitmap;

import com.tomclaw.drawa.stack.BitmapStack;
import com.tomclaw.drawa.stack.BitmapStackItem;
import com.tomclaw.drawa.stack.Stack;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.File;

/**
 * Created by ivsolkin on 06.07.16.
 */
@EBean(scope = EBean.Scope.Singleton)
public class UndoController {

    @RootContext
    Context context;

    private BitmapStack bitmapStack;

    @AfterInject
    void init() {
        File file = new File(context.getFilesDir(), "bitmap.stack");
        bitmapStack = new BitmapStack(file);
    }

    public void add(Bitmap bitmap) {
        BitmapStackItem item = new BitmapStackItem(bitmap);
        try {
            bitmapStack.push(item);
        } catch (Stack.StackException e) {
            e.printStackTrace();
        }
    }

    public Bitmap get() {
        try {
            BitmapStackItem item = bitmapStack.peek();
            return item.getBitmap();
        } catch (Stack.StackException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap undo() {
        try {
            BitmapStackItem item = bitmapStack.pop();
            return item.getBitmap();
        } catch (Stack.StackException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clear() {
        try {
            bitmapStack.clear();
        } catch (Stack.StackException e) {
            e.printStackTrace();
        }
    }

    public boolean canUndo() {
        return !bitmapStack.isEmpty();
    }
}
