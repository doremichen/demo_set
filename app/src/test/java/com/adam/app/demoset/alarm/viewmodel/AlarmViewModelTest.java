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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import android.app.Application;
import android.os.Build;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;

import com.adam.app.demoset.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Unit tests for AlarmViewModel using Robolectric and Mockito.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class AlarmViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private AlarmViewModel.AlarmScheduler mockScheduler;

    private AlarmViewModel viewModel;
    private Application context;
    private AutoCloseable mocks;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        context = ApplicationProvider.getApplicationContext();
        viewModel = new AlarmViewModel(context);
    }

    @After
    public void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
    }

    @Test
    public void testInitialValues() {
        assertEquals(Integer.valueOf(0), viewModel.getAlarmCount().getValue());
        assertEquals(Boolean.FALSE, viewModel.isRunning().getValue());
        assertEquals(Integer.valueOf(R.id.Repeat), viewModel.getSelectedStrategyId().getValue());
        assertEquals("5", viewModel.getInputDelay().getValue());
    }

    @Test
    public void testIncrementCount() {
        viewModel.incrementCount();
        assertEquals(Integer.valueOf(1), viewModel.getAlarmCount().getValue());
        viewModel.incrementCount();
        assertEquals(Integer.valueOf(2), viewModel.getAlarmCount().getValue());
    }

    @Test
    public void testToggleAlarmState() {
        // Initial state is false
        viewModel.toggleAlarmState();
        assertEquals(Boolean.TRUE, viewModel.isRunning().getValue());
        
        viewModel.toggleAlarmState();
        assertEquals(Boolean.FALSE, viewModel.isRunning().getValue());
    }

    @Test
    public void testSetRunning() {
        viewModel.setRunning(true);
        assertEquals(Boolean.TRUE, viewModel.isRunning().getValue());
        
        viewModel.setRunning(false);
        assertEquals(Boolean.FALSE, viewModel.isRunning().getValue());
    }

    @Test
    public void testRequestSchedule_InvalidInput() {
        viewModel.getInputDelay().setValue("not_a_number");
        viewModel.requestSchedule(1000L, mockScheduler);
        
        verify(mockScheduler).onValidationError(context.getString(R.string.demo_alarm_invalid_input_number_toast));
        assertEquals(Boolean.FALSE, viewModel.isRunning().getValue());
    }

    @Test
    public void testRequestSchedule_ZeroInput() {
        viewModel.getInputDelay().setValue("0");
        viewModel.requestSchedule(1000L, mockScheduler);
        
        verify(mockScheduler).onValidationError(context.getString(R.string.demo_alarm_offset_must_be_greater_than_0_toast));
        assertEquals(Boolean.FALSE, viewModel.isRunning().getValue());
    }

    @Test
    public void testRequestSchedule_NegativeInput() {
        viewModel.getInputDelay().setValue("-5");
        viewModel.requestSchedule(1000L, mockScheduler);
        
        verify(mockScheduler).onValidationError(context.getString(R.string.demo_alarm_offset_must_be_greater_than_0_toast));
        assertEquals(Boolean.FALSE, viewModel.isRunning().getValue());
    }

    @Test
    public void testRequestSchedule_Repeat_Valid() {
        viewModel.getInputDelay().setValue("60"); // 60 seconds
        viewModel.getSelectedStrategyId().setValue(R.id.Repeat);
        long currentTime = 1000000L;
        viewModel.requestSchedule(currentTime, mockScheduler);
        
        verify(mockScheduler).scheduleRepeating(currentTime + 60000L, 60000L);
    }

    @Test
    public void testRequestSchedule_Repeat_TooShort() {
        viewModel.getInputDelay().setValue("59"); // 59 seconds
        viewModel.getSelectedStrategyId().setValue(R.id.Repeat);
        viewModel.requestSchedule(1000L, mockScheduler);
        
        verify(mockScheduler).onValidationError("Repeating alarm requires at least 60s");
        verify(mockScheduler, never()).scheduleRepeating(anyLong(), anyLong());
        assertEquals(Boolean.FALSE, viewModel.isRunning().getValue());
    }

    @Test
    public void testRequestSchedule_InexactRepeat_Valid() {
        viewModel.getInputDelay().setValue("900"); // 15 mins
        viewModel.getSelectedStrategyId().setValue(R.id.inexactRepeat);
        long currentTime = 1000000L;
        viewModel.requestSchedule(currentTime, mockScheduler);
        
        verify(mockScheduler).scheduleInexactRepeating(currentTime + 900000L, 900000L);
    }

    @Test
    public void testRequestSchedule_InexactRepeat_TooShort() {
        viewModel.getInputDelay().setValue("899"); // Just under 15 mins
        viewModel.getSelectedStrategyId().setValue(R.id.inexactRepeat);
        viewModel.requestSchedule(1000L, mockScheduler);
        
        verify(mockScheduler).onValidationError("Inexact repeating requires at least 15 mins");
        verify(mockScheduler, never()).scheduleInexactRepeating(anyLong(), anyLong());
        assertEquals(Boolean.FALSE, viewModel.isRunning().getValue());
    }

    @Test
    public void testRequestSchedule_AllowWhileIdle() {
        viewModel.getInputDelay().setValue("10");
        viewModel.getSelectedStrategyId().setValue(R.id.allWhileIdle);
        long currentTime = 1000000L;
        viewModel.requestSchedule(currentTime, mockScheduler);
        
        verify(mockScheduler).scheduleAllowWhileIdle(currentTime + 10000L);
    }

    @Test
    public void testRequestSchedule_ExactAllowWhileIdle() {
        viewModel.getInputDelay().setValue("20");
        viewModel.getSelectedStrategyId().setValue(R.id.exectAllowWhileIde);
        long currentTime = 1000000L;
        viewModel.requestSchedule(currentTime, mockScheduler);
        
        verify(mockScheduler).scheduleExactAllowWhileIdle(currentTime + 20000L);
    }

    @Test
    public void testRequestSchedule_UnknownStrategy() {
        viewModel.getSelectedStrategyId().setValue(-1); // Unknown ID
        viewModel.requestSchedule(1000L, mockScheduler);
        
        verify(mockScheduler).onValidationError(context.getString(R.string.demo_alarm_unknown_strategy_selected_toast));
        assertEquals(Boolean.FALSE, viewModel.isRunning().getValue());
    }
}
