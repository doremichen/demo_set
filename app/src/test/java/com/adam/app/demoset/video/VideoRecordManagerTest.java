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
import static org.mockito.Mockito.mock;

import android.os.Build;

import com.adam.app.demoset.video.data.repository.VideoRepositoryImpl;
import com.adam.app.demoset.video.domain.repository.VideoRecordListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

/**
 * Unit tests for VideoRepositoryImpl.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class VideoRecordManagerTest {

    private VideoRepositoryImpl repository;

    @Before
    public void setUp() throws Exception {
        repository = new VideoRepositoryImpl();
        resetInternalState();
    }

    private void resetInternalState() throws Exception {
        Field deviceField = VideoRepositoryImpl.class.getDeclaredField("mDevice");
        deviceField.setAccessible(true);
        deviceField.set(repository, null);

        Field recorderField = VideoRepositoryImpl.class.getDeclaredField("mRecorder");
        recorderField.setAccessible(true);
        recorderField.set(repository, null);
    }

    @Test
    public void testInitialization() {
        assertNotNull(repository);
    }

    @Test
    public void testThreadManagement() {
        repository.startCameraThread();
        repository.stopCameraThread();
    }

    @Test
    public void testIsRecording_Default() {
        assertFalse(repository.isRecording());
    }

    @Test
    public void testRegisterListener() {
        VideoRecordListener mockListener = mock(VideoRecordListener.class);
        repository.registerListener(mockListener);
    }
}
