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
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.adam.app.demoset.graphics.viewmodel.GraphicsViewModel;

/**
 * Custom View demonstrating advanced Canvas 2D effects.
 */
public class AdvancedGraphicsView extends View {

    private static final float STROKE_WIDTH = 8f;
    private static final int ANIMATION_DURATION = 2000;
    
    // Wave constants
    private static final float WAVE_STEP = 10f;
    private static final float WAVE_AMPLITUDE = 100f;
    private static final double TWO_PI = 2 * Math.PI;

    // Spiral constants
    private static final float SPIRAL_MAX_ANGLE = 720f;
    private static final float SPIRAL_ANGLE_STEP = 5f;
    private static final float SPIRAL_RADIUS_DIVISOR = 2.5f;
    private static final float FULL_ROTATION_DEG = 360f;

    private Paint mPaint;
    private Path mPath;
    private float mPhase = 0;
    private ValueAnimator mAnimator;
    private int mEffectType = GraphicsViewModel.EFFECT_WAVE;

    public AdvancedGraphicsView(Context context) {
        super(context);
        init();
    }

    public AdvancedGraphicsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(STROKE_WIDTH);
        mPath = new Path();
    }

    public void setAnimating(boolean animating) {
        if (animating) {
            startAnimation();
        } else {
            stopAnimation();
        }
    }

    public void setEffectType(int type) {
        mEffectType = type;
        invalidate();
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

        int width = getWidth();
        int height = getHeight();

        if (mEffectType == GraphicsViewModel.EFFECT_WAVE) {
            drawWaveEffect(canvas, width, height);
        } else if (mEffectType == GraphicsViewModel.EFFECT_SPIRAL) {
            drawSpiralEffect(canvas, width, height);
        }
    }

    private void drawWaveEffect(Canvas canvas, int width, int height) {
        mPath.reset();
        float centerY = height / 2f;
        mPath.moveTo(0, centerY);

        for (float x = 0; x <= width; x += WAVE_STEP) {
            float y = (float) (centerY + Math.sin((x / width * TWO_PI) + (mPhase * TWO_PI)) * WAVE_AMPLITUDE);
            mPath.lineTo(x, y);
        }

        Shader shader = new LinearGradient(0, 0, width, 0,
                new int[]{Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED},
                null, Shader.TileMode.REPEAT);
        mPaint.setShader(shader);
        canvas.drawPath(mPath, mPaint);
    }

    private void drawSpiralEffect(Canvas canvas, int width, int height) {
        float centerX = width / 2f;
        float centerY = height / 2f;
        mPath.reset();
        
        float maxRadius = Math.min(width, height) / SPIRAL_RADIUS_DIVISOR;
        for (float angle = 0; angle < SPIRAL_MAX_ANGLE; angle += SPIRAL_ANGLE_STEP) {
            float rad = (float) Math.toRadians(angle + (mPhase * FULL_ROTATION_DEG));
            float r = (angle / SPIRAL_MAX_ANGLE) * maxRadius;
            float x = (float) (centerX + r * Math.cos(rad));
            float y = (float) (centerY + r * Math.sin(rad));
            if (angle == 0) mPath.moveTo(x, y);
            else mPath.lineTo(x, y);
        }

        mPaint.setShader(null);
        mPaint.setColor(Color.CYAN);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }
}
