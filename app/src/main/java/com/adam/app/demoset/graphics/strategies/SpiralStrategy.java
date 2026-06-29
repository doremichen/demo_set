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

package com.adam.app.demoset.graphics.strategies;

import android.graphics.Canvas;
import android.graphics.Color;

import com.adam.app.demoset.graphics.core.BaseCanvasStrategy;
import com.adam.app.demoset.graphics.viewmodel.GraphicsViewModel;

public class SpiralStrategy extends BaseCanvasStrategy {

    @Override
    public void onStart() {}

    @Override
    public void onStop() {}

    @Override
    public void draw(Canvas canvas, int width, int height, float phase) {
        float centerX = width / 2f;
        float centerY = height / 2f;
        mPath.reset();
        
        float maxRadius = Math.min(width, height) / 2.5f;
        for (float angle = 0; angle < 720f; angle += 5f) {
            float rad = (float) Math.toRadians(angle + (phase * 360f));
            float r = (angle / 720f) * maxRadius;
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
    public int getEffectType() {
        return GraphicsViewModel.EFFECT_SPIRAL;
    }
}
