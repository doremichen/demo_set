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

import com.adam.app.demoset.R;
import com.adam.app.demoset.dynamicdelivery.controller.DynamicDeliveryController;
import com.adam.app.demoset.dynamicdelivery.viewmodel.state.InstallSessionStateContext;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;

public class DynamicDeliveryViewModel extends AndroidViewModel implements DynamicDeliveryController.OnControllerCallback {

    private final DynamicDeliveryController mController;
    private final InstallSessionStateContext mStateContext;
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
        mStateContext = new InstallSessionStateContext();
        mController.registerListener(mListener);
        checkModuleStatus();
        mStatus.setValue(getApplication().getString(R.string.state_unknown, -1));
    }

    // --- Helper for Localization ---
    public String getString(int resId) {
        return getApplication().getString(resId);
    }

    public String getString(int resId, Object... formatArgs) {
        return getApplication().getString(resId, formatArgs);
    }

    @Override
    protected void onCleared() {
        mController.unregisterListener(mListener);
        super.onCleared();
    }

    // --- Getters for LiveData ---
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
        mStatus.setValue(isInstalled ? getString(R.string.state_installed) : getString(R.string.state_not_installed));
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

    // --- State Update Methods (Called by State Pattern) ---

    public void updateStatus(String label) {
        mStatus.setValue(label);
        addLog("Status Update: " + label);
    }

    public void updateProgress(int progress) {
        mProgress.setValue(progress);
    }

    public void updateProgressVisible(boolean visible) {
        mIsProgressVisible.setValue(visible);
    }

    public void addLog(String message) {
        StringBuilder current = mLogs.getValue();
        if (current == null) current = new StringBuilder();
        current.append("\n> ").append(message);
        mLogs.setValue(current);
    }

    // --- Controller Callbacks ---

    @Override
    public void onSessionIdReceived(int sessionId) {
        mSessionId = sessionId;
        addLog("Request accepted, session ID: " + sessionId);
    }

    @Override
    public void onFailure(String errorMessage) {
        String friendlyMessage = errorMessage;
        if (errorMessage.contains("-5")) {
            friendlyMessage = getString(R.string.err_msg_api_not_available);
        }
        String logMsg = getString(R.string.log_action_failed, friendlyMessage);
        addLog(logMsg);
        mToastMessage.setValue(logMsg);
    }

    @Override
    public void onUninstallRequested() {
        String logMsg = getString(R.string.log_uninstallation_requested);
        addLog(logMsg);
        mToastMessage.setValue(logMsg);
    }

    @Override
    public void onStateUpdate(SplitInstallSessionState state) {
        // Delegate processing to the State Context
        mStateContext.handle(this, state);
    }
}
