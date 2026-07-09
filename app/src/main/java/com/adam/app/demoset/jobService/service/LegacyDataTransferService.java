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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.adam.app.demoset.utils.ThreadHelper;
import com.adam.app.demoset.utils.Utils;

/**
 * Legacy Foreground Service for data transfer.
 */
public class LegacyDataTransferService extends Service {

    private static final String TAG = "LegacyDTService";
    private static final String CHANNEL_ID = "legacy_data_transfer";
    private static final int NOTIF_ID = 1001;

    private static final int MAX_PROGRESS = 100;
    private static final int STEP_PROGRESS = 10;
    private static final long STEP_DELAY = 1000;

    private ThreadHelper<String> mThreadHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.info(TAG, "onStartCommand");

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Legacy Data Transfer")
                .setContentText("Running background simulation...")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setOngoing(true)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIF_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(NOTIF_ID, notification);
        }

        startSimulation();
        return START_NOT_STICKY;
    }

    private void startSimulation() {
        if (mThreadHelper != null && mThreadHelper.isRunning()) {
            return;
        }

        mThreadHelper = new ThreadHelper.Builder<String>()
                .setTask(() -> {
                    for (int i = 0; i <= MAX_PROGRESS; i += STEP_PROGRESS) {
                        Utils.info(TAG, "Progress: " + i + "%");
                        Utils.sendTransferUpdate(this, i, "Legacy Running...");
                        Thread.sleep(STEP_DELAY);
                    }
                    return "Legacy Success";
                })
                .setCallback(new ThreadHelper.ThreadCallback<String>() {
                    @Override
                    public void onStarted() {
                    }

                    @Override
                    public void onSuccess(String result) {
                        Utils.info(TAG, "Transfer Complete: " + result);
                        Utils.sendTransferUpdate(LegacyDataTransferService.this, MAX_PROGRESS, result);
                    }

                    @Override
                    public void onError(Exception e) {
                        Utils.error(TAG, "Error: " + e.getMessage());
                    }

                    @Override
                    public void onCancelled() {
                        Utils.info(TAG, "Cancelled");
                    }

                    @Override
                    public void onFinished() {
                        stopForeground(true);
                        stopSelf();
                    }
                })
                .build();

        mThreadHelper.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mThreadHelper != null) {
            mThreadHelper.stop();
            mThreadHelper.shutDown();
        }
        Utils.info(TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Legacy Data Transfer",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
