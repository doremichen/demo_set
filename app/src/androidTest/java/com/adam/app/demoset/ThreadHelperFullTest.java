/**
 * This class is the thread helper unit test
 * Task success (onSuccess)
 * Cancel task (onCancelled)
 * Task error (onError)
 * Task finished (onFinished)
 * MultiTasks run
 * Builder reuse
 * isRunning() and stop() state check
 *
 * @author Adam Chen
 * @version 1.0
 * @since 2025-11-14
 */
package com.adam.app.demoset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.os.Handler;
import android.os.Looper;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
public class ThreadHelperFullTest {

    private Handler mHandler;

    @Before
    public void setUp() {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        mHandler = new Handler(Looper.myLooper());
    }

    // ---------------------------------------------
    // 1 Task success
    // ---------------------------------------------
    @Test
    public void testTaskSuccess() throws InterruptedException {
        AtomicBoolean started = new AtomicBoolean(false);
        AtomicReference<String> resultRef = new AtomicReference<>();
        AtomicBoolean finished = new AtomicBoolean(false);

        ThreadHelper<String> helper = new ThreadHelper.Builder<String>()
                .setTask(() -> "OK")
                .setCallback(new ThreadHelper.ThreadCallback<String>() {
                    @Override
                    public void onStarted() {
                        started.set(true);
                    }

                    @Override
                    public void onSuccess(String result) {
                        resultRef.set(result);
                    }

                    @Override
                    public void onError(Exception e) {
                        fail("should not enter to onError");
                    }

                    @Override
                    public void onCancelled() {
                        fail("should not enter to onCancelled");
                    }

                    @Override
                    public void onFinished() {
                        finished.set(true);
                    }
                })
                .build();

        helper.start();
        Thread.sleep(100);

        // check
        assertTrue(started.get());
        assertEquals("OK", resultRef.get());
        assertTrue(finished.get());
        assertFalse(helper.isRunning());
    }

    // ---------------------------------------------
    // 2 Cancel Task
    // ---------------------------------------------
    @Test
    public void testTaskCancelled() throws InterruptedException {
        AtomicBoolean cancelled = new AtomicBoolean(false);

        ThreadHelper<String> helper = new ThreadHelper.Builder<String>()
                .setTask(() -> {
                    while (!Thread.currentThread().isInterrupted()) {
                        Thread.sleep(50);
                    }
                    throw new InterruptedException();
                })
                .setCallback(new ThreadHelper.ThreadCallback<String>() {
                    @Override
                    public void onStarted() {
                    }

                    @Override
                    public void onSuccess(String result) {
                        fail("should not succeed!!!");
                    }

                    @Override
                    public void onError(Exception e) {
                        fail("should not error!!!");
                    }

                    @Override
                    public void onCancelled() {
                        cancelled.set(true);
                    }

                    @Override
                    public void onFinished() {
                    }
                })
                .build();

        helper.start();
        Thread.sleep(100); // wait thread to start
        // check
        assertTrue(helper.isRunning());

        helper.stop();
        Thread.sleep(100); // wait thread to stop

        // check
        assertTrue(cancelled.get());
        assertFalse(helper.isRunning());
    }

    // ---------------------------------------------
    // 3 Task error
    // ---------------------------------------------
    @Test
    public void testTaskError() throws InterruptedException {
        AtomicBoolean errorCalled = new AtomicBoolean(false);

        ThreadHelper<String> helper = new ThreadHelper.Builder<String>()
                .setTask(() -> {
                    throw new RuntimeException("test error");
                })
                .setCallback(new ThreadHelper.ThreadCallback<String>() {
                    @Override
                    public void onStarted() {
                    }

                    @Override
                    public void onSuccess(String result) {
                        fail("Should not succeed!!!");
                    }

                    @Override
                    public void onError(Exception e) {
                        errorCalled.set(true);
                        // check
                        assertEquals("test error", e.getMessage());
                    }

                    @Override
                    public void onCancelled() {
                        fail("should not cancelled");
                    }

                    @Override
                    public void onFinished() {
                    }
                })
                .build();

        helper.start();
        Thread.sleep(100); // wait thread to start
        // check
        assertTrue(errorCalled.get());
        assertFalse(helper.isRunning());
    }

