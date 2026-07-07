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

package com.adam.app.demoset.systemUI.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.databinding.ActivityDemoSysUiBinding;
import com.adam.app.demoset.systemUI.viewmodel.SystemUIViewModel;
import com.adam.app.demoset.utils.UIUtils;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Demo activity for System UI control, refactored with MVVM and Clean Architecture.
 */
@AndroidEntryPoint
public class DemoSysUIAct extends AppCompatActivity {

    private ActivityDemoSysUiBinding mBinding;
    private SystemUIViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Data Binding
        mBinding = ActivityDemoSysUiBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // ViewModel
        mViewModel = new ViewModelProvider(this).get(SystemUIViewModel.class);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(this);

        // Initial UI adjustments
        UIUtils.applySystemBarInsets(mBinding.getRoot(), mBinding.appBarWrapper);

        // Observe UI Status for side effects (System UI control)
        mViewModel.getUiStatus().observe(this, status -> {
            if (status != null) {
                applySystemUIState(status.isLowProfile(), status.isImmersive());
            }
        });

        // Observe Exit event
        mViewModel.getExitEvent().observe(this, exit -> {
            if (Boolean.TRUE.equals(exit)) {
                finish();
            }
        });
    }

    /**
     * Apply System UI state using WindowInsetsControllerCompat.
     */
    private void applySystemUIState(boolean isLowProfile, boolean isImmersive) {
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), mBinding.getRoot());
        if (controller == null) return;

        if (isImmersive) {
            // Hide System Bars
            controller.hide(WindowInsetsCompat.Type.systemBars());
            controller.hide(WindowInsetsCompat.Type.navigationBars());
            controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
        } else {
            // Show System Bars
            controller.show(WindowInsetsCompat.Type.systemBars());
            controller.show(WindowInsetsCompat.Type.navigationBars());

            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
            }

            // Simulating Low Profile by toggling light appearance
            controller.setAppearanceLightStatusBars(isLowProfile);
            controller.setAppearanceLightNavigationBars(isLowProfile);
        }

        // Request apply insets to ensure layout updates
        ViewCompat.requestApplyInsets(getWindow().getDecorView());
    }
}
