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

package com.adam.app.demoset.animation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel for GIF Animation demo.
 * Manages the playback state of the GIF.
 */
public class GifViewModel extends ViewModel {

    private final MutableLiveData<Boolean> mIsPlaying = new MutableLiveData<>(false);

    public LiveData<Boolean> isPlaying() {
        return mIsPlaying;
    }

    /**
     * Starts the GIF animation.
     */
    public void startPlayback() {
        mIsPlaying.setValue(true);
    }

    /**
     * Stops the GIF animation.
     */
    public void stopPlayback() {
        mIsPlaying.setValue(false);
    }

    /**
     * Toggles the playback state.
     */
    public void togglePlayback() {
        Boolean current = mIsPlaying.getValue();
        mIsPlaying.setValue(current == null || !current);
    }
}
