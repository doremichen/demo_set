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

package com.adam.app.demoset.privacy.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityPrivacyCaptureBinding;
import com.adam.app.demoset.privacy.domain.strategy.Api34DetectionStrategy;
import com.adam.app.demoset.privacy.domain.strategy.Api35DetectionStrategy;
import com.adam.app.demoset.privacy.domain.strategy.IPrivacyDetectionStrategy;
import com.adam.app.demoset.privacy.viewmodel.PrivacyViewModel;
import com.adam.app.demoset.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Activity for demonstrating Screen Recording Detection.
 * Uses Strategy pattern to handle multiple Android version detection APIs.
 */
@AndroidEntryPoint
public class PrivacyCaptureActivity extends AppCompatActivity {

    private static final String sTAG = "PrivacyCaptureActivity";

    private PrivacyViewModel mViewModel;
    private ActivityPrivacyCaptureBinding mBinding;
    private final List<IPrivacyDetectionStrategy> mStrategies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_privacy_capture);
        mViewModel = new ViewModelProvider(this).get(PrivacyViewModel.class);

        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(this);

        initStrategies();
        initView();
        initListener();
        observeViewModel();
    }

    private void initStrategies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            mStrategies.add(new Api34DetectionStrategy());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            mStrategies.add(new Api35DetectionStrategy());
        }
    }

    private void handleDetectionEvent(boolean active) {
        runOnUiThread(() -> {
            if (mViewModel != null) {
                mViewModel.reportCaptureStatus(active);
            }
            if (active) {
                Utils.showAlertDialog(PrivacyCaptureActivity.this, 
                        getString(R.string.privacy_capture_status_capturing), 
                        null);
            }
        });
    }

    private void initView() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_demo_privacy_capture);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initListener() {
        mBinding.btnStart.setOnClickListener(v -> startMonitoringFlow());
        mBinding.btnStop.setOnClickListener(v -> stopMonitoringFlow());
    }

    private void startMonitoringFlow() {
        mViewModel.startMonitoring();
        for (IPrivacyDetectionStrategy strategy : mStrategies) {
            strategy.register(this, this::handleDetectionEvent);
        }
        Utils.showToast(this, getString(R.string.privacy_capture_msg_monitoring_started));
    }

    private void stopMonitoringFlow() {
        for (IPrivacyDetectionStrategy strategy : mStrategies) {
            strategy.unregister(this);
        }
        mViewModel.stopMonitoring();
        Utils.showToast(this, getString(R.string.privacy_capture_msg_monitoring_stopped));
    }

    private void observeViewModel() {
        mViewModel.getIsSecureFlagEnabled().observe(this, enabled -> {
            if (enabled) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Boolean.TRUE.equals(mViewModel.getIsMonitoring().getValue())) {
            stopMonitoringFlow();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
