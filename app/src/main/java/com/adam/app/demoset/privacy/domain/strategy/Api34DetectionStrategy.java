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

import androidx.annotation.RequiresApi;

import com.adam.app.demoset.utils.Utils;

/**
 * Detection strategy for Android 14 (API 34).
 * Uses ScreenCaptureCallback which is primarily for screenshots.
 */
public class Api34DetectionStrategy implements IPrivacyDetectionStrategy {
    private static final String sTAG = "Api34Strategy";
    private Activity.ScreenCaptureCallback mCallback;

    @Override
    public void register(Activity activity, DetectionCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            mCallback = () -> {
                Utils.info(sTAG, "Screen capture detected (API 34)");
                callback.onPrivacyEventDetected(true);
            };
            activity.registerScreenCaptureCallback(activity.getMainExecutor(), mCallback);
            Utils.info(sTAG, "Registered API 34 callback");
        }
    }

    @Override
    public void unregister(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && mCallback != null) {
            try {
                activity.unregisterScreenCaptureCallback(mCallback);
                Utils.info(sTAG, "Unregistered API 34 callback");
            } catch (Exception e) {
                Utils.error(sTAG, "Failed to unregister API 34: " + e.getMessage());
            } finally {
                mCallback = null;
            }
        }
    }
}
