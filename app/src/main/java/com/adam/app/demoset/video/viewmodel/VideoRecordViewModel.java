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

    private final MutableLiveData<Boolean> _isRecording = new MutableLiveData<>(false);
    public final LiveData<Boolean> isRecording = _isRecording;

    private final MutableLiveData<String> _timerText = new MutableLiveData<>("00:00:00");
    public final LiveData<String> timerText = _timerText;

    private final MutableLiveData<Boolean> _canPlay = new MutableLiveData<>(false);
    public final LiveData<Boolean> canPlay = _canPlay;

    private final MutableLiveData<String> _filePath = new MutableLiveData<>();
    public final LiveData<String> filePath = _filePath;

    private final MutableLiveData<Integer> _errorResult = new MutableLiveData<>();
    public final LiveData<Integer> errorResult = _errorResult;

    private final MutableLiveData<String> _failMsg = new MutableLiveData<>();
    public final LiveData<String> failMsg = _failMsg;

    private final MutableLiveData<String> _infoMsg = new MutableLiveData<>();
    public final LiveData<String> infoMsg = _infoMsg;

    private long baseTime;

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
        boolean nowRecording = !mManager.isRecording();
        if (nowRecording) {
            mManager.startRecord(getApplication());
        } else {
            mManager.stopRecord();
        }
        _isRecording.setValue(nowRecording);
        if (nowRecording) {
            baseTime = SystemClock.elapsedRealtime();
            _canPlay.setValue(false);
        } else {
            _canPlay.setValue(true);
        }
    }

    public void updateTimer(long currentTime) {
        long time = currentTime - baseTime;
        int h = (int) (time / 3600000);
        int m = (int) (time - h * 3600000) / 60000;
        int s = (int) (time - h * 3600000 - m * 60000) / 1000;
        String hh = h < 10 ? "0" + h : h + "";
        String mm = m < 10 ? "0" + m : m + "";
        String ss = s < 10 ? "0" + s : s + "";
        _timerText.setValue(TextUtils.concat(hh, ":", mm, ":", ss).toString());
    }

    public Size getPreviewSize() {
        return mManager.getPreviewSize();
    }

    @Override
    public void onError(int result) {
        _errorResult.postValue(result);
    }

    @Override
    public void onFail(String msg) {
        _failMsg.postValue(msg);
    }

    @Override
    public void onInfo(String msg) {
        _infoMsg.postValue(msg);
    }

    @Override
    public String getPath() {
        File fileDir = getApplication().getFilesDir();
        String fileName = System.currentTimeMillis() + ".mp4";
        File outputDir = new File(fileDir, "videos");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        File outputFile = new File(outputDir, fileName);
        String path = outputFile.getPath();
        _filePath.postValue(path);
        return path;
    }
}
