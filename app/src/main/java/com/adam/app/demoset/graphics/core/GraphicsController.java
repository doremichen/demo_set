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

import android.util.SparseArray;
import com.adam.app.demoset.graphics.CubeRenderer;
import com.adam.app.demoset.graphics.strategies.CubeStrategy;
import com.adam.app.demoset.graphics.strategies.SpiralStrategy;
import com.adam.app.demoset.graphics.strategies.WaveStrategy;
import com.adam.app.demoset.graphics.viewmodel.GraphicsViewModel;

/**
 * Centered controller driving effect switching and animation lifecycle.
 */
public class GraphicsController {

    public interface OnEffectChangedListener {
        void onEffectChanged(int effectType, IGraphicsStrategy activeStrategy);
    }

    private final SparseArray<IGraphicsStrategy> mStrategies = new SparseArray<>();
    private IGraphicsStrategy mCurrentStrategy;
    private OnEffectChangedListener mListener;
    private boolean mIsGlobalAnimating = false;

    public GraphicsController() {
        // High cohesion: Group all available engine implementations here
        mStrategies.put(GraphicsViewModel.EFFECT_WAVE, new WaveStrategy());
        mStrategies.put(GraphicsViewModel.EFFECT_SPIRAL, new SpiralStrategy());
        mStrategies.put(GraphicsViewModel.EFFECT_3D, new CubeStrategy(new CubeRenderer()));
        
        // Default strategy
        mCurrentStrategy = mStrategies.get(GraphicsViewModel.EFFECT_WAVE);
    }

    public void setOnEffectChangedListener(OnEffectChangedListener listener) {
        mListener = listener;
    }

    public void setEffectType(int type) {
        if (mCurrentStrategy != null && mCurrentStrategy.getEffectType() == type) {
            return;
        }

        // 1. Scenario 2 handler: Gracefully stop old running strategy before switching
        if (mCurrentStrategy != null) {
            mCurrentStrategy.onStop();
        }

        mCurrentStrategy = mStrategies.get(type);

        if (mCurrentStrategy != null) {
            mCurrentStrategy.onStart();
            // Carry-over animation state smoothly
            mCurrentStrategy.setAnimating(mIsGlobalAnimating);
        }

        // Callback to system orchestrator
        if (mListener != null) {
            mListener.onEffectChanged(type, mCurrentStrategy);
        }
    }

    public void setAnimating(boolean animating) {
        mIsGlobalAnimating = animating;
        if (mCurrentStrategy != null) {
            mCurrentStrategy.setAnimating(animating);
        }
    }

    public boolean isAnimating() {
        return mIsGlobalAnimating;
    }

    public IGraphicsStrategy getCurrentStrategy() {
        return mCurrentStrategy;
    }

    /**
     * Scenario 1 handler: Destroy everything on screen departure
     */
    public void release() {
        mIsGlobalAnimating = false;
        for (int i = 0; i < mStrategies.size(); i++) {
            mStrategies.valueAt(i).onRelease();
        }
        mCurrentStrategy = null;
        mListener = null;
    }
}
