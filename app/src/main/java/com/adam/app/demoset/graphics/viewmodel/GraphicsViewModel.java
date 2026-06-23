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

/**
 * ViewModel for Advanced Graphics demo.
 */
public class GraphicsViewModel extends ViewModel {

    public static final int EFFECT_WAVE = 0;
    public static final int EFFECT_SPIRAL = 1;

    private final MutableLiveData<Boolean> isAnimating = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> effectType = new MutableLiveData<>(EFFECT_WAVE);

    public LiveData<Boolean> getIsAnimating() {
        return isAnimating;
    }

    public LiveData<Integer> getEffectType() {
        return effectType;
    }

    public void toggleAnimation() {
        Boolean current = isAnimating.getValue();
        isAnimating.setValue(current != null && !current);
    }

    public void setEffectType(int type) {
        effectType.setValue(type);
    }

    public void switchEffect() {
        Integer current = effectType.getValue();
        if (current == null) return;
        setEffectType(current == EFFECT_WAVE ? EFFECT_SPIRAL : EFFECT_WAVE);
    }
}
