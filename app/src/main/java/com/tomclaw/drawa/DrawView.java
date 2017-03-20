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

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.UiThread;

import java.io.File;

import static com.tomclaw.drawa.tools.Tool.TYPE_BRUSH;
import static com.tomclaw.drawa.tools.Tool.TYPE_ERASER;
import static com.tomclaw.drawa.tools.Tool.TYPE_FILL;
import static com.tomclaw.drawa.tools.Tool.TYPE_FLUFFY;
import static com.tomclaw.drawa.tools.Tool.TYPE_MARKER;
import static com.tomclaw.drawa.tools.Tool.TYPE_PENCIL;

/**
 * Created by Solkin on 24.12.2014.
 */
@EView
public class DrawView extends View implements DrawHost {

    private Bitmap bitmap;
    private Canvas canvas;
    private Paint simplePaint;
    private Tool tool;
    private final float scaleFactor = 1.5f;
    private Rect src, dst;
    private int baseRadius = 60;

    private int selectedRadius;
    private int selectedColor;

    @Bean
    History history;

    @Bean
    Pencil pencil;

    @Bean
    Brush brush;

    @Bean
    Marker marker;

    @Bean
    Fluffy fluffy;

    @Bean
    Fill fill;

    @Bean
    Eraser eraser;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);

        selectedColor = 0xcd0219;
        selectedRadius = (int) (baseRadius / scaleFactor);

        history = new History();
        simplePaint = new Paint();
        simplePaint.setAntiAlias(true);
        simplePaint.setFilterBitmap(true);
        simplePaint.setDither(true);
    }

    public Tool selectPencil() {
        pencil.initialize(canvas, this);
        setTool(pencil);
        return tool;
    }

    public Tool selectBrush() {
        brush.initialize(canvas, this);
        setTool(brush);
        return tool;
    }

    public Tool selectMarker() {
        marker.initialize(canvas, this);
        setTool(marker);
        return tool;
    }

    public Tool selectFluffy() {
        fluffy.initialize(canvas, this);
        setTool(fluffy);
        return tool;
    }

    public Tool selectFill() {
        fill.initialize(canvas, this);
        setTool(fill);
        return tool;
    }

    public Tool selectEraser() {
        eraser.initialize(canvas, this);
        setTool(eraser);
        return tool;
    }

    public Tool getTool(int toolType) {
        switch (toolType) {
            case TYPE_PENCIL:
                return pencil;
            case TYPE_BRUSH:
                return brush;
            case TYPE_MARKER:
                return marker;
            case TYPE_FLUFFY:
                return fluffy;
            case TYPE_FILL:
                return fill;
            case TYPE_ERASER:
                return eraser;
            default:
                throw new IllegalArgumentException("unknown tool type");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap == null) {
            initBitmap();
        }
        canvas.drawBitmap(bitmap, src, dst, simplePaint);
        tool.onDraw();
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

        selectPencil();

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

    private void processToolEvent(Event event) {
        Tool tool = getTool(event.getToolType());
        tool.initialize(canvas, this);
        int color = event.getColor();
        int radius = event.getRadius();
        int action = event.getAction();
        int x = event.getX();
        int y = event.getY();
        tool.setColor(color);
        tool.setBaseRadius(radius);
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
        invalidate();
        time = System.currentTimeMillis() - time;
        Log.d("Drawa", String.format("Undo time: %d msec.", time));
    }

    void applyHistory() {
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

    public void setTool(Tool tool) {
        int color = getToolColor();
        int radius = getToolRadius();
        tool.setColor(color);
        tool.setBaseRadius(radius);
        tool.resetRadius();
        this.tool = tool;
    }

    public void setToolColor(int color) {
        selectedColor = color;
        tool.setColor(color);
    }

    public void setToolRadius(int radius) {
        selectedRadius = radius;
        tool.setBaseRadius(radius);
        tool.resetRadius();
    }

    private int getToolColor() {
        return selectedColor;
    }

    private int getToolRadius() {
        return selectedRadius;
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

    public void loadHistory() {
        File backup = getBackupFile();
        loadHistory(backup);
    }

    private void loadHistory(File file) {
        history.load(file);
        applyHistory();
        invalidate();
    }

    public void saveHistory() {
        File backup = getBackupFile();
        saveHistory(backup);
    }

    private void saveHistory(File file) {
        history.save(file);
    }
}
