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

package com.adam.app.demoset.graphics.core;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Base class for 2D Canvas-based strategies.
 */
public abstract class BaseCanvasStrategy implements IGraphicsStrategy {

    protected final Paint mPaint;
    protected final Path mPath;
    protected boolean mIsAnimating = false;

    public BaseCanvasStrategy() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8f);
        mPath = new Path();
    }

    @Override
    public void setAnimating(boolean animating) {
        mIsAnimating = animating;
    }

    @Override
    public void onRelease() {
        mPath.reset();
    }

    /**
     * Draw the effect on the given canvas.
     */
    public abstract void draw(Canvas canvas, int width, int height, float phase);
}
