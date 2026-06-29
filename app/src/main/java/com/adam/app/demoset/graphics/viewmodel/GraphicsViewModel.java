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

package com.adam.app.demoset.graphics.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.adam.app.demoset.graphics.core.GraphicsController;
import com.adam.app.demoset.graphics.core.IGraphicsStrategy;

/**
 * ViewModel that owns and directs the GraphicsController.
 * Acts as the single source of truth for the UI.
 */
public class GraphicsViewModel extends ViewModel {

    public static final int EFFECT_WAVE = 0;
    public static final int EFFECT_SPIRAL = 1;
    public static final int EFFECT_3D = 2;

    private final MutableLiveData<Boolean> isAnimating = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> effectType = new MutableLiveData<>(EFFECT_WAVE);
    private final MutableLiveData<IGraphicsStrategy> activeStrategy = new MutableLiveData<>();
    
    // ViewModel is the creator and owner
    private final GraphicsController mController = new GraphicsController();

    public GraphicsViewModel() {
        // ViewModel directs the controller's callbacks back to LiveData
        mController.setOnEffectChangedListener((type, strategy) -> {
            effectType.setValue(type);
            activeStrategy.setValue(strategy);
        });
        
        // Push initial state
        activeStrategy.setValue(mController.getCurrentStrategy());
    }

    public LiveData<Boolean> getIsAnimating() {
        return isAnimating;
    }

    public LiveData<Integer> getEffectType() {
        return effectType;
    }

    public LiveData<IGraphicsStrategy> getActiveStrategy() {
        return activeStrategy;
    }

    public void toggleAnimation() {
        Boolean current = isAnimating.getValue();
        boolean nextState = (current != null && !current);
        isAnimating.setValue(nextState);
        mController.setAnimating(nextState);
    }

    public void switchEffect() {
        Integer current = effectType.getValue();
        if (current == null) return;
        
        int nextEffect;
        if (current == EFFECT_WAVE) {
            nextEffect = EFFECT_SPIRAL;
        } else if (current == EFFECT_SPIRAL) {
            nextEffect = EFFECT_3D;
        } else {
            nextEffect = EFFECT_WAVE;
        }
        
        mController.setEffectType(nextEffect);
    }

    /**
     * Directed from Activity to ensure controller state syncs with OS lifecycle.
     */
    public void onActivityResume() {
        Boolean animating = isAnimating.getValue();
        mController.setAnimating(animating != null && animating);
    }

    /**
     * Directed from Activity to ensure background tasks stop.
     */
    public void onActivityPause() {
        mController.setAnimating(false);
        isAnimating.setValue(false); // Sync LiveData to stop 2D animator immediately
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mController.release();
    }
}
