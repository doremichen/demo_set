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

package com.adam.app.demoset.alarm.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.R;

import java.util.Arrays;

/**
 * ViewModel for Alarm module.
 * Information Expert for Alarm Business Logic and Validation.
 */
public class AlarmViewModel extends AndroidViewModel {

    /**
     * Interface to abstract away system-level execution from business logic.
     * The implementation (usually Activity) handles actual AlarmManager calls.
     */
    public interface AlarmScheduler {
        void scheduleRepeating(long triggerTime, long interval);
        void scheduleInexactRepeating(long triggerTime, long interval);
        void scheduleAllowWhileIdle(long triggerTime);
        void scheduleExactAllowWhileIdle(long triggerTime);
        void onValidationError(String message);
    }

    /**
     * Enum Strategy Pattern to encapsulate Alarm strategies and their validation rules.
     * This avoids complex if-else chains and improves maintainability.
     */
    private enum Strategy {
        REPEAT(R.id.Repeat) {
            @Override
            boolean validate(long offset, AlarmScheduler scheduler) {
                if (offset < 60000) {
                    scheduler.onValidationError("Repeating alarm requires at least 60s");
                    return false;
                }
                return true;
            }

            @Override
            void execute(long triggerTime, long offset, AlarmScheduler scheduler) {
                scheduler.scheduleRepeating(triggerTime, offset);
            }
        },
        INEXACT_REPEAT(R.id.inexactRepeat) {
            @Override
            boolean validate(long offset, AlarmScheduler scheduler) {
                if (offset < 900000) { // 15 mins
                    scheduler.onValidationError("Inexact repeating requires at least 15 mins");
                    return false;
                }
                return true;
            }

            @Override
            void execute(long triggerTime, long offset, AlarmScheduler scheduler) {
                scheduler.scheduleInexactRepeating(triggerTime, offset);
            }
        },
        ALLOW_WHILE_IDLE(R.id.allWhileIdle) {
            @Override
            void execute(long triggerTime, long offset, AlarmScheduler scheduler) {
                scheduler.scheduleAllowWhileIdle(triggerTime);
            }
        },
        EXACT_ALLOW_WHILE_IDLE(R.id.exectAllowWhileIde) {
            @Override
            void execute(long triggerTime, long offset, AlarmScheduler scheduler) {
                scheduler.scheduleExactAllowWhileIdle(triggerTime);
            }
        };

        private final int id;

        Strategy(int id) {
            this.id = id;
        }

        /**
         * Validates if the given offset meets the business rules for this strategy.
         */
        boolean validate(long offset, AlarmScheduler scheduler) {
            return true; // Default no-op validation
        }

        /**
         * Delegates the scheduling call to the provided scheduler.
         */
        abstract void execute(long triggerTime, long offset, AlarmScheduler scheduler);

        /**
         * Finds the strategy matching the provided resource ID.
         */
        static Strategy fromId(int id) {
            return Arrays.stream(values())
                    .filter(s -> s.id == id)
                    .findFirst()
                    .orElse(null);
        }
    }

    private final MutableLiveData<Integer> mAlarmCount = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> mIsRunning = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> mSelectedStrategyId = new MutableLiveData<>(R.id.Repeat);
    private final MutableLiveData<String> mInputDelay = new MutableLiveData<>("5");

    private final Context mContext;

    public AlarmViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
    }

    public LiveData<Integer> getAlarmCount() { return mAlarmCount; }
    public LiveData<Boolean> isRunning() { return mIsRunning; }
    public MutableLiveData<Integer> getSelectedStrategyId() { return mSelectedStrategyId; }
    public MutableLiveData<String> getInputDelay() { return mInputDelay; }

    /**
     * Business Rule Expert: Safely increments the alarm count.
     */
    public void incrementCount() {
        Integer current = mAlarmCount.getValue();
        mAlarmCount.postValue(current != null ? current + 1 : 1);
    }

    /**
     * UI State Expert: Toggles between Running and Idle states.
     */
    public void toggleAlarmState() {
        Boolean current = mIsRunning.getValue();
        mIsRunning.setValue(current == null || !current);
    }

    public void setRunning(boolean running) {
        mIsRunning.setValue(running);
    }

    /**
     * Business Logic Expert: Decides how to schedule the alarm based on current state.
     * Uses the Strategy Enum to perform validation and delegate execution.
     *
     * @param currentTime Current system clock time.
     * @param scheduler The executor to perform system-level scheduling.
     */
    public void requestSchedule(long currentTime, AlarmScheduler scheduler) {
        // 1. Parse and basic validation of input
        String input = mInputDelay.getValue();
        long offsetMillis;
        try {
            offsetMillis = Long.parseLong(input != null ? input : "0") * 1000L;
            if (offsetMillis <= 0) {
                scheduler.onValidationError(mContext.getString(R.string.demo_alarm_offset_must_be_greater_than_0_toast));
                setRunning(false);
                return;
            }
        } catch (NumberFormatException e) {
            scheduler.onValidationError(mContext.getString(R.string.demo_alarm_invalid_input_number_toast));
            setRunning(false);
            return;
        }

        // 2. Identify the selected strategy
        Integer strategyId = mSelectedStrategyId.getValue();
        if (strategyId == null) return;

        Strategy strategy = Strategy.fromId(strategyId);
        if (strategy == null) {
            scheduler.onValidationError(mContext.getString(R.string.demo_alarm_unknown_strategy_selected_toast));
            setRunning(false);
            return;
        }

        // 3. Validate business rules and execute
        if (strategy.validate(offsetMillis, scheduler)) {
            long triggerTime = currentTime + offsetMillis;
            strategy.execute(triggerTime, offsetMillis, scheduler);
        } else {
            setRunning(false);
        }
    }
}
