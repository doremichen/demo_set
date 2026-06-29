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

package com.adam.app.demoset.graphics.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.adam.app.demoset.graphics.core.BaseCanvasStrategy;
import com.adam.app.demoset.graphics.core.IGraphicsStrategy;

/**
 * Custom View that delegates drawing logic to a Strategy.
 */
public class AdvancedGraphicsView extends View {

    private static final int ANIMATION_DURATION = 2000;
    
    private BaseCanvasStrategy mActiveStrategy;
    private float mPhase = 0;
    private ValueAnimator mAnimator;

    public AdvancedGraphicsView(Context context) {
        super(context);
    }

    public AdvancedGraphicsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setStrategy(IGraphicsStrategy strategy) {
        if (strategy instanceof BaseCanvasStrategy) {
            this.mActiveStrategy = (BaseCanvasStrategy) strategy;
            invalidate();
        }
    }

    public void setAnimating(boolean animating) {
        if (animating) {
            startAnimation();
        } else {
            stopAnimation();
        }
    }

    private void startAnimation() {
        if (mAnimator != null && mAnimator.isRunning()) return;

        mAnimator = ValueAnimator.ofFloat(0, 1f);
        mAnimator.setDuration(ANIMATION_DURATION);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(animation -> {
            mPhase = (float) animation.getAnimatedValue();
            invalidate();
        });
        mAnimator.start();
    }

    private void stopAnimation() {
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mActiveStrategy != null) {
            mActiveStrategy.draw(canvas, getWidth(), getHeight(), mPhase);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
        mActiveStrategy = null;
    }
}
