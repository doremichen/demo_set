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

package com.adam.app.demoset.jobService.service;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.adam.app.demoset.utils.ThreadHelper;
import com.adam.app.demoset.utils.Utils;

/**
 * Modern JobService for data transfer.
 */
public class ModernDataTransferJobService extends JobService {

    private static final String TAG = "ModernDTJobService";
    private static final String ACTION_UPDATE = "com.adam.app.demoset.action.TRANSFER_UPDATE";

    private static final int MAX_PROGRESS = 100;
    private static final int STEP_PROGRESS = 5;
    private static final long STEP_DELAY = 800;

    private ThreadHelper<String> mThreadHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.info(TAG, "onCreate");
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Utils.info(TAG, "onStartJob ID: " + params.getJobId());

        mThreadHelper = new ThreadHelper.Builder<String>()
                .setTask(() -> {
                    for (int i = 0; i <= MAX_PROGRESS; i += STEP_PROGRESS) {
                        Utils.info(TAG, "Modern Progress: " + i + "%");
                        Utils.sendTransferUpdate(this, i, "Modern Running...");
                        Thread.sleep(STEP_DELAY);
                    }
                    return "Modern Success";
                })
                .setCallback(new ThreadHelper.ThreadCallback<String>() {
                    @Override
                    public void onStarted() {
                    }

                    @Override
                    public void onSuccess(String result) {
                        Utils.info(TAG, "Modern Transfer Complete: " + result);
                        Utils.sendTransferUpdate(ModernDataTransferJobService.this, MAX_PROGRESS, result);
                        jobFinished(params, false);
                    }

                    @Override
                    public void onError(Exception e) {
                        jobFinished(params, true);
                    }

                    @Override
                    public void onCancelled() {
                        jobFinished(params, true);
                    }

                    @Override
                    public void onFinished() {
                    }
                })
                .build();

        mThreadHelper.start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Utils.info(TAG, "onStopJob reason: " + params.getStopReason());
        if (mThreadHelper != null) {
            mThreadHelper.stop();
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mThreadHelper != null) {
            mThreadHelper.shutDown();
        }
        Utils.info(TAG, "onDestroy");
    }
}
