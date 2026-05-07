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

package com.adam.app.demoset.scheduler.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.adam.app.demoset.R;
import com.adam.app.demoset.scheduler.controller.SchedulerController;

/**
 * ViewModel for DemoScheduleServiceAct
 */
public class SchedulerViewModel extends ViewModel {

    private final SchedulerController mController;

    private final MutableLiveData<Boolean> mIsRunning = new MutableLiveData<>(false);
    private final MutableLiveData<Long> mPeriodicTime = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> mArriveTime = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mFinishEvent = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> mToastEvent = new MutableLiveData<>();

    public SchedulerViewModel() {
        mController = new SchedulerController();
        mController.registerListener(new SchedulerController.onControllerListener() {
            @Override
            public void TimeArrive(long millisecond) {
                mArriveTime.postValue(millisecond);
            }

            @Override
            public void finishUI() {
                mFinishEvent.postValue(true);
            }
        });
    }

    public LiveData<Boolean> getIsRunning() {
        return mIsRunning;
    }

    public LiveData<Long> getPeriodicTime() {
        return mPeriodicTime;
    }

    public LiveData<Long> getArriveTime() {
        return mArriveTime;
    }

    public LiveData<Boolean> getFinishEvent() {
        return mFinishEvent;
    }

    public LiveData<Integer> getToastEvent() {
        return mToastEvent;
    }

    public void setPeriodicTime(long time) {
        mPeriodicTime.setValue(time);
    }

    /**
     * Start or stop scheduled task
     */
    public void toggleCounter() {
        if (Boolean.TRUE.equals(mIsRunning.getValue())) {
            mController.stopScheduledTask();
            mIsRunning.setValue(false);
            return;
        }

        Long period = mPeriodicTime.getValue();
        if (period != null && period > 0) {
            mController.startScheduledTask(period);
            mIsRunning.setValue(true);
        } else {
            mToastEvent.setValue(R.string.label_show_non_zero_input_info);
        }
    }

    /**
     * Finish task and trigger UI finish
     */
    public void finishTask() {
        mController.finishScheduledTask();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mController.stopScheduledTask();
    }
}
