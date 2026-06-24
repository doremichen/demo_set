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

package com.adam.app.demoset.tflite;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import com.adam.app.demoset.utils.DemoAppConstants;
import com.adam.app.demoset.utils.ThreadHelper;
import com.adam.app.demoset.utils.Utils;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.task.vision.classifier.Classifications;
import org.tensorflow.lite.task.vision.classifier.ImageClassifier;

import java.util.List;

/**
 * Helper class for TFLite Image Classification.
 */
public class TFLiteAnalyzer {
    private static final String MODEL_FILE = DemoAppConstants.TFLITE_MODEL_FILE;
    private ImageClassifier mImageClassifier;

    // init helper, classification helper
    private ThreadHelper<ImageClassifier> mInitHelper;
    private ThreadHelper<AnalysisResult> mClassificationHelper;


    private static class AnalysisResult {
        final List<Classifications> classifications;
        final long inferenceTime;

        AnalysisResult(List<Classifications> classifications, long inferenceTime) {
            this.classifications = classifications;
            this.inferenceTime = inferenceTime;
        }
    }

    public interface ClassifierListener {
        void onError(String error);
        void onResults(List<Classifications> results, long inferenceTime);
    }

    public void init(@NonNull final Context context, @NonNull final ClassifierListener listener) {
        // precondition check
        if (mImageClassifier != null || (mInitHelper != null && mInitHelper.isRunning())) {
            return;
        }

        mInitHelper = new ThreadHelper.Builder<ImageClassifier>()
                .setTask(() -> {
                    ImageClassifier.ImageClassifierOptions options =
                            ImageClassifier.ImageClassifierOptions.builder()
                                    .setMaxResults(3)
                                    .setScoreThreshold(0.5f)
                                    .build();
                    return ImageClassifier.createFromFileAndOptions(context, MODEL_FILE, options);
                })
                .setCallback(new ThreadHelper.ThreadCallback<ImageClassifier>() {
                    @Override
                    public void onStarted() {
                    }
                    @Override
                    public void onSuccess(ImageClassifier result) {
                        mImageClassifier = result;
                    }
                    @Override
                    public void onError(Exception e) {
                        mImageClassifier = null;
                        listener.onError("TFLite failed to load model: " + e.getMessage());
                        Utils.error(TFLiteAnalyzer.this, "Error initializing classifier: " + e.getMessage());
                    }
                    @Override
                    public void onCancelled() {
                    }
                    @Override
                    public void onFinished() {
                    }
                })
                .build();
        // start
        mInitHelper.start();
    }

    public void classify(@NonNull final Bitmap bitmap, @NonNull final ClassifierListener listener) {
        if (mImageClassifier == null) {
            listener.onError("Classifier is not initialized yet.");
            return;
        }

        // stop it if the Classification is running
        if (mClassificationHelper != null && mClassificationHelper.isRunning()) {
            mClassificationHelper.stop();
        }

        mClassificationHelper = new ThreadHelper.Builder<AnalysisResult>()
                .setTask(() -> {
                    long startTime = SystemClock.uptimeMillis();
                    TensorImage tensorImage = TensorImage.fromBitmap(bitmap);
                    List<Classifications> results = mImageClassifier.classify(tensorImage);
                    long inferenceTime = SystemClock.uptimeMillis() - startTime;

                    return new AnalysisResult(results, inferenceTime);
                })
                .setCallback(new ThreadHelper.ThreadCallback<AnalysisResult>() {
                    @Override
                    public void onStarted() {
                    }
                    @Override
                    public void onSuccess(AnalysisResult result) {
                        // result check
                        if (result == null) return;
                        // callback
                        listener.onResults(result.classifications, result.inferenceTime);
                    }
                    @Override
                    public void onError(Exception e) {
                        listener.onError("Error classifying frame: " + e.getMessage());
                        Utils.error(TFLiteAnalyzer.this, "Error classifying frame: " + e.getMessage());
                    }
                    @Override
                    public void onCancelled() {
                        // callback
                        listener.onError("Classification cancelled.");
                    }
                    @Override
                    public void onFinished() {
                    }
                })
                .build();

        // start
        mClassificationHelper.start();
    }

    /**
     * release resource
     */
    public void release() {
        if (mInitHelper != null) {
            mInitHelper.stop();
            mInitHelper.shutDown();
        }
        if (mClassificationHelper != null) {
            mClassificationHelper.stop();
            mClassificationHelper.shutDown();
        }

        // close Image Classifier
        if (mImageClassifier != null) {
            mImageClassifier.close();
            mImageClassifier = null;
        }

    }
}
