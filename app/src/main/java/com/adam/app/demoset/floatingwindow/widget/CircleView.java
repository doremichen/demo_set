/*
 * Copyright (c) 2026 Adam Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.adam.app.demoset.floatingwindow.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * CircleView displays a floating dot with a red inner circle that follows touch input.
 */
public class CircleView extends View {

    private float mCircle1X, mCircle1Y; // Background circle position
    private float mCircle2X, mCircle2Y; // Foreground circle position
    private float mRadius = 60.0f;      // Base radius
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Center the background circle
        mCircle1X = w / 2.0f;
        mCircle1Y = h / 2.0f;
        // Initially, the foreground circle is also at the center
        mCircle2X = mCircle1X;
        mCircle2Y = mCircle1Y;
        // Set radius based on view size
        mRadius = Math.min(w, h) / 2.0f * 0.8f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                // Move the red dot to the touch position
                mCircle2X = event.getX();
                mCircle2Y = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // Reset the red dot to the center
                mCircle2X = mCircle1X;
                mCircle2Y = mCircle1Y;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw the background green circle
        mPaint.setColor(Color.GREEN);
        canvas.drawCircle(mCircle1X, mCircle1Y, mRadius, mPaint);
        
        // Draw the foreground red circle
        mPaint.setColor(Color.RED);
        canvas.drawCircle(mCircle2X, mCircle2Y, mRadius * 0.6f, mPaint);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
