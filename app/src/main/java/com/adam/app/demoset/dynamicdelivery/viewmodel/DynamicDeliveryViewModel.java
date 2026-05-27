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

package com.adam.app.demoset.dynamicdelivery.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.dynamicdelivery.controller.DynamicDeliveryController;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;

public class DynamicDeliveryViewModel extends AndroidViewModel implements DynamicDeliveryController.OnControllerCallback {

    private final DynamicDeliveryController mController;
    private int mSessionId = 0;

    private final MutableLiveData<String> mStatus = new MutableLiveData<>("Unknown");
    private final MutableLiveData<Integer> mProgress = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> mIsProgressVisible = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mIsInstallEnabled = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> mIsLaunchEnabled = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mIsUninstallEnabled = new MutableLiveData<>(false);
    private final MutableLiveData<StringBuilder> mLogs = new MutableLiveData<>(new StringBuilder());
    private final MutableLiveData<String> mToastMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mLaunchEvent = new MutableLiveData<>(false);

    private final SplitInstallStateUpdatedListener mListener = state -> {
        if (state.sessionId() == mSessionId) {
            onStateUpdate(state);
        }
    };

    public DynamicDeliveryViewModel(@NonNull Application application) {
        super(application);
        mController = new DynamicDeliveryController(application);
        mController.registerListener(mListener);
        checkModuleStatus();
    }

    @Override
    protected void onCleared() {
        mController.unregisterListener(mListener);
        super.onCleared();
    }

    public LiveData<String> getStatus() { return mStatus; }
    public LiveData<Integer> getProgress() { return mProgress; }
    public LiveData<Boolean> getIsProgressVisible() { return mIsProgressVisible; }
    public LiveData<Boolean> getIsInstallEnabled() { return mIsInstallEnabled; }
    public LiveData<Boolean> getIsLaunchEnabled() { return mIsLaunchEnabled; }
    public LiveData<Boolean> getIsUninstallEnabled() { return mIsUninstallEnabled; }
    public LiveData<StringBuilder> getLogs() { return mLogs; }
    public LiveData<String> getToastMessage() { return mToastMessage; }
    public LiveData<Boolean> getLaunchEvent() { return mLaunchEvent; }

    public void onLaunchEventHandled() {
        mLaunchEvent.setValue(false);
    }

    public void checkModuleStatus() {
        boolean isInstalled = mController.isModuleInstalled();
        updateUiForInstallation(isInstalled);
    }

    private void updateUiForInstallation(boolean isInstalled) {
        mStatus.setValue(isInstalled ? "Installed" : "Not Installed");
        mIsInstallEnabled.setValue(!isInstalled);
        mIsLaunchEnabled.setValue(isInstalled);
        mIsUninstallEnabled.setValue(isInstalled);
    }

    public void onInstallClicked() {
        addLog("Starting installation for: " + mController.getModuleName());
        mController.installModule(this);
    }

    public void onLaunchClicked() {
        addLog("Launching module: " + mController.getModuleName());
        mLaunchEvent.setValue(true);
    }

    public void onUninstallClicked() {
        addLog("Requesting deferred uninstallation...");
        mController.uninstallModule(this);
    }

    @Override
    public void onSessionIdReceived(int sessionId) {
        mSessionId = sessionId;
        addLog("Request accepted, session ID: " + sessionId);
    }

    @Override
    public void onFailure(String errorMessage) {
        String friendlyMessage = errorMessage;
        if (errorMessage.contains("-5")) {
            friendlyMessage = "API Not Available (-5): Deferred Uninstall requires a real Google Play Store environment and is not supported in local testing mode.";
        }
        addLog("Action failed: " + friendlyMessage);
        mToastMessage.setValue("Action failed: " + friendlyMessage);
    }

    @Override
    public void onUninstallRequested() {
        addLog("Uninstallation requested. It will happen in the background.");
        mToastMessage.setValue("Uninstallation requested");
    }

    @Override
    public void onStateUpdate(SplitInstallSessionState state) {
        String statusLabel;
        switch (state.status()) {
            case SplitInstallSessionStatus.PENDING:
                statusLabel = "Pending";
                mIsProgressVisible.setValue(true);
                break;
            case SplitInstallSessionStatus.DOWNLOADING:
                statusLabel = "Downloading";
                mIsProgressVisible.setValue(true);
                int progress = (int) (100 * state.bytesDownloaded() / state.totalBytesToDownload());
                mProgress.setValue(progress);
                break;
            case SplitInstallSessionStatus.INSTALLING:
                statusLabel = "Installing";
                mIsProgressVisible.setValue(true);
                break;
            case SplitInstallSessionStatus.INSTALLED:
                statusLabel = "Installed";
                mIsProgressVisible.setValue(false);
                checkModuleStatus();
                addLog("Module installed successfully!");
                break;
            case SplitInstallSessionStatus.FAILED:
                statusLabel = "Failed (" + state.errorCode() + ")";
                mIsProgressVisible.setValue(false);
                addLog("Installation failed with error code: " + state.errorCode());
                break;
            case SplitInstallSessionStatus.CANCELED:
                statusLabel = "Canceled";
                mIsProgressVisible.setValue(false);
                break;
            default:
                statusLabel = "Unknown (" + state.status() + ")";
        }
        mStatus.setValue(statusLabel);
        addLog("Status Update: " + statusLabel);
    }

    private void addLog(String message) {
        StringBuilder current = mLogs.getValue();
        if (current == null) current = new StringBuilder();
        current.append("\n> ").append(message);
        mLogs.setValue(current);
    }
}
