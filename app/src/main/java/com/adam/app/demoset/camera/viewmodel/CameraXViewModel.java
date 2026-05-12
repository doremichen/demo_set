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

package com.adam.app.demoset.camera.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.camera.core.Preview;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.camera.controller.MyCameraController;

/**
 * ViewModel for CameraX Demo.
 * Orchestrates Activity commands to MyCameraController.
 */
public class CameraXViewModel extends AndroidViewModel implements MyCameraController.CameraStatusCallback {

    private final MutableLiveData<String> mStatusText = new MutableLiveData<>("Initializing...");
    private final MutableLiveData<Uri> mLastPhotoUri = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mViewPhotoEvent = new MutableLiveData<>(false);
    private final MyCameraController mController;

    public CameraXViewModel(@NonNull Application application) {
        super(application);
        mController = new MyCameraController(application);
    }

    public LiveData<String> getStatusText() {
        return mStatusText;
    }

    public LiveData<Uri> getLastPhotoUri() {
        return mLastPhotoUri;
    }

    public LiveData<Boolean> getViewPhotoEvent() {
        return mViewPhotoEvent;
    }

    public void initializeCamera(LifecycleOwner lifecycleOwner, Preview.SurfaceProvider surfaceProvider) {
        mController.startCamera(lifecycleOwner, surfaceProvider, this);
    }

    public void performCapture() {
        mController.takePhoto(this);
    }

    public void triggerViewPhoto() {
        if (mLastPhotoUri.getValue() != null) {
            mViewPhotoEvent.setValue(true);
        }
    }

    public void onViewPhotoHandled() {
        mViewPhotoEvent.setValue(false);
    }

    // --- MyCameraController.CameraStatusCallback Implementation ---

    @Override
    public void onCameraReady(String message) {
        mStatusText.postValue("Camera State: " + message);
    }

    @Override
    public void onPhotoSaved(Uri uri) {
        mLastPhotoUri.postValue(uri);
        mStatusText.postValue("Photo Saved: " + (uri != null ? uri.getLastPathSegment() : "unknown"));
    }

    @Override
    public void onError(String error) {
        mStatusText.postValue("Error: " + error);
    }
}
