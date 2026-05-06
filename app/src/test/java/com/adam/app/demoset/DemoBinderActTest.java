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

package com.adam.app.demoset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.os.Build;

import com.adam.app.demoset.binder.viewmodel.BinderViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class DemoBinderActTest {

    private BinderViewModel mViewModel;

    @Before
    public void setup() {
        mViewModel = new BinderViewModel();
    }

    @Test
    public void testViewModelInitialization() {
        assertNotNull(mViewModel.getOperationLogs());
        assertNotNull(mViewModel.getResultC());
    }

    @Test
    public void testResultUpdate() {
        mViewModel.result(42);
        assertEquals("42", mViewModel.getResultC().getValue());
    }

    @Test
    public void testAddLog() {
        List<String> logs = mViewModel.getOperationLogs().getValue();
        assertNotNull(logs);
        int initialSize = logs.size();
        mViewModel.showLog("Test Log");
        assertEquals(initialSize + 1, logs.size());
        assertEquals("Test Log", logs.get(initialSize));
    }
}
