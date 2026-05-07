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

package com.adam.app.demoset.scheduler;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoScheduleServiceBinding;
import com.adam.app.demoset.scheduler.viewmodel.SchedulerViewModel;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Activity for Demo Schedule Service, demonstrating MVVM, Data Binding, and View Binding.
 */
public class DemoScheduleServiceAct extends AppCompatActivity {

    private ActivityDemoScheduleServiceBinding mBinding;
    private SchedulerViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.info(this, "onCreate enter");

        // Initialize Data Binding
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_demo_schedule_service);
        mViewModel = new ViewModelProvider(this).get(SchedulerViewModel.class);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(this);

        // Apply system bar insets
        UIUtils.applySystemBarInsets(mBinding.rootLayout, mBinding.headerLayout);

        setupChronometer();
        setupSeekBar();
        setupObservers();
    }

    /**
     * Set up Chronometer tick listener for custom formatting.
     */
    private void setupChronometer() {
        mBinding.chronometer.setOnChronometerTickListener(chronometer -> {
            long time = SystemClock.elapsedRealtime() - chronometer.getBase();

            long h = TimeUnit.MILLISECONDS.toHours(time);
            long m = TimeUnit.MILLISECONDS.toMinutes(time) % 60;
            long s = TimeUnit.MILLISECONDS.toSeconds(time) % 60;

            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s);
            chronometer.setText(formattedTime);
        });
    }

    /**
     * Set up SeekBar listener to update ViewModel.
     */
    private void setupSeekBar() {
        mBinding.seekBarPeriodic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mViewModel.setPeriodicTime(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    /**
     * Set up observers for ViewModel LiveData.
     */
    @SuppressLint("InlinedApi")
    private void setupObservers() {
        mViewModel.getIsRunning().observe(this, this::handleRunningState);
        mViewModel.getArriveTime().observe(this, this::handleTaskArrive);
        mViewModel.getToastEvent().observe(this, this::handleToastEvent);
        mViewModel.getFinishEvent().observe(this, this::handleFinishEvent);
    }

    private void handleRunningState(Boolean isRunning) {
        if (Boolean.TRUE.equals(isRunning)) {
            mBinding.chronometer.setBase(SystemClock.elapsedRealtime());
            mBinding.chronometer.start();
        } else {
            mBinding.chronometer.stop();
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void handleTaskArrive(Long millisecond) {
        Utils.info(this, "Task triggered: " + millisecond + " ms");
        if (hasNotificationPermission()) {
            Utils.makeStatusNotification(this, getString(R.string.demo_schedule_service_time_is_arrived_msg));
        } else {
            Utils.info(this, "Missing POST_NOTIFICATIONS permission");
        }
    }

    private void handleToastEvent(Integer resId) {
        if (resId != null) {
            Utils.showToast(this, getString(resId));
        }
    }

    private void handleFinishEvent(Boolean finish) {
        if (Boolean.TRUE.equals(finish)) {
            finish();
        }
    }

    private boolean hasNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_exit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.demo_exit) {
            mViewModel.finishTask();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
