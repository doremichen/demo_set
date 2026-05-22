/*
 * Copyright (c) 2026 Adam Chen
 */

package com.adam.app.demoset.mlkit.strategy;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageProxy;

import com.adam.app.demoset.R;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

/**
 * Enum representing different vision detection modes.
 * Each mode implements its own detection strategy.
 */
public enum VisionDetectionMode {
    BARCODE(CameraSelector.LENS_FACING_BACK) {
        private BarcodeScanner mScanner;

        private BarcodeScanner getScanner() {
            if (mScanner == null) {
                mScanner = BarcodeScanning.getClient();
            }
            return mScanner;
        }

        @Override
        public void process(InputImage image, ImageProxy imageProxy, StrategyCallback callback) {
            getScanner().process(image)
                    .addOnSuccessListener(barcodes -> {
                        if (!barcodes.isEmpty()) {
                            StringBuilder sb = new StringBuilder();
                            for (Barcode barcode : barcodes) {
                                String rawValue = barcode.getRawValue();
                                if (rawValue != null) {
                                    sb.append(rawValue).append("\n");
                                }
                            }
                            callback.onResult(R.string.msg_mlkit_barcode_detected, sb.toString().trim());
                        }
                    })
                    .addOnFailureListener(e -> callback.onError(e.getMessage()))
                    .addOnCompleteListener(task -> imageProxy.close());
        }

        @Override
        public void close() {
            if (mScanner != null) {
                mScanner.close();
                mScanner = null;
            }
        }
    },
    FACE(CameraSelector.LENS_FACING_FRONT) {
        private FaceDetector mDetector;

        private FaceDetector getDetector() {
            if (mDetector == null) {
                mDetector = FaceDetection.getClient();
            }
            return mDetector;
        }

        @Override
        public void process(InputImage image, ImageProxy imageProxy, StrategyCallback callback) {
            getDetector().process(image)
                    .addOnSuccessListener(faces -> {
                        if (!faces.isEmpty()) {
                            callback.onResult(R.string.msg_mlkit_face_detected);
                        }
                    })
                    .addOnFailureListener(e -> callback.onError(e.getMessage()))
                    .addOnCompleteListener(task -> imageProxy.close());
        }

        @Override
        public void close() {
            if (mDetector != null) {
                mDetector.close();
                mDetector = null;
            }
        }
    },
    TEXT(CameraSelector.LENS_FACING_BACK) {
        private TextRecognizer mRecognizer;

        private TextRecognizer getRecognizer() {
            if (mRecognizer == null) {
                mRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
            }
            return mRecognizer;
        }

        @Override
        public void process(InputImage image, ImageProxy imageProxy, StrategyCallback callback) {
            getRecognizer().process(image)
                    .addOnSuccessListener(visionText -> {
                        if (!visionText.getText().isEmpty()) {
                            callback.onResult(R.string.msg_mlkit_text_detected, visionText.getText());
                        }
                    })
                    .addOnFailureListener(e -> callback.onError(e.getMessage()))
                    .addOnCompleteListener(task -> imageProxy.close());
        }

        @Override
        public void close() {
            if (mRecognizer != null) {
                mRecognizer.close();
                mRecognizer = null;
            }
        }
    };

    private final int lensFacing;

    VisionDetectionMode(int lensFacing) {
        this.lensFacing = lensFacing;
    }

    public int getLensFacing() {
        return lensFacing;
    }

    /**
     * Closes all detectors across all modes.
     */
    public static void closeAll() {
        for (VisionDetectionMode mode : values()) {
            mode.close();
        }
    }

    /**
     * Processes the image using the current mode's strategy.
     */
    public abstract void process(InputImage image, ImageProxy imageProxy, StrategyCallback callback);

    /**
     * Closes the detector associated with this mode.
     */
    public abstract void close();

    /**
     * Callback interface for strategy results.
     */
    public interface StrategyCallback {
        void onResult(int resId, Object... args);

        void onError(String error);
    }
}
