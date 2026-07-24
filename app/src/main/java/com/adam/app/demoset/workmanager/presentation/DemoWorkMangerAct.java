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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.WorkInfo;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoWorkMangerBinding;
import com.adam.app.demoset.utils.DemoAppConstants;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;

import java.io.File;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Legacy Demo Activity for WorkManager.
 */
@AndroidEntryPoint
public class DemoWorkMangerAct extends AppCompatActivity {

    private ActivityDemoWorkMangerBinding mBinding;
    private WorkManagerViewModel mViewModel;
    private boolean mIsShowImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDemoWorkMangerBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        UIUtils.applySystemBarInsets(mBinding.rootLayout, mBinding.tvTitle);

        mViewModel = new ViewModelProvider(this).get(WorkManagerViewModel.class);

        // Observer for work info state
        mViewModel.getWorkInfo().observe(this, workInfos -> {
            if (workInfos == null || workInfos.isEmpty()) {
                return;
            }

            WorkInfo workInfo = workInfos.get(0);
            boolean isFinished = workInfo.getState().isFinished();

            if (isFinished) {
                Data outputData = workInfo.getOutputData();
                String outputImageUriStr = outputData.getString(DemoAppConstants.THE_SELECTED_IMAGE);
                if (!TextUtils.isEmpty(outputImageUriStr)) {
                    mBinding.btnTestWm.setText(R.string.action_show_img);
                }
            }
        });

        mBinding.btnTestWm.setOnClickListener(this::onMyWork);
    }

    /**
     * Handles the execution button click.
     *
     * @param v The view that was clicked.
     */
    public void onMyWork(View v) {
        if (!mIsShowImage) {
            mViewModel.applyBlur(WorkManagerViewModel.BLUR_LEVEL_1);
        } else {
            showImage();
            mBinding.btnTestWm.setText(R.string.action_test_blur_img);
        }
        mIsShowImage = !mIsShowImage;
    }

    /**
     * Displays the blurred image using an external viewer.
     */
    private void showImage() {
        if (Utils.sImagePath == null) return;
        File file = new File(Utils.sImagePath);

        if (!file.exists()) {
            Utils.showToast(this, "No image to show");
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri contentUri = FileProvider.getUriForFile(this, DemoAppConstants.AUTHORITY_FILE_PROVIDER, file);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(contentUri, "image/*");
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_exit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.demo_exit) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
