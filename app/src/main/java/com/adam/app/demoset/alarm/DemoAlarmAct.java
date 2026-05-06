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

package com.adam.app.demoset.alarm;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.alarm.viewmodel.AlarmViewModel;
import com.adam.app.demoset.databinding.ActivityDemoAlarmBinding;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;

/**
 * Activity following MVVM pattern for Alarm demo.
 * Acts as the 'Worker' for the ViewModel (Expert).
 */
public class DemoAlarmAct extends AppCompatActivity implements AlarmViewModel.AlarmScheduler {

    public static final String ACTION_UPDATE_INFO = "com.adam.app.demoset.alarm.ACTION_UPDATE_INFO";

    private AlarmViewModel mViewModel;
    private AlarmManager mAlarmManager;
    private PendingIntent mAlarmIntent;
    private UIReceiver mUIReceiver;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ActivityDemoAlarmBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_demo_alarm);
        mViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);
        binding.setVm(mViewModel);
        binding.setLifecycleOwner(this);

        UIUtils.applySystemBarInsets(binding.getRoot(), binding.titleAlarm);

        initAlarmSystem();
        observeViewModel();
    }

    private void observeViewModel() {
        mViewModel.isRunning().observe(this, running -> {
            if (Boolean.TRUE.equals(running)) {
                tryStartAlarm();
            } else {
                cancelSystemAlarm();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void initAlarmSystem() {
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmIntent = createPendingIntent();

        mUIReceiver = new UIReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPDATE_INFO);
        filter.addAction(AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED);
        registerReceiver(mUIReceiver, filter, RECEIVER_EXPORTED);
    }

    private PendingIntent createPendingIntent() {
        Intent intent = new Intent(this, MyAlarmReceiver.class);
        return PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private void tryStartAlarm() {
        // System Requirement Check (Activity expertise)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!mAlarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivity(intent);
                mViewModel.setRunning(false);
                return;
            }
        }

        // Pass control to the Expert (ViewModel)
        mViewModel.requestSchedule(SystemClock.elapsedRealtime(), this);
    }

    private void cancelSystemAlarm() {
        if (mAlarmManager != null && mAlarmIntent != null) {
            mAlarmManager.cancel(mAlarmIntent);
            Utils.info(this, "System: Alarm Canceled");
        }
    }

    // --- AlarmScheduler Implementation ---

    @Override
    public void scheduleRepeating(long triggerTime, long interval) {
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, interval, mAlarmIntent);
        Utils.info(this, "System Call: setRepeating");
    }

    @Override
    public void scheduleInexactRepeating(long triggerTime, long interval) {
        mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, interval, mAlarmIntent);
        Utils.info(this, "System Call: setInexactRepeating");
    }

    @Override
    public void scheduleAllowWhileIdle(long triggerTime) {
        mAlarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, mAlarmIntent);
        Utils.info(this, "System Call: setAndAllowWhileIdle");
    }

    @Override
    public void scheduleExactAllowWhileIdle(long triggerTime) {
        mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, mAlarmIntent);
        Utils.info(this, "System Call: setExactAndAllowWhileIdle");
    }

    @Override
    public void onValidationError(String message) {
        Utils.showToast(this, message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUIReceiver != null) unregisterReceiver(mUIReceiver);
        cancelSystemAlarm();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_exit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.demo_exit) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class UIReceiver extends BroadcastReceiver {
        @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_UPDATE_INFO.equals(intent.getAction())) {
                mViewModel.incrementCount();
                Boolean running = mViewModel.isRunning().getValue();
                if (Boolean.TRUE.equals(running)) {
                    mViewModel.requestSchedule(SystemClock.elapsedRealtime(), DemoAlarmAct.this);
                }
                Utils.makeStatusNotification(getApplicationContext(), "Alarm Triggered");
            }
        }
    }
}
