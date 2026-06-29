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
import android.graphics.LinearGradient;
import android.graphics.Shader;

import com.adam.app.demoset.graphics.core.BaseCanvasStrategy;
import com.adam.app.demoset.graphics.viewmodel.GraphicsViewModel;

public class WaveStrategy extends BaseCanvasStrategy {

    @Override
    public void onStart() {}

    @Override
    public void onStop() {}

    @Override
    public void draw(Canvas canvas, int width, int height, float phase) {
        mPath.reset();
        float centerY = height / 2f;
        mPath.moveTo(0, centerY);

        for (float x = 0; x <= width; x += 10f) {
            float y = (float) (centerY + Math.sin((x / width * 2 * Math.PI) + (phase * 2 * Math.PI)) * 100f);
            mPath.lineTo(x, y);
        }

        Shader shader = new LinearGradient(0, 0, width, 0,
                new int[]{Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED},
                null, Shader.TileMode.REPEAT);
        mPaint.setShader(shader);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public int getEffectType() {
        return GraphicsViewModel.EFFECT_WAVE;
    }
}
