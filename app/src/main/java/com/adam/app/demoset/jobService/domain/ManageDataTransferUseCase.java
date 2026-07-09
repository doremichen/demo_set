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

package com.adam.app.demoset.jobService.domain;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.adam.app.demoset.R;
import com.adam.app.demoset.jobService.service.LegacyDataTransferService;
import com.adam.app.demoset.jobService.service.ModernDataTransferJobService;
import com.adam.app.demoset.utils.Utils;

import javax.inject.Inject;

/**
 * UseCase for managing background data transfer tasks.
 */
public class ManageDataTransferUseCase {

    private static final String TAG = "ManageDTUseCase";
    private static final int JOB_ID = 2026;
    private static final long ESTIMATED_BYTES = 100 * 1024 * 1024; // 100 MB

    private final Application mContext;

    @Inject
    public ManageDataTransferUseCase(Application application) {
        this.mContext = application;
    }

    /**
     * Starts the data transfer task.
     */
    public void executeStart(boolean isModern, boolean isUserInitiated, int networkType, boolean charging, boolean idle) {
        if (isModern) {
            startModernJob(isUserInitiated, networkType, charging, idle);
        } else {
            startLegacyService();
        }
    }

    /**
     * Stops any running tasks.
     */
    public void executeStop() {
        Utils.info(TAG, "Stopping all background tasks");
        mContext.stopService(new Intent(mContext, LegacyDataTransferService.class));
        JobScheduler scheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (scheduler != null) {
            scheduler.cancel(JOB_ID);
        }
    }

    private void startLegacyService() {
        Utils.info(TAG, "Starting Legacy Foreground Service");
        Intent intent = new Intent(mContext, LegacyDataTransferService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.startForegroundService(intent);
        } else {
            mContext.startService(intent);
        }
    }

    private void startModernJob(boolean isUserInitiated, int networkType, boolean charging, boolean idle) {
        Utils.info(TAG, "Scheduling Modern Job. User-Initiated: " + isUserInitiated);

        JobScheduler scheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (scheduler == null) return;

        ComponentName componentName = new ComponentName(mContext, ModernDataTransferJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName)
                .setRequiredNetworkType(networkType)
                .setRequiresCharging(charging);

        if (isUserInitiated && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            builder.setUserInitiated(true);
            builder.setRequiresDeviceIdle(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                builder.setEstimatedNetworkBytes(ESTIMATED_BYTES, ESTIMATED_BYTES);
            }
        } else {
            builder.setRequiresDeviceIdle(idle);
        }

        int result = scheduler.schedule(builder.build());
        if (result == JobScheduler.RESULT_SUCCESS) {
            Utils.info(TAG, "Job scheduled successfully (Mode: " + (isUserInitiated ? "UI" : "Deferred") + ")");
        } else {
            Utils.error(TAG, "Job scheduling failed");
            Utils.showToast(mContext, mContext.getString(R.string.bg_exec_msg_job_failed));
        }
    }
}
