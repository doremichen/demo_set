/*
 * Copyright (c) 2026 Adam Chen
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
