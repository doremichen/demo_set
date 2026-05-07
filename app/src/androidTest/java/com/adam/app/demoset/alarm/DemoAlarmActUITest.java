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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.adam.app.demoset.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Optimized UI tests for DemoAlarmAct.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class DemoAlarmActUITest {

    @Rule
    public ActivityScenarioRule<DemoAlarmAct> activityRule =
            new ActivityScenarioRule<>(DemoAlarmAct.class);

    @Test
    public void testUIInitialState() {
        onView(withId(R.id.title_alarm)).check(matches(isDisplayed()));
        onView(withId(R.id.input_delay_number)).check(matches(withText("5")));
    }

    @Test
    public void testStrategySelectionUI() {
        // Verify all radio buttons are present
        onView(withId(R.id.Repeat)).check(matches(isDisplayed()));
        onView(withId(R.id.inexactRepeat)).check(matches(isDisplayed()));
        onView(withId(R.id.allWhileIdle)).check(matches(isDisplayed()));
        
        // Interaction test
        onView(withId(R.id.inexactRepeat)).perform(click());
    }

    @Test
    public void testInvalidInputHandling() {
        // Type 0 (Invalid)
        onView(withId(R.id.input_delay_number))
                .perform(clearText(), typeText("0"));

        onView(withId(R.id.btn_alarm)).perform(click());

        // Should stay at "START" because validation fails
        onView(withId(R.id.btn_alarm)).check(matches(withText(R.string.action_start)));
        onView(withId(R.id.input_delay_number)).check(matches(isEnabled()));
    }

    @Test
    public void testInputFieldEnablement() {
        // Verify input is enabled initially
        onView(withId(R.id.input_delay_number)).check(matches(isEnabled()));
    }
}
