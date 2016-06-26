package com.tomclaw.kuva;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Solkin on 24.12.2014.
 */
public class SketchView extends View {

    private float VELOCITY_FILTER_WEIGHT = 0.2f;

    private List<Bitmap> backup;

    private Paint paint;

    private float prevX, prevY;
    private Point prevPoint;
    private float prevVelocity;
    private int trackLength;
    private Path path;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint simplePaint;

    private final int scaleFactor = 1;

    private Rect src, dst;

    private int radius;

    public SketchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        backup = new ArrayList<>();

        // initFluffy();
        initPencil();

        radius(80 / scaleFactor);
        color(0xE6B82A);

        path = new Path();
        simplePaint = new Paint();
        simplePaint.setAntiAlias(true);
        simplePaint.setFilterBitmap(true);
        simplePaint.setDither(true);
    }

    private void initFluffy() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{2, 0}, 0));
    }

    private void initPencil() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(bitmap == null) {
            initBitmap();
        }

        canvas.drawBitmap(bitmap, src, dst, simplePaint);
        path.reset();
    }

    private void initBitmap() {
        bitmap = Bitmap.createBitmap(getWidth() / scaleFactor, getHeight() / scaleFactor,
                Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        dst = new Rect(0, 0, getWidth(), getHeight());
    }

    private void cloneBitmap() {
        bitmap = Bitmap.createBitmap(bitmap);
        canvas = new Canvas(bitmap);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float eventX = event.getX() / scaleFactor;
        float eventY = event.getY() / scaleFactor;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                trackLength = 0;
                backup.add(bitmap);
                cloneBitmap();
                path.moveTo(eventX, eventY);
                break;
            }
            case MotionEvent.ACTION_UP: {
                prevX = 0;
                prevY = 0;
                prevPoint = null;
                path.reset();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if(path.isEmpty()) {
                    path.moveTo(prevX, prevY);
                }
                path.lineTo(eventX, eventY);

                /*int radius = 100 / scaleFactor - trackLength;
                if(radius < scaleFactor * 10) {
                    radius = scaleFactor * 10;
                }
                radius(radius);*/

                canvas.drawPath(path, paint);
                path.reset();

                prevX = eventX;
                prevY = eventY;
                trackLength ++;

                invalidate();
                break;
            }
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    public void undo() {
        if(!backup.isEmpty()) {
            bitmap = backup.remove(backup.size() - 1);
            canvas = new Canvas(bitmap);
            invalidate();
        }
    }

    public void reset() {
        if(!backup.isEmpty()) {
            bitmap = backup.get(0);
            backup.clear();
            canvas = new Canvas(bitmap);
            invalidate();
        }
    }

    public void color(int color) {
        paint.setColor(0xffffffff);
        paint.setColor(Color.argb(0x50, Color.red(color), Color.green(color), Color.blue(color)));
    }

    public void radius(int radius) {
        paint.setStrokeWidth(radius);
    }

    public int getRadius() {
        return (int) paint.getStrokeWidth();
    }
}
