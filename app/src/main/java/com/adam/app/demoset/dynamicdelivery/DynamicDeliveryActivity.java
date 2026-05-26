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

package com.adam.app.demoset.dynamicdelivery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDynamicDeliveryBinding;
import com.adam.app.demoset.utils.Utils;
import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;

import java.util.Collections;

public class DynamicDeliveryActivity extends AppCompatActivity {

    private static final String sMODULE_NAME = "dynamic_feature";
    private ActivityDynamicDeliveryBinding mBinding;
    private SplitInstallManager mSplitInstallManager;
    private int mSessionId = 0;

    private final SplitInstallStateUpdatedListener mListener = new SplitInstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(SplitInstallSessionState state) {
            if (state.sessionId() == mSessionId) {
                updateStatus(state);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_dynamic_delivery);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_demo_dynamic_delivery);
        }

        mSplitInstallManager = SplitInstallManagerFactory.create(this);

        mBinding.btnInstall.setOnClickListener(v -> installModule());
        mBinding.btnLaunch.setOnClickListener(v -> launchModule());
        mBinding.btnUninstall.setOnClickListener(v -> uninstallModule());

        checkModuleStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSplitInstallManager.registerListener(mListener);
    }

    @Override
    protected void onPause() {
        mSplitInstallManager.unregisterListener(mListener);
        super.onPause();
    }

    private void checkModuleStatus() {
        boolean isInstalled = mSplitInstallManager.getInstalledModules().contains(sMODULE_NAME);
        if (isInstalled) {
            mBinding.statusText.setText(getString(R.string.msg_feature_status, "Installed"));
            mBinding.btnInstall.setEnabled(false);
            mBinding.btnLaunch.setEnabled(true);
            mBinding.btnUninstall.setEnabled(true);
        } else {
            mBinding.statusText.setText(getString(R.string.msg_feature_status, "Not Installed"));
            mBinding.btnInstall.setEnabled(true);
            mBinding.btnLaunch.setEnabled(false);
            mBinding.btnUninstall.setEnabled(false);
        }
    }

    private void installModule() {
        addLog("Starting installation for: " + sMODULE_NAME);
        SplitInstallRequest request = SplitInstallRequest.newBuilder()
                .addModule(sMODULE_NAME)
                .build();

        mSplitInstallManager.startInstall(request)
                .addOnSuccessListener(id -> {
                    mSessionId = id;
                    addLog("Request accepted, session ID: " + id);
                })
                .addOnFailureListener(e -> {
                    addLog("Installation failed: " + e.getMessage());
                    Utils.showToast(this, "Installation failed: " + e.getMessage());
                });
    }

    private void updateStatus(SplitInstallSessionState state) {
        String statusLabel;
        switch (state.status()) {
            case SplitInstallSessionStatus.PENDING:
                statusLabel = "Pending";
                mBinding.progressBar.setVisibility(View.VISIBLE);
                mBinding.progressBar.setIndeterminate(true);
                break;
            case SplitInstallSessionStatus.DOWNLOADING:
                statusLabel = "Downloading";
                mBinding.progressBar.setVisibility(View.VISIBLE);
                mBinding.progressBar.setIndeterminate(false);
                int progress = (int) (100 * state.bytesDownloaded() / state.totalBytesToDownload());
                mBinding.progressBar.setProgress(progress);
                break;
            case SplitInstallSessionStatus.INSTALLING:
                statusLabel = "Installing";
                mBinding.progressBar.setIndeterminate(true);
                break;
            case SplitInstallSessionStatus.INSTALLED:
                statusLabel = "Installed";
                mBinding.progressBar.setVisibility(View.GONE);
                checkModuleStatus();
                addLog("Module " + sMODULE_NAME + " installed successfully!");
                break;
            case SplitInstallSessionStatus.FAILED:
                statusLabel = "Failed (" + state.errorCode() + ")";
                mBinding.progressBar.setVisibility(View.GONE);
                addLog("Installation failed with error code: " + state.errorCode());
                break;
            case SplitInstallSessionStatus.CANCELED:
                statusLabel = "Canceled";
                mBinding.progressBar.setVisibility(View.GONE);
                break;
            default:
                statusLabel = "Unknown (" + state.status() + ")";
        }
        mBinding.statusText.setText(getString(R.string.msg_feature_status, statusLabel));
        addLog("Status Update: " + statusLabel);
    }

    private void launchModule() {
        addLog("Launching module: " + sMODULE_NAME);
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), "com.adam.app.demoset.dynamicfeature.DynamicFeatureActivity");
        startActivity(intent);
    }

    private void uninstallModule() {
        addLog("Requesting deferred uninstallation...");
        mSplitInstallManager.deferredUninstall(Collections.singletonList(sMODULE_NAME))
                .addOnSuccessListener(v -> {
                    addLog("Uninstallation requested. It will happen in the background when the app is not in use.");
                    Utils.showToast(this, "Uninstallation requested");
                })
                .addOnFailureListener(e -> {
                    addLog("Uninstallation request failed: " + e.getMessage());
                });
    }

    private void addLog(String message) {
        String currentLog = mBinding.logText.getText().toString();
        String newLog = currentLog + "\n> " + message;
        mBinding.logText.setText(newLog);
        Utils.info(this, message);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
