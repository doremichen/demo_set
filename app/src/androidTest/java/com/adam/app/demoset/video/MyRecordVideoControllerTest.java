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

package com.adam.app.demoset.video;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import android.Manifest;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented tests for MyRecordVideoController.
 * This runs on a real device and tests the controller's basic state and singleton behavior.
 */
@RunWith(AndroidJUnit4.class)
public class MyRecordVideoControllerTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    );

    private MyRecordVideoController controller;

    @Before
    public void setUp() {
        controller = MyRecordVideoController.newInstance();
    }

    @Test
    public void testSingletonInstance() {
        MyRecordVideoController instance1 = MyRecordVideoController.newInstance();
        MyRecordVideoController instance2 = MyRecordVideoController.newInstance();
        assertNotNull(instance1);
        assertSame("Should return the same instance", instance1, instance2);
    }

    @Test
    public void testInitialRecordingState() {
        // Ensure the controller is not recording initially
        assertFalse("Initially should not be recording", controller.isRecording());
    }

    @Test
    public void testThreadManagementLifeCycle() {
        // Verify that starting and stopping the background thread doesn't throw exceptions
        controller.startCameraThread();
        controller.stopCameraThread();
    }

    @Test
    public void testRegisterListener() {
        // Simply verify we can register a listener without crash
        controller.registerListener(new MyRecordVideoController.ControllerListener() {
            @Override
            public void onError(int result) {}
            @Override
            public void onFail(String msg) {}
            @Override
            public void onInfo(String msg) {}
            @Override
            public String getPath() { return ""; }
        });
    }
}
