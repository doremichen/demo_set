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

package com.adam.app.demoset.video.viewmodel;

import android.app.Application;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.video.domain.repository.VideoRecordListener;
import com.adam.app.demoset.video.domain.usecase.CameraUseCase;
import com.adam.app.demoset.video.domain.usecase.RecordUseCase;
import com.adam.app.demoset.video.domain.usecase.StartPreviewUseCase;

import java.io.File;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for video recording, following Clean Architecture.
 * Interacts with Use Cases to perform business logic.
 */
@HiltViewModel
public class VideoRecordViewModel extends AndroidViewModel implements VideoRecordListener {

    private static final String INITIAL_TIMER_TEXT = "00:00:00";
    private static final String VIDEO_FOLDER_NAME = "videos";
    private static final String VIDEO_EXTENSION = ".mp4";

    // Time constants
    private static final int MILLIS_IN_HOUR = 3600000;
    private static final int MILLIS_IN_MINUTE = 60000;
    private static final int MILLIS_IN_SECOND = 1000;
    private static final int TIME_UNIT_MAX = 10;

    private final CameraUseCase mCameraUseCase;
    private final RecordUseCase mRecordUseCase;
    private final StartPreviewUseCase mStartPreviewUseCase;

    private final MutableLiveData<Boolean> mIsRecording = new MutableLiveData<>(false);
    private final MutableLiveData<String> mTimerText = new MutableLiveData<>(INITIAL_TIMER_TEXT);
    private final MutableLiveData<Boolean> mCanPlay = new MutableLiveData<>(false);
    private final MutableLiveData<String> mFilePath = new MutableLiveData<>();
    private final MutableLiveData<Integer> mErrorResult = new MutableLiveData<>();
    private final MutableLiveData<Integer> mFailResId = new MutableLiveData<>();
    private final MutableLiveData<Integer> mInfoResId = new MutableLiveData<>();

    private long mBaseTime;

    @Inject
    public VideoRecordViewModel(@NonNull Application application,
                                CameraUseCase cameraUseCase,
                                RecordUseCase recordUseCase,
                                StartPreviewUseCase startPreviewUseCase) {
        super(application);
        this.mCameraUseCase = cameraUseCase;
        this.mRecordUseCase = recordUseCase;
        this.mStartPreviewUseCase = startPreviewUseCase;
        this.mCameraUseCase.registerListener(this);
    }

    public void startCameraThread() {
        mCameraUseCase.startThread();
    }

    public void stopCameraThread() {
        mCameraUseCase.stopThread();
    }

    public void openCamera(TextureView textureView) {
        mCameraUseCase.openCamera(getApplication(), textureView);
    }

    public void closeCamera() {
        mCameraUseCase.closeCamera();
    }

    /**
     * Toggles the recording state.
     */
    public void toggleRecord() {
        if (mRecordUseCase.isRecording()) {
            stopRecording();
            return;
        }
        startRecording();
    }

    private void startRecording() {
        mRecordUseCase.startRecord(getApplication());
        mBaseTime = SystemClock.elapsedRealtime();
        mIsRecording.setValue(true);
        mCanPlay.setValue(false);
    }

    private void stopRecording() {
        mRecordUseCase.stopRecord();
        mIsRecording.setValue(false);
        mCanPlay.setValue(true);
    }

    /**
     * Updates the recording timer based on current time.
     *
     * @param currentTime The current elapsed realtime.
     */
    public void updateTimer(long currentTime) {
        long time = currentTime - mBaseTime;
        int h = (int) (time / MILLIS_IN_HOUR);
        int m = (int) (time - h * MILLIS_IN_HOUR) / MILLIS_IN_MINUTE;
        int s = (int) (time - h * MILLIS_IN_HOUR - m * MILLIS_IN_MINUTE) / MILLIS_IN_SECOND;

        String hh = h < TIME_UNIT_MAX ? "0" + h : String.valueOf(h);
        String mm = m < TIME_UNIT_MAX ? "0" + m : String.valueOf(m);
        String ss = s < TIME_UNIT_MAX ? "0" + s : String.valueOf(s);

        mTimerText.setValue(TextUtils.concat(hh, ":", mm, ":", ss).toString());
    }


    public void configureTransform(int width, int height, int rotation) {
        mCameraUseCase.configureTransform(width, height, rotation);
    }

    public LiveData<Boolean> isRecording() {
        return mIsRecording;
    }

    public LiveData<String> getTimerText() {
        return mTimerText;
    }

    public LiveData<Boolean> canPlay() {
        return mCanPlay;
    }

    public LiveData<String> getFilePath() {
        return mFilePath;
    }

    public LiveData<Integer> getErrorResult() {
        return mErrorResult;
    }

    public LiveData<Integer> getFailResId() {
        return mFailResId;
    }

    public LiveData<Integer> getInfoResId() {
        return mInfoResId;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Prevent memory leaks by unregistering the listener
        mCameraUseCase.unregisterListener(this);
    }

    @Override
    public void onError(int result) {
        mErrorResult.postValue(result);
    }

    @Override
    public void onFail(int resId) {
        mFailResId.postValue(resId);
    }

    @Override
    public void onInfo(int resId) {
        mInfoResId.postValue(resId);
    }

    @Override
    public String getPath() {
        File fileDir = getApplication().getFilesDir();
        File outputDir = new File(fileDir, VIDEO_FOLDER_NAME);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            return null;
        }
        String fileName = System.currentTimeMillis() + VIDEO_EXTENSION;
        File outputFile = new File(outputDir, fileName);
        String path = outputFile.getPath();
        mFilePath.postValue(path);
        return path;
    }
}
