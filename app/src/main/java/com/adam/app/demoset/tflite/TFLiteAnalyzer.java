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

import com.adam.app.demoset.utils.Utils;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.task.vision.classifier.Classifications;
import org.tensorflow.lite.task.vision.classifier.ImageClassifier;

import java.io.IOException;
import java.util.List;

/**
 * Helper class for TFLite Image Classification.
 */
public class TFLiteAnalyzer {
    private static final String MODEL_FILE = "mobilenet_v2_1.0_224_quant.tflite";
    private ImageClassifier mImageClassifier;

    public interface ClassifierListener {
        void onError(String error);
        void onResults(List<Classifications> results, long inferenceTime);
    }

    public void init(Context context, ClassifierListener listener) {
        ImageClassifier.ImageClassifierOptions options =
                ImageClassifier.ImageClassifierOptions.builder()
                        .setMaxResults(3)
                        .setScoreThreshold(0.5f)
                        .build();

        try {
            mImageClassifier = ImageClassifier.createFromFileAndOptions(context, MODEL_FILE, options);
        } catch (IOException e) {
            listener.onError("TFLite failed to load model: " + e.getMessage());
            Utils.error(this, "Error initializing classifier: " + e.getMessage());
        }
    }

    public void classify(Bitmap bitmap, ClassifierListener listener) {
        if (mImageClassifier == null) {
            listener.onError("Classifier not initialized");
            return;
        }

        long startTime = SystemClock.uptimeMillis();
        TensorImage image = TensorImage.fromBitmap(bitmap);
        List<Classifications> results = mImageClassifier.classify(image);
        long inferenceTime = SystemClock.uptimeMillis() - startTime;

        listener.onResults(results, inferenceTime);
    }
}
