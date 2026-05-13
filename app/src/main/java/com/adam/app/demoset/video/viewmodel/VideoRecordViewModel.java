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
import android.util.Size;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.video.controller.VideoRecordManager;

import java.io.File;

public class VideoRecordViewModel extends AndroidViewModel implements VideoRecordManager.RecordListener {

    private final VideoRecordManager mManager;

    private final MutableLiveData<Boolean> mIsRecording = new MutableLiveData<>(false);
    private final MutableLiveData<String> mTimerText = new MutableLiveData<>("00:00:00");
    private final MutableLiveData<Boolean> mCanPlay = new MutableLiveData<>(false);
    private final MutableLiveData<String> mFilePath = new MutableLiveData<>();
    private final MutableLiveData<Integer> mErrorResult = new MutableLiveData<>();
    private final MutableLiveData<Integer> mFailResId = new MutableLiveData<>();
    private final MutableLiveData<Integer> mInfoResId = new MutableLiveData<>();

    private long mBaseTime;

    public VideoRecordViewModel(@NonNull Application application) {
        super(application);
        mManager = VideoRecordManager.getInstance();
        mManager.registerListener(this);
    }

    public void startCameraThread() {
        mManager.startCameraThread();
    }

    public void stopCameraThread() {
        mManager.stopCameraThread();
    }

    public void openCamera(TextureView textureView) {
        mManager.openCamera(getApplication(), textureView);
    }

    public void closeCamera() {
        mManager.closeCamera();
    }

    public void toggleRecord() {
        if (mManager.isRecording()) {
            stopRecording();
            return;
        }
        startRecording();
    }

    private void startRecording() {
        mManager.startRecord(getApplication());
        mBaseTime = SystemClock.elapsedRealtime();
        mIsRecording.setValue(true);
        mCanPlay.setValue(false);
    }

    private void stopRecording() {
        mManager.stopRecord();
        mIsRecording.setValue(false);
        mCanPlay.setValue(true);
    }

    public void updateTimer(long currentTime) {
        long time = currentTime - mBaseTime;
        int h = (int) (time / 3600000);
        int m = (int) (time - h * 3600000) / 60000;
        int s = (int) (time - h * 3600000 - m * 60000) / 1000;
        String hh = h < 10 ? "0" + h : h + "";
        String mm = m < 10 ? "0" + m : m + "";
        String ss = s < 10 ? "0" + s : s + "";
        mTimerText.setValue(TextUtils.concat(hh, ":", mm, ":", ss).toString());
    }

    public Size getPreviewSize() {
        return mManager.getPreviewSize();
    }

    public void configureTransform(int width, int height, int rotation) {
        mManager.configureTransform(width, height, rotation);
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
        File outputDir = new File(fileDir, "videos");
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            return null;
        }
        String fileName = System.currentTimeMillis() + ".mp4";
        File outputFile = new File(outputDir, fileName);
        String path = outputFile.getPath();
        mFilePath.postValue(path);
        return path;
    }
}
