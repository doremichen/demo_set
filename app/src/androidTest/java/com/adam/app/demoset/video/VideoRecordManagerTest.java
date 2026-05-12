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
import static org.junit.Assert.assertTrue;

import android.Manifest;
import android.graphics.SurfaceTexture;
import android.view.TextureView;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.adam.app.demoset.R;
import com.adam.app.demoset.video.controller.VideoRecordManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Advanced Instrumented tests for VideoRecordManager.
 */
@RunWith(AndroidJUnit4.class)
public class VideoRecordManagerTest {

    private static String sOriginalSleepTimeout;

    @Rule
    public ActivityScenarioRule<DemoVideoRecordAct> activityRule =
            new ActivityScenarioRule<>(DemoVideoRecordAct.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    );

    private VideoRecordManager manager;

    @BeforeClass
    public static void keepScreenAwake() {
        sOriginalSleepTimeout = executeShellCommand("settings get system screen_off_timeout").trim();
        executeShellCommand("settings put system screen_off_timeout 1800000");
    }

    @AfterClass
    public static void restoreScreenSettings() {
        if (sOriginalSleepTimeout != null && !sOriginalSleepTimeout.isEmpty()) {
            executeShellCommand("settings put system screen_off_timeout " + sOriginalSleepTimeout);
        }
    }

    private static String executeShellCommand(String command) {
        try {
            android.os.ParcelFileDescriptor pfd = InstrumentationRegistry.getInstrumentation()
                    .getUiAutomation().executeShellCommand(command);
            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(new android.os.ParcelFileDescriptor.AutoCloseInputStream(pfd)));
            return reader.readLine();
        } catch (Exception e) {
            return "";
        }
    }

    @Before
    public void setUp() {
        manager = VideoRecordManager.getInstance();
        activityRule.getScenario().onActivity(activity -> {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        });
    }

    @Test
    public void testOpenCamera_SuccessfulWhenSurfaceAvailable() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        activityRule.getScenario().onActivity(activity -> {
            TextureView textureView = activity.findViewById(R.id.surface_record);
            
            if (textureView.isAvailable()) {
                manager.openCamera(activity, textureView);
                latch.countDown();
            } else {
                textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                    @Override
                    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                        manager.openCamera(activity, textureView);
                        latch.countDown();
                    }
                    @Override public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {}
                    @Override public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) { return true; }
                    @Override public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {}
                });
            }
        });

        assertTrue("Camera open logic should be triggered", latch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void testRecordState_Consistency() {
        activityRule.getScenario().onActivity(activity -> {
            assertFalse(manager.isRecording());
            manager.closeCamera();
            assertFalse(manager.isRecording());
        });
    }

    @Test
    public void testStopRecord_WhenNotActive() {
        activityRule.getScenario().onActivity(activity -> {
            manager.stopRecord();
            assertFalse(manager.isRecording());
        });
    }
}
