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

package com.adam.app.demoset.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Unit tests for ThreadHelper using Robolectric.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class ThreadHelperTest {

    private void await(CountDownLatch latch) throws InterruptedException {
        long timeout = 5000; // 5 seconds
        long start = System.currentTimeMillis();
        while (latch.getCount() > 0) {
            if (System.currentTimeMillis() - start > timeout) {
                fail("Timeout waiting for latch. Count=" + latch.getCount());
            }
            // Background thread might be running, allow it some time
            Thread.sleep(100);
            // Process tasks posted to the main looper
            ShadowLooper.idleMainLooper();
        }
        ShadowLooper.idleMainLooper();
    }

    @Test
    public void testTaskSuccess() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean started = new AtomicBoolean(false);
        AtomicReference<String> resultRef = new AtomicReference<>();
        AtomicBoolean finished = new AtomicBoolean(false);

        ThreadHelper<String> helper = new ThreadHelper.Builder<String>()
                .setTask(() -> "OK")
                .setCallback(new ThreadHelper.ThreadCallback<String>() {
                    @Override
                    public void onStarted() { started.set(true); }

                    @Override
                    public void onSuccess(String result) { resultRef.set(result); }

                    @Override
                    public void onError(Exception e) { fail("Should not fail"); }

                    @Override
                    public void onCancelled() { fail("Should not cancel"); }

                    @Override
                    public void onFinished() {
                        finished.set(true);
                        latch.countDown();
                    }
                })
                .build();

        helper.start();
        await(latch);

        assertTrue(started.get());
        assertEquals("OK", resultRef.get());
        assertTrue(finished.get());
        assertFalse(helper.isRunning());
        helper.shutDown();
    }

    @Test
    public void testTaskCancelled() throws InterruptedException {
        CountDownLatch startedLatch = new CountDownLatch(1);
        CountDownLatch cancelledLatch = new CountDownLatch(1);
        AtomicBoolean cancelled = new AtomicBoolean(false);

        ThreadHelper<String> helper = new ThreadHelper.Builder<String>()
                .setTask(() -> {
                    startedLatch.countDown();
                    while (true) {
                        Thread.sleep(100);
                    }
                })
                .setCallback(new ThreadHelper.ThreadCallback<String>() {
                    @Override public void onStarted() {}
                    @Override public void onSuccess(String result) { fail("Should not succeed"); }
                    @Override public void onError(Exception e) {}
                    @Override public void onCancelled() {
                        cancelled.set(true);
                        cancelledLatch.countDown();
                    }
                    @Override public void onFinished() {}
                })
                .build();

        helper.start();
        
        // Wait for it to reach the sleep loop
        assertTrue("Task should have started", startedLatch.await(3, TimeUnit.SECONDS));
        ShadowLooper.idleMainLooper();
        assertTrue("Helper should be running", helper.isRunning());

        helper.stop();
        await(cancelledLatch);

        assertTrue(cancelled.get());
        assertFalse(helper.isRunning());
        helper.shutDown();
    }

    @Test
    public void testTaskError() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean errorCalled = new AtomicBoolean(false);

        ThreadHelper<String> helper = new ThreadHelper.Builder<String>()
                .setTask(() -> { throw new RuntimeException("test error"); })
                .setCallback(new ThreadHelper.ThreadCallback<String>() {
                    @Override public void onStarted() {}
                    @Override public void onSuccess(String result) { fail("Should not succeed"); }
                    @Override public void onError(Exception e) {
                        errorCalled.set(true);
                        assertEquals("test error", e.getMessage());
                    }
                    @Override public void onCancelled() { fail("Should not cancel"); }
                    @Override public void onFinished() {
                        latch.countDown();
                    }
                })
                .build();

        helper.start();
        await(latch);

        assertTrue(errorCalled.get());
        assertFalse(helper.isRunning());
        helper.shutDown();
    }

    @Test
    public void testMultipleTasks() throws InterruptedException {
        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(0);

        ThreadHelper<Void> h1 = new ThreadHelper.Builder<Void>()
                .setTask(() -> { counter.incrementAndGet(); return null; })
                .setCallback(new ThreadHelper.ThreadCallback<Void>() {
                    @Override public void onStarted() {}
                    @Override public void onSuccess(Void result) {}
                    @Override public void onError(Exception e) { fail(); }
                    @Override public void onCancelled() { fail(); }
                    @Override public void onFinished() { latch1.countDown(); }
                }).build();

        ThreadHelper<Void> h2 = new ThreadHelper.Builder<Void>()
                .setTask(() -> { counter.incrementAndGet(); return null; })
                .setCallback(new ThreadHelper.ThreadCallback<Void>() {
                    @Override public void onStarted() {}
                    @Override public void onSuccess(Void result) {}
                    @Override public void onError(Exception e) { fail(); }
                    @Override public void onCancelled() { fail(); }
                    @Override public void onFinished() { latch2.countDown(); }
                }).build();

        h1.start();
        h2.start();

        await(latch1);
        await(latch2);

        assertEquals(2, counter.get());
        h1.shutDown();
        h2.shutDown();
    }
}
