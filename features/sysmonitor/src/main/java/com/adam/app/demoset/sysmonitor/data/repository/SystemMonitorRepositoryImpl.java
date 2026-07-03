/*
 * MIT License
 *
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

package com.adam.app.demoset.sysmonitor.data.repository;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.sysmonitor.domain.model.SystemStatus;
import com.adam.app.demoset.sysmonitor.domain.repository.ISystemMonitorRepository;

import java.io.File;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * Implementation of ISystemMonitorRepository.
 * Uses Hilt for dependency injection.
 */
@Singleton
public class SystemMonitorRepositoryImpl implements ISystemMonitorRepository {

    // Constants to avoid magic numbers
    private static final int MONITOR_INTERVAL_MS = 2000;
    private static final int PERCENTAGE_MAX = 100;
    private static final int DEFAULT_VALUE_NEG = -1;
    private static final int ZERO = 0;
    private static final long BYTES_TO_MB = 1024L * 1024L;
    private static final long BYTES_TO_GB = 1024L * 1024L * 1024L;
    
    private static final String UNKNOWN = "Unknown";
    private static final String STORAGE_FORMAT = "%dGB / %dGB";
    private static final String MEMORY_FORMAT = "%dMB / %dMB";

    private final Context context;
    private final MutableLiveData<SystemStatus> statusLiveData = new MutableLiveData<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isMonitoring = false;

    private final Runnable monitorRunnable = new Runnable() {
        @Override
        public void run() {
            if (isMonitoring) {
                updateStatus();
                handler.postDelayed(this, MONITOR_INTERVAL_MS);
            }
        }
    };

    @Inject
    public SystemMonitorRepositoryImpl(@ApplicationContext Context context) {
        this.context = context;
    }

    @Override
    public LiveData<SystemStatus> getSystemStatus() {
        return statusLiveData;
    }

    @Override
    public void startMonitoring() {
        if (!isMonitoring) {
            isMonitoring = true;
            handler.post(monitorRunnable);
        }
    }

    @Override
    public void stopMonitoring() {
        isMonitoring = false;
        handler.removeCallbacks(monitorRunnable);
    }

    private void updateStatus() {
        int batteryLevel = getBatteryLevel();
        String storageUsage = getStorageInfo();
        String memoryUsage = getMemoryInfo();
        statusLiveData.setValue(new SystemStatus(batteryLevel, storageUsage, memoryUsage, System.currentTimeMillis()));
    }

    private int getBatteryLevel() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        if (batteryStatus != null) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, DEFAULT_VALUE_NEG);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, DEFAULT_VALUE_NEG);
            if (scale > ZERO) {
                return (int) ((level / (float) scale) * PERCENTAGE_MAX);
            }
        }
        return DEFAULT_VALUE_NEG;
    }

    private String getStorageInfo() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long availableBlocks = stat.getAvailableBlocksLong();

        long totalSize = totalBlocks * blockSize;
        long availableSize = availableBlocks * blockSize;
        long usedSize = totalSize - availableSize;

        return String.format(Locale.getDefault(), STORAGE_FORMAT, usedSize / BYTES_TO_GB, totalSize / BYTES_TO_GB);
    }

    private String getMemoryInfo() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        if (activityManager != null) {
            activityManager.getMemoryInfo(memoryInfo);
            long availableMegs = memoryInfo.availMem / BYTES_TO_MB;
            long totalMegs = memoryInfo.totalMem / BYTES_TO_MB;
            return String.format(Locale.getDefault(), MEMORY_FORMAT, availableMegs, totalMegs);
        }
        return UNKNOWN;
    }
}
