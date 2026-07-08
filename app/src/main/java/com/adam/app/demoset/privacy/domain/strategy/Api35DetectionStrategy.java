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

package com.adam.app.demoset.privacy.domain.strategy;

import android.app.Activity;
import android.os.Build;
import android.view.WindowManager;

import com.adam.app.demoset.utils.Utils;

import java.util.function.Consumer;

/**
 * Detection strategy for Android 15 (API 35).
 * Uses ScreenRecordingCallback for specific recording detection.
 */
public class Api35DetectionStrategy implements IPrivacyDetectionStrategy {
    private static final String sTAG = "Api35Strategy";
    private Consumer<Integer> mCallback;

    @Override
    public void register(Activity activity, DetectionCallback callback) {
        if (Build.VERSION.SDK_INT >= 35) {
            mCallback = state -> {
                // state 0: SCREEN_RECORDING_STATE_NOT_RECORDING
                // state 1: SCREEN_RECORDING_STATE_RECORDING
                Utils.info(sTAG, "onScreenRecordingStateChanged raw state: " + state);
                boolean isRecording = (state != 0);
                callback.onPrivacyEventDetected(isRecording);
            };
            try {
                activity.getWindowManager().addScreenRecordingCallback(activity.getMainExecutor(), mCallback);
                Utils.info(sTAG, "Registered API 35 ScreenRecordingCallback successfully");
            } catch (Exception e) {
                Utils.error(sTAG, "Failed to register API 35: " + e.getMessage());
            }
        }
    }

    @Override
    public void unregister(Activity activity) {
        if (Build.VERSION.SDK_INT >= 35 && mCallback != null) {
            try {
                activity.getWindowManager().removeScreenRecordingCallback(mCallback);
                Utils.info(sTAG, "Unregistered API 35 callback");
            } catch (Exception e) {
                Utils.error(sTAG, "Failed to unregister API 35: " + e.getMessage());
            } finally {
                mCallback = null;
            }
        }
    }
}
