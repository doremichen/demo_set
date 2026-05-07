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

package com.adam.app.demoset.jobService.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.R;
import com.adam.app.demoset.jobService.SecurJobService;
import com.adam.app.demoset.utils.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * ViewModel for DemoJobSvrAct.
 * Manages the state and logic for scheduling JobService with various constraints.
 */
public class DemoJobSvrViewModel extends AndroidViewModel {

    // TAG
    private static final String TAG = DemoJobSvrViewModel.class.getSimpleName();

    private final MutableLiveData<Boolean> mIsIdleRequired = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mIsChargingRequired = new MutableLiveData<>(false);
    private final MutableLiveData<NetworkOption> mNetworkOption = new MutableLiveData<>(NetworkOption.NONE);
    private final MutableLiveData<TriggerStrategy> mTriggerStrategy = new MutableLiveData<>(TriggerStrategy.PERIODIC);
    private final MutableLiveData<Integer> mTriggerInterval = new MutableLiveData<>(0);
    private final MutableLiveData<String> mIntervalText = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> mCanSetTrigger = new MutableLiveData<>(false);

    private int mJobId = 0;

    public DemoJobSvrViewModel(@NonNull Application application) {
        super(application);
        updateIntervalText(0);
    }

    public MutableLiveData<Boolean> getIsIdleRequired() {
        return mIsIdleRequired;
    }

    public MutableLiveData<Boolean> getIsChargingRequired() {
        return mIsChargingRequired;
    }

    public MutableLiveData<NetworkOption> getNetworkOption() {
        return mNetworkOption;
    }

    public MutableLiveData<TriggerStrategy> getTriggerStrategy() {
        return mTriggerStrategy;
    }

    public MutableLiveData<Integer> getTriggerInterval() {
        return mTriggerInterval;
    }

    public LiveData<String> getIntervalText() {
        return mIntervalText;
    }

    public void onIdleChanged(boolean checked) {
        mIsIdleRequired.setValue(checked);
    }

    public void onChargingChanged(boolean checked) {
        mIsChargingRequired.setValue(checked);
    }

    public void onNetworkTypeChanged(int checkedId) {
        mNetworkOption.setValue(NetworkOption.fromResId(checkedId));
    }

    public void onTriggerTypeSelected(int position) {
        mTriggerStrategy.setValue(TriggerStrategy.fromIndex(position));
        updateIntervalText(mTriggerInterval.getValue());
    }

    public void onIntervalChanged(int progress) {
        mTriggerInterval.setValue(progress);
        updateIntervalText(progress);
        mCanSetTrigger.setValue(progress > 0);
    }

    private void updateIntervalText(Integer progress) {
        if (progress == null || progress <= 0) {
            mIntervalText.setValue(getApplication().getString(R.string.label_interval_no_set));
            return;
        }

        TriggerStrategy strategy = mTriggerStrategy.getValue();
        if (strategy != null) {
            mIntervalText.setValue(progress + strategy.getUnitLabel());
        }
    }

