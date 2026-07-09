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

package com.adam.app.demoset.jobService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoJobSvrBinding;
import com.adam.app.demoset.jobService.viewmodel.DemoJobSvrViewModel;
import com.adam.app.demoset.utils.DemoAppConstants;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;

import java.lang.ref.WeakReference;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Demo Background Execution Evolution Activity.
 */
@AndroidEntryPoint
public class DemoJobSvrAct extends AppCompatActivity {

    private ActivityDemoJobSvrBinding mBinding;
    private DemoJobSvrViewModel mViewModel;
    private TransferReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_demo_job_svr);
        mViewModel = new ViewModelProvider(this).get(DemoJobSvrViewModel.class);

        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(this);

        UIUtils.applySystemBarInsets(mBinding.rootLayout, mBinding.toolbar);

        initViews();
    }

    private void initViews() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mBinding.toggleGroupMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            // check if the button is checked
            if (!isChecked) {
                return;
            }

            Utils.info(this, "Mode selected: " + checkedId);
            mViewModel.setModernMode(checkedId == R.id.btn_mode_modern);
        });

        mBinding.toggleGroupMode.clearChecked();
        mBinding.toggleGroupMode.check(R.id.btn_mode_modern);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mReceiver = new TransferReceiver(mViewModel);
        IntentFilter filter = new IntentFilter(DemoAppConstants.ACTION_TRANSFER_UPDATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(mReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(mReceiver, filter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Static inner class for BroadcastReceiver to avoid memory leaks.
     */
    private static class TransferReceiver extends BroadcastReceiver {
        private final WeakReference<DemoJobSvrViewModel> mViewModelRef;

        TransferReceiver(DemoJobSvrViewModel viewModel) {
            this.mViewModelRef = new WeakReference<>(viewModel);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            DemoJobSvrViewModel viewModel = mViewModelRef.get();
            if (viewModel != null && intent != null) {
                int progress = intent.getIntExtra(DemoAppConstants.KEY_PROGRESS, 0);
                String status = intent.getStringExtra(DemoAppConstants.KEY_STATUS);
                viewModel.updateProgress(progress);
                if (status != null) {
                    viewModel.updateStatus(status);
                }
            }
        }
    }
}
