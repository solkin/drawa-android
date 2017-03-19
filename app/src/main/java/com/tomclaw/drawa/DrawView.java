package com.tomclaw.drawa;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.tomclaw.drawa.tools.Brush;
import com.tomclaw.drawa.tools.Eraser;
import com.tomclaw.drawa.tools.Fill;
import com.tomclaw.drawa.tools.Fluffy;
import com.tomclaw.drawa.tools.Marker;
import com.tomclaw.drawa.tools.Pencil;
import com.tomclaw.drawa.tools.Tool;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EView;

import java.io.File;

/**
 * Created by Solkin on 24.12.2014.
 */
@EView
public class DrawView extends View implements DrawHost {

    private Bitmap stub;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint simplePaint;
    private Tool tool;
    private final float scaleFactor = 1.5f;
    private Rect src, dst;
    private int baseRadius = 60;
    private History history;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);

        history = new History();
        simplePaint = new Paint();
        simplePaint.setAntiAlias(true);
        simplePaint.setFilterBitmap(true);
        simplePaint.setDither(true);
    }

    public void initPencil() {
        Pencil pencil = new Pencil();
        pencil.initialize(canvas, this);
        pencil.setBaseRadius((int) (baseRadius / scaleFactor));
        setTool(pencil);
    }

    public void initBrush() {
        Brush brush = new Brush();
        brush.initialize(canvas, this);
        brush.setBaseRadius((int) (baseRadius / scaleFactor));
        setTool(brush);
    }

    public void initMarker() {
        Marker marker = new Marker();
        marker.initialize(canvas, this);
        marker.setBaseRadius((int) (baseRadius / scaleFactor));
        setTool(marker);
    }

    public void initFluffy() {
        Fluffy fluffy = new Fluffy();
        fluffy.initialize(canvas, this);
        fluffy.setBaseRadius((int) (baseRadius / scaleFactor));
        setTool(fluffy);
    }

    public void initFill() {
        Fill fill = new Fill();
        fill.initialize(canvas, this);
        setTool(fill);
    }

    public void initEraser() {
        Eraser eraser = new Eraser();
        eraser.initialize(canvas, this);
        eraser.setBaseRadius((int) (baseRadius / scaleFactor));
        setTool(eraser);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (stub != null) {
            canvas.drawBitmap(stub, src, dst, simplePaint);
        } else {
            if (bitmap == null) {
                initBitmap();
            }
            canvas.drawBitmap(bitmap, src, dst, simplePaint);
            tool.onDraw();
        }
    }

    private void initBitmap() {
        bitmap = Bitmap.createBitmap(
                (int) (getWidth() / scaleFactor),
                (int) (getHeight() / scaleFactor),
                Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        dst = new Rect(0, 0, getWidth(), getHeight());

        initPencil();

        loadHistory();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int eventX = (int) (event.getX() / scaleFactor);
        int eventY = (int) (event.getY() / scaleFactor);
        Event e = history.add(tool, eventX, eventY, event.getAction());
        processToolEvent(e);
        invalidate();
        return true;
    }

    private static void processToolEvent(Event event) {
        Tool tool = event.getTool();
        int color = event.getColor();
        int action = event.getAction();
        int x = event.getX();
        int y = event.getY();
        tool.setColor(color);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                tool.onTouchDown(x, y);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                tool.onTouchMove(x, y);
                break;
            }
            case MotionEvent.ACTION_UP: {
                tool.onTouchUp(x, y);
                break;
            }
        }
        tool.onDraw();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    public void undo() {
        long time = System.currentTimeMillis();
        history.undo();
        applyHistory();
        time = System.currentTimeMillis() - time;
        Log.d("Drawa", String.format("Undo time: %d msec.", time));
    }

    private void applyHistory() {
        canvas.drawColor(Color.WHITE);
        for (Event event : history.getEvents()) {
            processToolEvent(event);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    public void reset() {
        history.clear();
        clear();
        invalidate();
    }

    public void setupStub() {
        stub = Bitmap.createBitmap(bitmap);
    }

    public void removeStub() {
        stub.recycle();
        stub = null;
    }

    public void setTool(Tool tool) {
        int color = getToolColor();
        tool.setColor(color);
        this.tool = tool;
    }

    public void setToolColor(int color) {
        tool.setColor(color);
    }

    private int getToolColor() {
        return tool == null ? 0xcd0219 : tool.getColor();
    }

    private void clear() {
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }

    File getBackupFile() {
        return new File(getContext().getFilesDir(), "backup.dat");
    }

    @Background
    public void loadHistory() {
        File backup = getBackupFile();
        loadHistory(backup);
    }

    private void loadHistory(File file) {
        history.load(file, canvas, this);
        applyHistory();
    }

    @Background
    public void saveHistory() {
        File backup = getBackupFile();
        saveHistory(backup);
    }

    private void saveHistory(File file) {
        history.save(file);
    }
}
