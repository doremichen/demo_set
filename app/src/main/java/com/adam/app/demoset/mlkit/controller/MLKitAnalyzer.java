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
package com.adam.app.demoset.mlkit.controller;

import android.annotation.SuppressLint;
import android.media.Image;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.adam.app.demoset.mlkit.strategy.VisionDetectionMode;
import com.google.mlkit.vision.common.InputImage;

/**
 * Analyzer implementation that delegates image processing to ML Kit strategies.
 */
public class MLKitAnalyzer implements ImageAnalysis.Analyzer {

    /**
     * Listener for detection results.
     */
    public interface MLKitListener {
        void onResult(int resId, Object... args);
        void onError(String error);
    }

    private final MLKitListener mListener;
    private VisionDetectionMode mMode = VisionDetectionMode.BARCODE;

    public MLKitAnalyzer(MLKitListener listener) {
        mListener = listener;
    }

    public void setMode(VisionDetectionMode mode) {
        mMode = mode;
    }

    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        @SuppressLint("UnsafeOptInUsageError")
        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            
            mMode.process(image, imageProxy, new VisionDetectionMode.StrategyCallback() {
                @Override
                public void onResult(int resId, Object... args) {
                    mListener.onResult(resId, args);
                }

                @Override
                public void onError(String error) {
                    mListener.onError(error);
                }
            });
        } else {
            imageProxy.close();
        }
    }

    public void close() {
        VisionDetectionMode.closeAll();
    }
}
