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
package com.adam.app.demoset.mlkit.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.R;
import com.adam.app.demoset.mlkit.controller.MLKitAnalyzer;
import com.adam.app.demoset.mlkit.strategy.VisionDetectionMode;
import com.adam.app.demoset.utils.Utils;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel for ML Kit Vision Demo.
 * Manages the camera lifecycle, detection modes, and result presentation.
 */
public class MLKitViewModel extends AndroidViewModel {

    private final MutableLiveData<VisionDetectionMode> mMode = new MutableLiveData<>(VisionDetectionMode.BARCODE);
    private final MutableLiveData<String> mResultText = new MutableLiveData<>();
    private final ExecutorService mAnalysisExecutor = Executors.newSingleThreadExecutor();
    private final MLKitAnalyzer mAnalyzer;

    private LifecycleOwner mLifecycleOwner;
    private Preview.SurfaceProvider mSurfaceProvider;

    public MLKitViewModel(@NonNull Application application) {
        super(application);
        mResultText.setValue(application.getString(R.string.msg_mlkit_default_hint));
        mAnalyzer = new MLKitAnalyzer(new MLKitAnalyzer.MLKitListener() {
            @Override
            public void onResult(int resId, Object... args) {
                mResultText.postValue(application.getString(resId, args));
            }

            @Override
            public void onError(String error) {
                mResultText.postValue(application.getString(R.string.msg_mlkit_error_prefix, error));
            }
        });
    }

    public LiveData<VisionDetectionMode> getMode() {
        return mMode;
    }

    public LiveData<String> getResultText() {
        return mResultText;
    }

    /**
     * Updates the current detection mode and triggers a camera re-bind.
     * @param modeIndex Index of the mode in VisionDetectionMode values.
     */
    public void setMode(int modeIndex) {
        VisionDetectionMode mode = VisionDetectionMode.values()[modeIndex];
        mMode.setValue(mode);
        mAnalyzer.setMode(mode);
        mResultText.setValue(getApplication().getString(R.string.msg_mlkit_mode_switched));

        // State-driven re-binding of the camera when mode changes
        if (mLifecycleOwner != null && mSurfaceProvider != null) {
            startCamera(mLifecycleOwner, mSurfaceProvider);
        }
    }

    /**
     * Initializes the CameraX provider and starts the camera.
     * @param lifecycleOwner Lifecycle owner (Activity/Fragment).
     * @param surfaceProvider Provider for the preview surface.
     */
    public void startCamera(LifecycleOwner lifecycleOwner, Preview.SurfaceProvider surfaceProvider) {
        mLifecycleOwner = lifecycleOwner;
        mSurfaceProvider = surfaceProvider;

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = 
                ProcessCameraProvider.getInstance(getApplication());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider, lifecycleOwner, surfaceProvider);
            } catch (ExecutionException | InterruptedException e) {
                Utils.error(this, "Error starting camera: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(getApplication()));
    }

    private void bindPreview(ProcessCameraProvider cameraProvider, 
                             LifecycleOwner lifecycleOwner, 
                             Preview.SurfaceProvider surfaceProvider) {
        
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(surfaceProvider);

        VisionDetectionMode mode = mMode.getValue();
        int lensFacing = (mode != null) ? mode.getLensFacing() : CameraSelector.LENS_FACING_BACK;
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(mAnalysisExecutor, mAnalyzer);

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mAnalysisExecutor.shutdown();
        mAnalyzer.close();
    }
}
