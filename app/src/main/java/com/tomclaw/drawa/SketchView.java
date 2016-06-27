package com.tomclaw.drawa;

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

    private List<Bitmap> backup;

    private Paint paint;

    private float prevX, prevY;
    private Point prevPoint;
    private Path path;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint simplePaint;

    private final float scaleFactor = 1.5f;

    private Rect src, dst;

    private int alpha = 0x50;
    private boolean isVarRadius = false;

    private int baseRadius = 60;

    public SketchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        backup = new ArrayList<>();

        initPencil();
//        initBrush();
//        initMarker();
//        initFluffy();
//        initEraser();

        radius(baseRadius / scaleFactor);
        color(0xcd0219);

        path = new Path();
        simplePaint = new Paint();
        simplePaint.setAntiAlias(true);
        simplePaint.setFilterBitmap(true);
        simplePaint.setDither(true);
    }

    private void initMarker() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.MITER);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setPathEffect(new DashPathEffect(new float[]{2, 0}, 0));
        alpha = 0x50;
        isVarRadius = false;
    }

    private void initBrush() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        alpha = 0xff;
        isVarRadius = true;
    }

    private void initPencil() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        alpha = 0xff;
        isVarRadius = false;
    }

    private void initFluffy() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.MITER);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStrokeMiter(0.2f);
        paint.setPathEffect(new DiscretePathEffect(2, 2));
        alpha = 0x20;
        isVarRadius = false;
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
        bitmap = Bitmap.createBitmap(
                (int) (getWidth() / scaleFactor),
                (int) (getHeight() / scaleFactor),
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
                prevPoint = null;
                radius(baseRadius / scaleFactor);
                backup.add(bitmap);
                cloneBitmap();
                path.moveTo(eventX, eventY);
                break;
            }
            case MotionEvent.ACTION_UP: {
                if(path.isEmpty()) {
                    path.moveTo(prevX, prevY);
                }
                path.lineTo(eventX+1, eventY);
                canvas.drawPath(path, paint);
                path.reset();
                invalidate();

                prevX = 0;
                prevY = 0;
                prevPoint = null;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if(path.isEmpty()) {
                    path.moveTo(prevX, prevY);
                }
                path.lineTo(eventX, eventY);

                if (isVarRadius) {
                    float absLength = (baseRadius / scaleFactor) - (Math.abs(eventX - prevX) + Math.abs(eventY - prevY));
                    float r = getRadius();
                    if (r < absLength) {
                        r += 1;
                    } else {
                        r -= 1;
                    }
                    if (r > 10 && r < (baseRadius / scaleFactor) && prevPoint != null) {
                        radius(r);
                    }
                }

                canvas.drawPath(path, paint);
                path.reset();

                prevX = eventX;
                prevY = eventY;

                if (prevPoint == null) {
                    prevPoint = new Point(eventX, eventY);
                } else {
                    prevPoint.setX(eventX);
                    prevPoint.setY(eventY);
                }

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
        paint.setColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
    }

    public void radius(float radius) {
        paint.setStrokeWidth(radius);
    }

    public float getRadius() {
        return paint.getStrokeWidth();
    }
}
