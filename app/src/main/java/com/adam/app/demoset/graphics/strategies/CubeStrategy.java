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

import com.adam.app.demoset.graphics.CubeRenderer;
import com.adam.app.demoset.graphics.core.IGraphicsStrategy;
import com.adam.app.demoset.graphics.viewmodel.GraphicsViewModel;

/**
 * 3D Graphics strategy using GLSurfaceView Renderer.
 */
public class CubeStrategy implements IGraphicsStrategy {

    private final CubeRenderer mRenderer;

    public CubeStrategy(CubeRenderer renderer) {
        this.mRenderer = renderer;
    }

    @Override
    public void onStart() {}

    @Override
    public void onStop() {
        setAnimating(false);
    }

    @Override
    public void setAnimating(boolean animating) {
        if (mRenderer != null) {
            mRenderer.setAnimating(animating);
        }
    }

    @Override
    public void onRelease() {
        setAnimating(false);
    }

    @Override
    public int getEffectType() {
        return GraphicsViewModel.EFFECT_3D;
    }

    public CubeRenderer getRenderer() {
        return mRenderer;
    }
}