    // ---------------------------------------------
    // 4 Multiple Tasks run at the same time
    // ---------------------------------------------
    @Test
    public void testMultipleTasks() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);

        ThreadHelper<Void> helper1 = new ThreadHelper.Builder<Void>()
                .setTask(() -> {
                    Thread.sleep(50);
                    counter.incrementAndGet();
                    return null;
                })
                .setCallback(new ThreadHelper.ThreadCallback<Void>() {
                    @Override
                    public void onStarted() {
                    }

                    @Override
                    public void onSuccess(Void result) {
                    }

                    @Override
                    public void onError(Exception e) {
                        fail();
                    }

                    @Override
                    public void onCancelled() {
                        fail();
                    }

                    @Override
                    public void onFinished() {
                    }
                }).build();

        ThreadHelper<Void> helper2 = new ThreadHelper.Builder<Void>()
                .setTask(() -> {
                    Thread.sleep(70);
                    counter.incrementAndGet();
                    return null;
                })
                .setCallback(new ThreadHelper.ThreadCallback<Void>() {
                    @Override
                    public void onStarted() {
                    }

                    @Override
                    public void onSuccess(Void result) {
                    }

                    @Override
                    public void onError(Exception e) {
                        fail();
                    }

                    @Override
                    public void onCancelled() {
                        fail();
                    }

                    @Override
                    public void onFinished() {
                    }
                }).build();

        helper1.start();
        helper2.start();

        Thread.sleep(200); // wait thread to start
        // check
        assertEquals(2, counter.get());
        assertFalse(helper1.isRunning());
        assertFalse(helper2.isRunning());
    }

    // ---------------------------------------------
    // 5 Builder reusable
    // ---------------------------------------------
    @Test
    public void testBuilderReusable() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);

        ThreadHelper.Builder<Void> builder = new ThreadHelper.Builder<>();
        builder.setTask(() -> {
                    counter.incrementAndGet();
                    return null;
                })
                .setCallback(new ThreadHelper.ThreadCallback<Void>() {
                    @Override
                    public void onStarted() {
                    }

                    @Override
                    public void onSuccess(Void result) {
                    }

                    @Override
                    public void onError(Exception e) {
                        fail();
                    }

                    @Override
                    public void onCancelled() {
                        fail();
                    }

                    @Override
                    public void onFinished() {
                    }
                });

        ThreadHelper<Void> helperA = builder.build();
        ThreadHelper<Void> helperB = builder.build();

        helperA.start();
        helperB.start();

        Thread.sleep(200); // wait thread to start
        // check
        assertEquals(2, counter.get());
        assertFalse(helperA.isRunning());
        assertFalse(helperB.isRunning());
    }

    // ---------------------------------------------
    // 6 stop() and isRunning() status check
    // ---------------------------------------------
    @Test
    public void testStopAndIsRunning() throws InterruptedException {
        ThreadHelper<Void> helper = new ThreadHelper.Builder<Void>()
                .setTask(() -> {
                    for (int i = 0; i < 10; i++) {
                        if (Thread.currentThread().isInterrupted())
                            throw new InterruptedException();
                        Thread.sleep(50);
                    }
                    return null;
                })
                .setCallback(new ThreadHelper.ThreadCallback<Void>() {
                    @Override
                    public void onStarted() {
                    }

                    @Override
                    public void onSuccess(Void result) {
                    }

                    @Override
                    public void onError(Exception e) {
                    }

                    @Override
                    public void onCancelled() {
                    }

                    @Override
                    public void onFinished() {
                    }
                }).build();

        helper.start();
        Thread.sleep(100);  // wait thread to start
        // check
        assertTrue(helper.isRunning());

        helper.stop();
        Thread.sleep(100);  // wait thread to stop
        // check
        assertFalse(helper.isRunning());
    }
}
