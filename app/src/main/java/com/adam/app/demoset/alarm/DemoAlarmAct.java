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
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import androidx.core.content.ContextCompat;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoAlarmBinding;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Testing your app with Doze
 * Force the system into idle mode by running the following command: adb shell dumpsys deviceidle force-idle
 * When ready, exit idle mode by running the following command: adb shell dumpsys deviceidle unforce
 * Reactivate the device by performing the following command: adb shell dumpsys battery reset
 * <p>
 * Testing your app with App Standby
 * Force the app into App Standby mode by running the following commands:
 * $ adb shell dumpsys battery unplug
 * $ adb shell am set-inactive <packageName> true
 * Simulate waking your app using the following commands:
 * $ adb shell am set-inactive <packageName> false
 * $ adb shell am get-inactive <packageName>
 */
public class DemoAlarmAct extends AppCompatActivity {

    public static final String ACTION_UPDATE_INFO = "com.adam.app.demoset.alarm.ACTION_UPDATE_INFO";

    private ActivityDemoAlarmBinding mBinding;
    private AlarmManager mAlarmManager;
    private PendingIntent mAlarmIntent;
    private int mCount = 0;
    private UIReceiver mUIReceiver;
    private long mOffsetMillis;
    private AlarmActionContext mActionContext;
    private final Map<Integer, AlarmStrategy> mStrategyMap = new HashMap<>();

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDemoAlarmBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        UIUtils.applySystemBarInsets(mBinding.getRoot(), mBinding.titleAlarm);

        initStrategies();
        setupUI();
        initAlarmSystem();
    }

    private void initStrategies() {
        mStrategyMap.put(R.id.Repeat, new RepeatAlarmStrategy());
        mStrategyMap.put(R.id.inexactRepeat, new InexactRepeatAlarmStrategy());
        mStrategyMap.put(R.id.allWhileIdle, new AllowWhileIdleAlarmStrategy());
        mStrategyMap.put(R.id.exectAllowWhileIde, new ExactAllowWhileIdleAlarmStrategy());
    }

    private void setupUI() {
        mBinding.tvAlarmInfo.setText(getString(R.string.alarm_count, mCount));
        mBinding.btnAlarm.setOnClickListener(v -> onAlarmToggle());
        mActionContext = new AlarmActionContext();
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

    private void onAlarmToggle() {
        try {
            String input = mBinding.inputDelayNumber.getText().toString();
            mOffsetMillis = Long.parseLong(input) * 1000L;
            if (mOffsetMillis <= 0) {
                Utils.showToast(this, getString(R.string.demo_alarm_input_offset_zero));
                return;
            }
            mActionContext.toggle();
        } catch (NumberFormatException e) {
            Utils.showToast(this, getString(R.string.demo_alarm_input_offset_invalid));
        }
    }

    private void startAlarmTask() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!mAlarmManager.canScheduleExactAlarms()) {
                Utils.info(this, "Requesting SCHEDULE_EXACT_ALARM permission.");
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivity(intent);
                return;
            }
        }

        int checkedId = mBinding.radioGroup.getCheckedRadioButtonId();
        AlarmStrategy strategy = mStrategyMap.get(checkedId);
        if (strategy != null) {
            long triggerTime = SystemClock.elapsedRealtime() + mOffsetMillis;
            strategy.setAlarm(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime);
            Utils.info(this, "Alarm scheduled with strategy: " + strategy.getClass().getSimpleName());
        } else {
            Utils.showToast(this, getString(R.string.demo_alarm_no_action));
        }
    }

    private void stopAlarmTask() {
        if (mAlarmManager != null && mAlarmIntent != null) {
            Utils.info(this, "Stopping alarm.");
            mAlarmManager.cancel(mAlarmIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUIReceiver != null) {
            unregisterReceiver(mUIReceiver);
        }
        stopAlarmTask();
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

    /**
     * Receiver for UI updates and permission status changes.
     */
    private class UIReceiver extends BroadcastReceiver {
        @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_UPDATE_INFO.equals(action)) {
                mCount++;
                mBinding.tvAlarmInfo.setText(getString(R.string.alarm_count, mCount));
                
                // For non-repeating strategies, we might need to manually reschedule if that's the desired behavior.
                // In this demo, we reschedule to show continuous triggering.
                startAlarmTask();
                
                Utils.makeStatusNotification(getApplicationContext(), "Alarm Count: " + mCount);
            } else if (AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED.equals(action)) {
                Utils.info(DemoAlarmAct.this, "Exact alarm permission state changed.");
            }
        }
    }

    /**
     * Context for the Alarm Action State Pattern (Switching between Start/Stop UI states).
     */
    private class AlarmActionContext {
        private AlarmState currentState = new AlarmOffState();

        void toggle() {
            currentState.handle();
        }

        void setState(AlarmState state) {
            this.currentState = state;
        }

        interface AlarmState {
            void handle();
        }

        class AlarmOffState implements AlarmState {
            @Override
            public void handle() {
                startAlarmTask();
                mBinding.btnAlarm.setText(R.string.action_stop);
                mBinding.btnAlarm.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                mBinding.inputDelayNumber.setEnabled(false);
                setState(new AlarmOnState());
            }
        }

        class AlarmOnState implements AlarmState {
            @Override
            public void handle() {
                stopAlarmTask();
                mBinding.btnAlarm.setText(R.string.action_start);
                int primaryColor = ContextCompat.getColor(DemoAlarmAct.this, R.color.teal_700);
                mBinding.btnAlarm.setBackgroundTintList(ColorStateList.valueOf(primaryColor));
                mBinding.inputDelayNumber.setEnabled(true);
                setState(new AlarmOffState());
            }
        }
    }

    /**
     * Strategy interface for different AlarmManager scheduling methods.
     */
    interface AlarmStrategy {
        void setAlarm(int type, long triggerTime);
    }

    class RepeatAlarmStrategy implements AlarmStrategy {
        @Override
        public void setAlarm(int type, long triggerTime) {
            if (mOffsetMillis < 60000) {
                Utils.showCustomizedToast(DemoAlarmAct.this, getString(R.string.demo_alarm_set_time_LT_60));
                return;
            }
            mAlarmManager.setRepeating(type, triggerTime, mOffsetMillis, mAlarmIntent);
        }
    }

    class InexactRepeatAlarmStrategy implements AlarmStrategy {
        @Override
        public void setAlarm(int type, long triggerTime) {
            if (mOffsetMillis < AlarmManager.INTERVAL_FIFTEEN_MINUTES) {
                Utils.showCustomizedToast(DemoAlarmAct.this, getString(R.string.demo_alram_set_time_LT_15));
                return;
            }
            mAlarmManager.setInexactRepeating(type, triggerTime, mOffsetMillis, mAlarmIntent);
        }
    }

    class AllowWhileIdleAlarmStrategy implements AlarmStrategy {
        @Override
        public void setAlarm(int type, long triggerTime) {
            mAlarmManager.setAndAllowWhileIdle(type, triggerTime, mAlarmIntent);
        }
    }

    class ExactAllowWhileIdleAlarmStrategy implements AlarmStrategy {
        @Override
        public void setAlarm(int type, long triggerTime) {
            mAlarmManager.setExactAndAllowWhileIdle(type, triggerTime, mAlarmIntent);
        }
    }
}
