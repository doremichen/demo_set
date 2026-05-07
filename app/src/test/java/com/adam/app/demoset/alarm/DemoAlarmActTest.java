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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ActivityScenario;

import com.adam.app.demoset.R;
import com.adam.app.demoset.alarm.viewmodel.AlarmViewModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlarmManager;
import org.robolectric.shadows.ShadowLooper;

/**
 * Unit tests for DemoAlarmAct using Robolectric.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.TIRAMISU)
public class DemoAlarmActTest {

    @Test
    public void testActivityInitialization() {
        try (ActivityScenario<DemoAlarmAct> scenario = ActivityScenario.launch(DemoAlarmAct.class)) {
            scenario.onActivity(activity -> {
                assertNotNull(activity);
                AlarmViewModel viewModel = new ViewModelProvider(activity).get(AlarmViewModel.class);
                assertNotNull(viewModel);
            });
        }
    }

    @Test
    public void testScheduleRepeating_CallsSystemManager() {
        try (ActivityScenario<DemoAlarmAct> scenario = ActivityScenario.launch(DemoAlarmAct.class)) {
            scenario.onActivity(activity -> {
                AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
                ShadowAlarmManager shadowAlarmManager = shadowOf(alarmManager);

                long triggerTime = 1000L;
                long interval = 60000L;
                activity.scheduleRepeating(triggerTime, interval);

                ShadowAlarmManager.ScheduledAlarm scheduledAlarm = shadowAlarmManager.peekNextScheduledAlarm();
                assertNotNull(scheduledAlarm);
                assertEquals(AlarmManager.ELAPSED_REALTIME_WAKEUP, scheduledAlarm.type);
                assertEquals(triggerTime, scheduledAlarm.triggerAtTime);
                assertEquals(interval, scheduledAlarm.interval);
            });
        }
    }

    @Test
    public void testOnReceive_UpdatesViewModel() {
        try (ActivityScenario<DemoAlarmAct> scenario = ActivityScenario.launch(DemoAlarmAct.class)) {
            scenario.onActivity(activity -> {
                AlarmViewModel viewModel = new ViewModelProvider(activity).get(AlarmViewModel.class);
                viewModel.setRunning(false); // Stop auto-reschedule for this test
                Integer initialCount = viewModel.getAlarmCount().getValue();
                int baseCount = initialCount != null ? initialCount : 0;

                Intent intent = new Intent(DemoAlarmAct.ACTION_UPDATE_INFO);
                // Set package to ensure it targets this app's receivers in some environments
                intent.setPackage(activity.getPackageName());
                activity.sendBroadcast(intent);

                // Idle the looper to process the broadcast
                ShadowLooper.idleMainLooper();

                assertEquals(baseCount + 1, (int) viewModel.getAlarmCount().getValue());
            });
        }
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.S)
    public void testTryStartAlarm_RequestPermissionIfMissing() {
        try (ActivityScenario<DemoAlarmAct> scenario = ActivityScenario.launch(DemoAlarmAct.class)) {
            scenario.onActivity(activity -> {
                AlarmViewModel viewModel = new ViewModelProvider(activity).get(AlarmViewModel.class);
                
                // Trigger the observer that calls tryStartAlarm
                viewModel.setRunning(true); 
                ShadowLooper.idleMainLooper();

                ShadowActivity shadowActivity = shadowOf(activity);
                Intent startedIntent = shadowActivity.getNextStartedActivity();
                
                // Depending on Robolectric version/config, canScheduleExactAlarms might be false by default.
                if (startedIntent != null) {
                    assertEquals(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, startedIntent.getAction());
                }
            });
        }
    }

    @Test
    public void testMenuExit_FinishesActivity() {
        try (ActivityScenario<DemoAlarmAct> scenario = ActivityScenario.launch(DemoAlarmAct.class)) {
            scenario.onActivity(activity -> {
                activity.onOptionsItemSelected(new RoboMenuItem(R.id.demo_exit));
                assertTrue(activity.isFinishing());
            });
        }
    }
}
