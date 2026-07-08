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

package com.adam.app.demoset.privacy.domain;

import com.adam.app.demoset.utils.Utils;

import javax.inject.Inject;

/**
 * UseCase for managing screen capture business logic.
 * This class acts as a mediator between the system event source and the presentation layer.
 */
public class MonitorScreenCaptureUseCase {

    /**
     * Callback interface for screen capture events.
     */
    public interface Callback {
        void onScreenCaptureStatusChanged(boolean isCaptured);
    }

    private Callback mCallback;

    @Inject
    public MonitorScreenCaptureUseCase() {
        // Constructor for Hilt
    }

    /**
     * Initialize the UseCase with a callback.
     *
     * @param callback The callback to receive processed events.
     */
    public void start(Callback callback) {
        Utils.info(this, "start");
        this.mCallback = callback;
    }

    /**
     * Clear the callback to avoid leaks.
     */
    public void stop() {
        Utils.info(this, "stop");
        this.mCallback = null;
    }

    /**
     * Process the capture event received from the system sensor.
     *
     * @param isCaptured Whether a capture event was detected.
     */
    public void processCaptureEvent(boolean isCaptured) {
        Utils.info(this, "processCaptureEvent: " + isCaptured);
        if (mCallback != null) {
            mCallback.onScreenCaptureStatusChanged(isCaptured);
        }
    }
}
