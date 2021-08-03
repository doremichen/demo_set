package com.adam.app.demoset.floatingwindow.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.adam.app.demoset.Utils;

public class CircleView extends View {

    // prepare circle
    private static class Circle1 {
        static float sX = 0.0f;
        static float sY = 0.0f;
    }
    private static class Circle2 {
        static float sX = 0.0f;
        static float sY = 0.0f;
    }

    // circle radius condition value
    private static final float sRadius = 80.0f;

    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * layout of the circle view
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    public void layout(int left, int top, int right, int bottom) {
        super.layout(left, top, right, bottom);
        // initial circle data
        Circle1.sX = left/2.0f + sRadius;
        Circle1.sY = top/2.0f + sRadius;
        // Assign circle1 position is circle2 position
        Circle2.sX = Circle1.sX;
        Circle2.sY = Circle1.sY;
    }

    /**
     * Receive the touch event UP/DOWN/MOVE/CANCEL
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Utils.info(this, "onTouchEvent");
        int action = event.getAction();
//        StringBuilder stb = new StringBuilder("Refresh UI: \n");

        if (action == MotionEvent.ACTION_CANCEL
            || action == MotionEvent.ACTION_UP) {
            // Assign circle1 position is circle2 position
            Circle2.sX = Circle1.sX;
            Circle2.sY = Circle1.sY;

        } else if (action == MotionEvent.ACTION_MOVE
                || action == MotionEvent.ACTION_DOWN) {
            // Update circle2 position
            if (lessThanRadius(Circle2.sX, event.getX())
                && lessThanRadius(Circle2.sY, event.getY())) {

                if (lessThanRadius(Circle1.sX, Circle2.sX)
                    && lessThanRadius(Circle1.sY, Circle2.sY)) {
                    Circle2.sX = event.getX();
                    Circle2.sY = event.getY();
                } else {
                    // Assign circle1 position is circle2 position
                    Circle2.sX = Circle1.sX;
                    Circle2.sY = Circle1.sY;
                }

//                stb.append("Circle1: ").append(Circle1.sX).append(" ").append(Circle1.sY);
//                stb.append("\n");
//                stb.append("Circle2: ").append(Circle2.sX).append(" ").append(Circle2.sY);
//                stb.append("\n");
            }

        }

//        Utils.inFo(this, stb.toString());
        // refresh ui
        invalidate();

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw circle
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        canvas.drawCircle(Circle1.sX, Circle1.sY, sRadius, paint);
        paint.setColor(Color.RED);
        canvas.drawCircle(Circle2.sX, Circle2.sY, sRadius-30, paint);

    }

    @Override
    public boolean performClick() {
        return true;
    }

    private boolean lessThanRadius(float value1, float value2) {
        return Math.abs(value1 - value2) < sRadius;
    }
}
