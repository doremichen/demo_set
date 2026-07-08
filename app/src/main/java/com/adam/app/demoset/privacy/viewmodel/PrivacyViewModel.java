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

package com.adam.app.demoset.privacy.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.R;
import com.adam.app.demoset.privacy.domain.MonitorScreenCaptureUseCase;
import com.adam.app.demoset.utils.Utils;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for Screen Recording Detection Demo.
 */
@HiltViewModel
public class PrivacyViewModel extends AndroidViewModel {

    private final MonitorScreenCaptureUseCase mMonitorUseCase;
    private final MutableLiveData<Boolean> mIsCaptured = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mIsSecureFlagEnabled = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mIsMonitoring = new MutableLiveData<>(false);

    @Inject
    public PrivacyViewModel(@NonNull Application application, MonitorScreenCaptureUseCase monitorUseCase) {
        super(application);
        this.mMonitorUseCase = monitorUseCase;
    }

    /**
     * Start the monitoring session in Domain layer.
     */
    public void startMonitoring() {
        Utils.info(this, "startMonitoring");
        mIsMonitoring.setValue(true);
        mMonitorUseCase.start(this::setCaptured);
    }

    /**
     * Stop the monitoring session in Domain layer.
     */
    public void stopMonitoring() {
        Utils.info(this, "stopMonitoring");
        mIsMonitoring.setValue(false);
        mMonitorUseCase.stop();
    }

    public LiveData<Boolean> getIsMonitoring() {
        return mIsMonitoring;
    }

    /**
     * Report the system capture event to the Domain layer.
     *
     * @param isCaptured Detected capture status from Activity.
     */
    public void reportCaptureStatus(boolean isCaptured) {
        Utils.info(this, "reportCaptureStatus: " + isCaptured);
        if (isCaptured) {
            Utils.showToast(getApplication(), getApplication().getString(R.string.privacy_capture_msg_event_reported));
        }
        mMonitorUseCase.processCaptureEvent(isCaptured);
    }

    public LiveData<Boolean> getIsCaptured() {
        return mIsCaptured;
    }

    public void setCaptured(boolean captured) {
        Utils.info(this, "setCaptured: " + captured);
        mIsCaptured.postValue(captured);
    }

    public MutableLiveData<Boolean> getIsSecureFlagEnabled() {
        return mIsSecureFlagEnabled;
    }
}
