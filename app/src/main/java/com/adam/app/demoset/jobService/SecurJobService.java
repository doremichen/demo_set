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

import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.RequiresPermission;

import com.adam.app.demoset.utils.Utils;

public class SecurJobService extends JobService {


    private Handler mJobH = new Handler(Looper.getMainLooper()) {
        @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Utils.showToast(SecurJobService.this, "Job is running...");
            Utils.info(this, "Job is running...");
            Utils.makeStatusNotification(getApplicationContext(), "JobService is triggerd...");
            //jobFinished((JobParameters) msg.obj, false);
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        Utils.showToast(this, "onCreate");
        Utils.info(this, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.showToast(this, "onDestroy");
        Utils.info(this, "onDestroy");
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Utils.info(this, "onStartJob");
        Utils.showToast(this, "onStartJob");
        Message msg = Message.obtain(mJobH, 1, params);
        mJobH.sendMessage(msg);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Utils.info(this, "onSopJob");
        Utils.showToast(this, "onStopJob");
        mJobH.removeMessages(1);
        return false;
    }
}
