package com.tomclaw.drawa;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tomclaw.drawa.tools.Brush;
import com.tomclaw.drawa.tools.Fluffy;
import com.tomclaw.drawa.tools.Marker;
import com.tomclaw.drawa.tools.Pencil;
import com.tomclaw.drawa.tools.Tool;

/**
 * Created by Solkin on 24.12.2014.
 */
public class SketchView extends View implements DrawCallback {

    //    private Paint paint;
//
//    private float prevX, prevY;
//    private Point prevPoint;
//    private Path path;
//    private List<Point> points;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint simplePaint;

    private Tool tool;

    private final float scaleFactor = 1.5f;

    private Rect src, dst;

//    private int alpha = 0x50;
//    private boolean isVarRadius = false;
//    private boolean isFill = false;

    private int baseRadius = 60;
//
//    private Stack<History> stack;
//
//    private final int DRAW = 0;
//    private final int BACK = 1;
//
//    private int color = 0xcd0219;
//
//    private final int COLOR_DELTA = 0x32;

    public SketchView(Context context, AttributeSet attrs) {
        super(context, attrs);

//        stack = new Stack<>();

        initPencil();
//        initBrush();
//        initMarker();
//        initFluffy();
//        initEraser();

//        setBaseRadius(baseRadius / scaleFactor);
//        setToolColor(color);
//
//        path = new Path();
//        points = new ArrayList<>();
        simplePaint = new Paint();
        simplePaint.setAntiAlias(true);
        simplePaint.setFilterBitmap(true);
        simplePaint.setDither(true);
    }

    public void initMarker() {
        Marker marker = new Marker(canvas, this);
        marker.setBaseRadius((int) (baseRadius / scaleFactor));
        setTool(marker);
//        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeJoin(Paint.Join.MITER);
//        paint.setStrokeCap(Paint.Cap.BUTT);
//        paint.setPathEffect(new DashPathEffect(new float[]{2, 0}, 0));
//        alpha = 0x50;
//        isVarRadius = false;
//        isFill = false;
//        setToolColor(color);
    }

    public void initBrush() {
        Brush brush = new Brush(canvas, this);
        brush.setBaseRadius((int) (baseRadius / scaleFactor));
        setTool(brush);
//        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setAntiAlias(true);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeJoin(Paint.Join.ROUND);
//        paint.setStrokeCap(Paint.Cap.ROUND);
//        alpha = 0xff;
//        isVarRadius = true;
//        isFill = false;
//        setToolColor(color);
    }

    public void initPencil() {
        Pencil pencil = new Pencil(canvas, this);
        pencil.setBaseRadius((int) (baseRadius / scaleFactor));
        setTool(pencil);
//        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setAntiAlias(true);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeJoin(Paint.Join.ROUND);
//        paint.setStrokeCap(Paint.Cap.ROUND);
//        alpha = 0xff;
//        isVarRadius = false;
//        isFill = false;
//        setToolColor(color);
    }

    public void initFluffy() {
        Fluffy fluffy = new Fluffy(canvas, this);
        fluffy.setBaseRadius((int) (baseRadius / scaleFactor));
        setTool(fluffy);
//        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setAntiAlias(true);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeJoin(Paint.Join.MITER);
//        paint.setStrokeCap(Paint.Cap.SQUARE);
//        paint.setStrokeMiter(0.2f);
//        paint.setPathEffect(new DiscretePathEffect(2, 2));
//        alpha = 0x20;
//        isVarRadius = false;
//        isFill = false;
//        setToolColor(color);
    }

    public void initFill() {
//        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setAntiAlias(true);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeJoin(Paint.Join.ROUND);
//        paint.setStrokeCap(Paint.Cap.ROUND);
//        alpha = 0xff;
//        isVarRadius = false;
//        isFill = true;
//        setToolColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap == null) {
            initBitmap();
        }

        canvas.drawBitmap(bitmap, src, dst, simplePaint);
        tool.onDraw();
//        path.reset();
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

