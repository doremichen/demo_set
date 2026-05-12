/*
 * Copyright (c) 2026 Adam Chen
 */

package com.adam.app.demoset.camera2.viewmodel;

import android.app.Application;
import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.R;
import com.adam.app.demoset.camera2.controller.MyCameraController;
import com.adam.app.demoset.utils.Utils;

import java.io.File;

/**
 * ViewModel for Camera2 Demo.
 */
public class Camera2ViewModel extends AndroidViewModel implements MyCameraController.CameraCallback {

    private final MyCameraController mCameraController;

    private final MutableLiveData<Integer> mLensFacing = new MutableLiveData<>(CameraCharacteristics.LENS_FACING_BACK);
    private final MutableLiveData<Boolean> mCaptureDone = new MutableLiveData<>(false);
    private final MutableLiveData<String> mFilePath = new MutableLiveData<>();
    
    // UI Notification Events
    private final MutableLiveData<Integer> mToastEvent = new MutableLiveData<>();
    private final MutableLiveData<Integer> mErrorEvent = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mShowResultRequest = new MutableLiveData<>();

    public Camera2ViewModel(@NonNull Application application) {
        super(application);
        mCameraController = MyCameraController.getInstance();
        mCameraController.registerCallback(this);
    }

    // --- Actions for Activity to call ---

    public void startCameraThread() { mCameraController.startCameraThread(); }
    public void stopCameraThread() { mCameraController.stopCameraThread(); }

    public void openCamera(Context context, int facing) {
        mLensFacing.setValue(facing);
        mCameraController.openCamera(context, facing);
    }

    public void closeCamera() { mCameraController.closeCamera(); }
    public void capturePicture() { mCameraController.capturePicture(); }
    public void performShowResult() { mShowResultRequest.setValue(true); }

    public void switchCamera(Context context, boolean isFront) {
        int facing = isFront ? CameraCharacteristics.LENS_FACING_FRONT : CameraCharacteristics.LENS_FACING_BACK;
        mLensFacing.setValue(facing);
        mCameraController.closeCamera();
        mCameraController.openCamera(context, facing);
    }

    // --- LiveData for Activity to observe ---

    public LiveData<Integer> getLensFacing() { return mLensFacing; }
    public LiveData<Boolean> getCaptureDone() { return mCaptureDone; }
    public LiveData<String> getFilePath() { return mFilePath; }
    public LiveData<Integer> getToastEvent() { return mToastEvent; }
    public LiveData<Integer> getErrorEvent() { return mErrorEvent; }
    public LiveData<Boolean> getShowResultRequest() { return mShowResultRequest; }

    public void resetShowResultRequest() { mShowResultRequest.setValue(false); }

    // --- Controller Callbacks ---

    @Override
    public void onCaptureDone() {
        mCaptureDone.postValue(true);
        mToastEvent.postValue(R.string.demo_camera2_capture_done_toast);
    }

    @Override
    public void info(String str) {
        Utils.info(this, "Camera Info: " + str);
    }

    @Override
    public void onDeviceStateError(int code) { mErrorEvent.postValue(code); }

    @Override
    public String getPath() {
        File outputDir = new File(getApplication().getFilesDir(), "images");
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            Utils.error(this, "Failed to create images directory");
        }
        File outputFile = new File(outputDir, System.currentTimeMillis() + ".jpg");
        String path = outputFile.getAbsolutePath();
        mFilePath.postValue(path);
        return path;
    }

    @Override
    public void onSaveImageComplete() {
        mToastEvent.postValue(R.string.demo_camera2_save_complete_toast);
    }

    public void resetCaptureDone() { mCaptureDone.setValue(false); }
}
