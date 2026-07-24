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

package com.adam.app.demoset.workmanager.presentation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.WorkInfo;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoExecuteTaskBinding;
import com.adam.app.demoset.utils.DemoAppConstants;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Activity to execute the blur task.
 */
@AndroidEntryPoint
public class DemoExecuteTaskAct extends AppCompatActivity {

    private WorkManagerViewModel mViewModel;
    private ActivityDemoExecuteTaskBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDemoExecuteTaskBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        UIUtils.applySystemBarInsets(mBinding.getRoot(), mBinding.appHeader);

        mViewModel = new ViewModelProvider(this).get(WorkManagerViewModel.class);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(this);

        // Update image URI if intent provided
        String uriStr = getIntent().getStringExtra(DemoAppConstants.THE_SELECTED_IMAGE);
        if (!TextUtils.isEmpty(uriStr)) {
            Utils.info(this, "uriStr: " + uriStr);
            mViewModel.updateImgUri(Uri.parse(uriStr));
        }

        // Observer for work info
        mViewModel.getWorkInfo().observe(this, workInfos -> {
            if (workInfos == null || workInfos.isEmpty()) {
                return;
            }

            WorkInfo workInfo = workInfos.get(0);
            boolean isFinished = workInfo.getState().isFinished();
            updateButtonVisibility(!isFinished);

            if (isFinished) {
                Data outputData = workInfo.getOutputData();
                String outputImageUriStr = outputData.getString(DemoAppConstants.THE_SELECTED_IMAGE);
                if (!TextUtils.isEmpty(outputImageUriStr)) {
                    mViewModel.setOutputUri(outputImageUriStr);
                }
            }
        });

        // Execute button listener
        mBinding.goButton.setOnClickListener(v -> mViewModel.applyBlur(getBlurLevel()));

        // Result viewing button listener
        mBinding.seeFileButton.setOnClickListener(v -> {
            Uri currentUri = mViewModel.getOutputUri().getValue();
            if (currentUri != null) {
                Intent actionView = new Intent(Intent.ACTION_VIEW, currentUri);
                if (actionView.resolveActivity(getPackageManager()) != null) {
                    startActivity(actionView);
                }
            }
        });
    }

    /**
     * Updates UI components visibility based on task state.
     *
     * @param isProcessing Flag indicating if the task is currently running.
     */
    private void updateButtonVisibility(boolean isProcessing) {
        mBinding.progressBar.setVisibility(isProcessing ? android.view.View.VISIBLE : android.view.View.GONE);
        mBinding.cancelButton.setVisibility(isProcessing ? android.view.View.VISIBLE : android.view.View.GONE);
        mBinding.goButton.setVisibility(isProcessing ? android.view.View.GONE : android.view.View.VISIBLE);
    }

    /**
     * Retrieves the selected blur level from the radio group.
     *
     * @return Selected blur level constant.
     */
    private int getBlurLevel() {
        int choiceId = mBinding.radioBlurGroup.getCheckedRadioButtonId();
        if (choiceId == R.id.radio_blur_lv_2) return WorkManagerViewModel.BLUR_LEVEL_2;
        if (choiceId == R.id.radio_blur_lv_3) return WorkManagerViewModel.BLUR_LEVEL_3;
        return WorkManagerViewModel.BLUR_LEVEL_1;
    }
}