    Bezier bezier = new Bezier();

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int eventX = (int) (event.getX() / scaleFactor);
        int eventY = (int) (event.getY() / scaleFactor);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                tool.onTouchDown(eventX, eventY);
//                if (isFill) {
//                    int pixel = bitmap.getPixel((int) eventX, (int) eventY);
//                    QueueLinearFloodFiller filler = new QueueLinearFloodFiller(bitmap, pixel, color);
//                    filler.setTolerance(COLOR_DELTA);
//                    filler.floodFill((int) eventX, (int) eventY);
//                } else {
//                    prevPoint = null;
//                    setBaseRadius(baseRadius / scaleFactor);
//                    path.moveTo(eventX, eventY);
//                }
//                points.add(new Point(eventX, eventY));
                break;
            }
            case MotionEvent.ACTION_UP: {
                tool.onTouchUp(eventX, eventY);
//                if (!isFill) {
//                    if (path.isEmpty()) {
//                        path.moveTo(prevX, prevY);
//                    }
//                    path.lineTo(eventX + 1, eventY);
//                    points.add(new Point(eventX + 1, eventY));
//
//                    canvas.drawPath(path, paint);
//
//                    History history = new History(new Paint(paint), points);
//                    stack.add(history);
//
//                    path.reset();
//                    points.clear();
//                }
//
//                invalidate();
//                prevX = 0;
//                prevY = 0;
//                prevPoint = null;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                tool.onTouchMove(eventX, eventY);
//                if (!isFill) {
//                    if (path.isEmpty()) {
//                        path.moveTo(prevX, prevY);
//                    }
//                    path.lineTo(eventX, eventY);
//                    points.add(new Point(eventX, eventY));
//
//                    if (isVarRadius) {
//                        float absLength = (baseRadius / scaleFactor) - (Math.abs(eventX - prevX) + Math.abs(eventY - prevY));
//                        float r = getRadius();
//                        if (r < absLength) {
//                            r += 1;
//                        } else {
//                            r -= 1;
//                        }
//                        if (r > 10 && r < (baseRadius / scaleFactor) && prevPoint != null) {
//                            setBaseRadius(r);
//                        }
//                    }
//
//                    canvas.drawPath(path, paint);
//
//                    prevX = eventX;
//                    prevY = eventY;
//
//                    if (prevPoint == null) {
//                        prevPoint = new Point(eventX, eventY);
//                    } else {
//                        prevPoint.setX(eventX);
//                        prevPoint.setY(eventY);
//                    }
//                }

//                invalidate();

                break;
            }
        }
        invalidate();
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    public void undo() {
//        if (stack.isEmpty()) {
//            clear();
//        } else {
//            stack.pop();
//            applyStack();
//            invalidate();
//        }
    }

    public void reset() {
        clear();
//        stack.clear();
        invalidate();
    }

    public void setTool(Tool tool) {
        int color = getToolColor();
        tool.setColor(color);
        this.tool = tool;
    }

    public void setToolColor(int color) {
        tool.setColor(color);
//        paint.setToolColor(0xffffffff);
//        paint.setToolColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
//        this.color = color;
    }

    private int getToolColor() {
        return tool == null ? 0xcd0219 : tool.getColor();
    }

    public void setRadius(float radius) {
//        paint.setStrokeWidth(radius);
    }

    public float getRadius() {
        return 0;//paint.getStrokeWidth();
    }

    private void applyStack() {
//        Canvas canvas = new Canvas(bitmap);
//        canvas.save();
//        canvas.drawColor(Color.WHITE);
//        for (History history : stack) {
//            List<Point> points = history.getPoints();
//            path.reset();
//            history.getPaint();
////            path.moveTo(history.get(0));
////            canvas.drawPath(history.getPath(), history.getPaint());
//        }
//        canvas.restore();
    }

    private void clear() {
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
    }
}
