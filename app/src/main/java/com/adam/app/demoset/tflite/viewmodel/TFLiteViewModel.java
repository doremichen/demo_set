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

package com.adam.app.demoset.tflite.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.tflite.TFLiteAnalyzer;

import org.tensorflow.lite.task.vision.classifier.Classifications;

import java.util.List;
import java.util.Locale;

public class TFLiteViewModel extends AndroidViewModel implements TFLiteAnalyzer.ClassifierListener {

    private final MutableLiveData<String> mResultText = new MutableLiveData<>("Ready");
    private final TFLiteAnalyzer mAnalyzer;

    public TFLiteViewModel(@NonNull Application application) {
        super(application);
        mAnalyzer = new TFLiteAnalyzer();
        mAnalyzer.init(application, this);
    }

    public LiveData<String> getResultText() {
        return mResultText;
    }

    public void analyzeImage(Bitmap bitmap) {
        mResultText.setValue("Analyzing...");
        mAnalyzer.classify(bitmap, this);
    }

    @Override
    public void onError(String error) {
        mResultText.postValue("Error: " + error);
    }

    @Override
    public void onResults(List<Classifications> results, long inferenceTime) {
        if (results == null || results.isEmpty() || results.get(0).getCategories().isEmpty()) {
            mResultText.postValue("No results found.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Inference: ").append(inferenceTime).append("ms\n\n");
        
        results.get(0).getCategories().forEach(category -> {
            sb.append(String.format("%s: %.2f%%\n", 
                category.getLabel(),
                category.getScore() * 100, Locale.getDefault()));
        });

        mResultText.postValue(sb.toString());
    }
}