    /**
     * Schedules a job based on current UI constraints.
     */
    public void scheduleJob() {
        Utils.info(this, "scheduleJob");
        if (!shouldSetJobRequirements()) {
            Utils.showToast(getApplication(), "No Jobinfo ConstraintSet");
            return;
        }

        JobInfo.Builder builder = new JobInfo.Builder(mJobId++,
                new ComponentName(getApplication().getPackageName(), SecurJobService.class.getName()));

        JobConfigApplier configApplier = new JobConfigApplier(builder);
        configApplier.applyNetwork(mNetworkOption.getValue())
                .applyDeviceIdle(mIsIdleRequired.getValue())
                .applyCharging(mIsChargingRequired.getValue())
                .applyTrigger(mCanSetTrigger.getValue(), mTriggerStrategy.getValue(), mTriggerInterval.getValue());

        JobScheduler jobScheduler = (JobScheduler) getApplication().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            jobScheduler.schedule(configApplier.build());
            Utils.info(this, "Job scheduled. ID: " + (mJobId - 1));
        }
    }

    /**
     * Cancels all scheduled jobs.
     */
    public void cancelAllJobs() {
        Utils.info(this, "scheduleJob");
        JobScheduler jobScheduler = (JobScheduler) getApplication().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            jobScheduler.cancelAll();
            Utils.info(this, "All jobs cancelled");
        }
    }

    private boolean shouldSetJobRequirements() {
        List<BooleanSupplier> conditions = Arrays.asList(
                () -> mNetworkOption.getValue() != null && mNetworkOption.getValue() != NetworkOption.NONE,
                () -> Boolean.TRUE.equals(mIsIdleRequired.getValue()),
                () -> Boolean.TRUE.equals(mIsChargingRequired.getValue()),
                () -> Boolean.TRUE.equals(mCanSetTrigger.getValue())
        );

        return conditions.stream().anyMatch(BooleanSupplier::getAsBoolean);
    }

    /**
     * Helper class to abstract the JobInfo configuration process.
     */
    private static class JobConfigApplier {
        private final JobInfo.Builder mBuilder;

        JobConfigApplier(JobInfo.Builder builder) {
            this.mBuilder = builder;
        }

        @SuppressLint("WrongConstant")
        JobConfigApplier applyNetwork(NetworkOption option) {
            if (option != null) {
                mBuilder.setRequiredNetworkType(option.getNetworkType());
            }
            return this;
        }

        JobConfigApplier applyDeviceIdle(Boolean idle) {
            if (idle != null) {
                mBuilder.setRequiresDeviceIdle(idle);
            }
            return this;
        }

        JobConfigApplier applyCharging(Boolean charging) {
            if (charging != null) {
                mBuilder.setRequiresCharging(charging);
            }
            return this;
        }

        JobConfigApplier applyTrigger(Boolean canSet, TriggerStrategy strategy, Integer progress) {
            if (Boolean.TRUE.equals(canSet) && strategy != null && progress != null) {
                long intervalMs = progress * 1000L;
                strategy.apply(mBuilder, intervalMs);
            }
            return this;
        }

        JobInfo build() {
            return mBuilder.build();
        }
    }

    /**
     * Enum for Network selection options.
     */
    public enum NetworkOption {
        NONE(R.id.no_network_opt, JobInfo.NETWORK_TYPE_NONE),
        ANY(R.id.any_network_opt, JobInfo.NETWORK_TYPE_ANY),
        UNMETERED(R.id.wifi_network_opt, JobInfo.NETWORK_TYPE_UNMETERED);

        private final int resId;
        private final int networkType;

        NetworkOption(int resId, int networkType) {
            this.resId = resId;
            this.networkType = networkType;
        }

        public int getNetworkType() {
            return networkType;
        }

        public static NetworkOption fromResId(int resId) {
            for (NetworkOption option : values()) {
                if (option.resId == resId) {
                    return option;
                }
            }
            return NONE;
        }
    }

    /**
     * Strategy ENUM for different trigger types.
     */
    public enum TriggerStrategy {
        PERIODIC {
            @Override
            void apply(JobInfo.Builder builder, long intervalMs) {
                builder.setPeriodic(intervalMs * 60);
            }
            @Override
            String getUnitLabel() { return " min"; }
        },
        OVERRIDE_DEADLINE {
            @Override
            void apply(JobInfo.Builder builder, long intervalMs) {
                builder.setOverrideDeadline(intervalMs);
            }
            @Override
            String getUnitLabel() { return " s"; }
        },
        MINIMUM_LATENCY {
            @Override
            void apply(JobInfo.Builder builder, long intervalMs) {
                builder.setMinimumLatency(intervalMs);
            }
            @Override
            String getUnitLabel() { return " s"; }
        };

        abstract void apply(JobInfo.Builder builder, long intervalMs);
        abstract String getUnitLabel();

        public static TriggerStrategy fromIndex(int index) {
            TriggerStrategy[] values = values();
            return (index >= 0 && index < values.length) ? values[index] : PERIODIC;
        }
    }
}
