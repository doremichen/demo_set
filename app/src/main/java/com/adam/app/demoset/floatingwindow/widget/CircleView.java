package com.adam.app.demoset.floatingwindow.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.Nullable;
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

        switch (action) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // Assign circle1 position is circle2 position
                Circle2.sX = Circle1.sX;
                Circle2.sY = Circle1.sY;
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
                if (inRange(Circle2.sX, Circle2.sY, event.getX(), event.getY())) {
                    if (inRange(Circle1.sX, Circle1.sY, Circle2.sX, Circle2.sY)) {
                        Circle2.sX = event.getX();
                        Circle2.sY = event.getY();
                    } else {
                        // Assign circle1 position is circle2 position
                        Circle2.sX = Circle1.sX;
                        Circle2.sY = Circle1.sY;
                    }
                }
                break;

        }

        // refresh ui
        invalidate();

        return true;
    }

    private boolean inRange(float v1, float v2, float v3, float v4) {
        return lessThanRadius(v1, v3) && lessThanRadius(v2, v4);
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
