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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import android.os.Build;

import com.adam.app.demoset.video.controller.VideoRecordManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

/**
 * Unit tests for VideoRecordManager.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class VideoRecordManagerTest {

    private VideoRecordManager manager;

    @Before
    public void setUp() throws Exception {
        manager = VideoRecordManager.getInstance();
        resetSingleton();
    }

    private void resetSingleton() throws Exception {
        Field deviceField = VideoRecordManager.class.getDeclaredField("mDevice");
        deviceField.setAccessible(true);
        deviceField.set(manager, null);

        Field recorderField = VideoRecordManager.class.getDeclaredField("mRecorder");
        recorderField.setAccessible(true);
        recorderField.set(manager, null);

        Field stateField = VideoRecordManager.class.getDeclaredField("mRecordState");
        stateField.setAccessible(true);
    }

    @Test
    public void testGetInstance() {
        assertNotNull(VideoRecordManager.getInstance());
        assertTrue(VideoRecordManager.getInstance() == VideoRecordManager.getInstance());
    }

    @Test
    public void testThreadManagement() {
        manager.startCameraThread();
        manager.stopCameraThread();
    }

    @Test
    public void testIsRecording_Default() {
        assertFalse(manager.isRecording());
    }

    @Test
    public void testRegisterListener() {
        VideoRecordManager.RecordListener mockListener = mock(VideoRecordManager.RecordListener.class);
        manager.registerListener(mockListener);
    }
}
