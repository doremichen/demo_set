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

package com.adam.app.demoset.jobService.viewmodel;

import android.app.Application;
import android.app.job.JobInfo;
import android.content.Context;
import android.net.wifi.WifiManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.R;
import com.adam.app.demoset.jobService.domain.ManageDataTransferUseCase;
import com.adam.app.demoset.utils.Utils;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for Background Execution Evolution Demo.
 * Manages states for Legacy FGS vs. Modern Job scenarios.
 */
@HiltViewModel
public class DemoJobSvrViewModel extends AndroidViewModel {

    private final ManageDataTransferUseCase mUseCase;

    private final MutableLiveData<Boolean> mIsIdleRequired = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mIsChargingRequired = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mIsModernMode = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> mIsUserInitiated = new MutableLiveData<>(true);
    private final MutableLiveData<Integer> mNetworkTypeId = new MutableLiveData<>(R.id.radio_net_any);
    private final MutableLiveData<Boolean> mIsWifiEnabled = new MutableLiveData<>(true);
    
    private final MutableLiveData<Integer> mProgress = new MutableLiveData<>(0);
    private final MutableLiveData<String> mStatusText = new MutableLiveData<>("");

    @Inject
    public DemoJobSvrViewModel(@NonNull Application application, ManageDataTransferUseCase useCase) {
        super(application);
        this.mUseCase = useCase;
        this.mStatusText.setValue(application.getString(R.string.bg_exec_status_idle));
        checkWifiState();
    }

    private void checkWifiState() {
        WifiManager wifiManager = (WifiManager) getApplication().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            mIsWifiEnabled.setValue(wifiManager.isWifiEnabled());
        }
    }

    public MutableLiveData<Boolean> getIsIdleRequired() {
        return mIsIdleRequired;
    }

    public MutableLiveData<Boolean> getIsChargingRequired() {
        return mIsChargingRequired;
    }

    public LiveData<Boolean> getIsModernMode() {
        return mIsModernMode;
    }

    public LiveData<Boolean> getIsUserInitiated() {
        return mIsUserInitiated;
    }

    public MutableLiveData<Integer> getNetworkTypeId() {
        return mNetworkTypeId;
    }

    public LiveData<Boolean> getIsWifiEnabled() {
        return mIsWifiEnabled;
    }

    public LiveData<Integer> getProgress() {
        return mProgress;
    }

    public LiveData<String> getStatusText() {
        return mStatusText;
    }

    public void setModernMode(boolean modern) {
        mIsModernMode.setValue(modern);
    }

    public void setUserInitiated(boolean userInitiated) {
        mIsUserInitiated.setValue(userInitiated);
        // User-initiated jobs CANNOT have idle constraint.
        if (userInitiated) {
            mIsIdleRequired.setValue(false);
        }
    }

    public void setNetworkTypeId(int id) {
        mNetworkTypeId.setValue(id);
    }

    /**
     * Starts the background task based on current settings.
     */
    public void startTask() {
        boolean isModern = Boolean.TRUE.equals(mIsModernMode.getValue());
        boolean isUI = Boolean.TRUE.equals(mIsUserInitiated.getValue());

        Utils.info(this, "startTask. Mode: " + (isModern ? "Modern" : "Legacy"));
        
        Integer typeId = mNetworkTypeId.getValue();
        int networkType = (typeId != null && typeId == R.id.radio_net_unmetered) 
                ? JobInfo.NETWORK_TYPE_UNMETERED 
                : JobInfo.NETWORK_TYPE_ANY;

        mUseCase.executeStart(
                isModern,
                isUI,
                networkType,
                Boolean.TRUE.equals(mIsChargingRequired.getValue()),
                Boolean.TRUE.equals(mIsIdleRequired.getValue())
        );

        if (isModern && !isUI) {
            // Deferred Job: Inform user it's scheduled
            String msg = getApplication().getString(R.string.bg_exec_status_waiting);
            mStatusText.setValue(msg);
            Utils.showToast(getApplication(), msg);
        }
    }

    /**
     * Stops the running task.
     */
    public void stopTask() {
        Utils.info(this, "stopTask");
        mUseCase.executeStop();
        mStatusText.setValue(getApplication().getString(R.string.bg_exec_status_cancelled));
        mProgress.setValue(0);
    }

    public void updateStatus(String status) {
        mStatusText.postValue(status);
    }

    public void updateProgress(int progress) {
        mProgress.postValue(progress);
    }
}
