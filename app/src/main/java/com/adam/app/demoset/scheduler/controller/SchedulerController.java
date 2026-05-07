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

package com.adam.app.demoset.scheduler;

import com.adam.app.demoset.utils.Utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SchedulerController {

    private ScheduledExecutorService mService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> mFuture;

    private onControllerListener mListener;

    private long mStartTime;


    private Runnable mScheduleTask;

    /**
     * Register callback
     *
     * @param listener
     */
    public void registerListener(onControllerListener listener) {
        Utils.info(this, "registeronControllerListener enter");
        mListener = listener;
    }

    /**
     * Start counter
     *
     * @param period
     */
    public void startScheduledTask(long period) {
        Utils.info(this, "startCount enter");

        // Check if task is running
        cancelScheduledTask();

        // Record the current time
        mStartTime = System.currentTimeMillis();

        // New task
        mScheduleTask = new MyScheduleTask();

        // Start schedule
        this.mFuture = this.mService.scheduleWithFixedDelay(this.mScheduleTask, period, period, TimeUnit.SECONDS);
        //mFuture = mService.scheduleAtFixedRate(mScheduleTask, period, period, TimeUnit.SECONDS);

    }

    /**
     * Stop counter
     */
    public void stopScheduledTask() {
        Utils.info(this, "stopCount enter");
        cancelScheduledTask();

    }

    /**
     * Release resource
     */
    public void finishScheduledTask() {
        Utils.info(this, "finishTask enter");
        cancelScheduledTask();

        mService.shutdown();

        try {
            /**
             * Blocks until all tasks have completed execution after a shutdown request,
             * or the timeout occurs, or the current thread is interrupted, whichever happens first.
             */
            if (!mService.awaitTermination(5L, TimeUnit.SECONDS)) {
                mService.shutdownNow();
                if (!mService.awaitTermination(5L, TimeUnit.SECONDS)) {
                    Utils.info(this, "Service did not terminate");
                }
            }
        } catch (InterruptedException e) {
            mService.shutdownNow();
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        Utils.info(this, "finishTask exit");
        if (mListener != null) {
            mListener.finishUI();
        }

    }

    /**
     * Cancel counter task
     */
    private void cancelScheduledTask() {
        Utils.info(this, "cancelTask enter");
        if (mFuture != null && !mFuture.isCancelled()) {
            mFuture.cancel(true);
        }
    }

    /**
     * For UI callback
     */
    public interface onControllerListener {
        void TimeArrive(long millisecond);

        void finishUI();
    }

    /**
     * Counter task
     */
    private class MyScheduleTask implements Runnable {
        @Override
        public void run() {
            Utils.info(this, "Schedule task is running....");
            // Tell UI
            if (mListener != null) {
                mListener.TimeArrive(System.currentTimeMillis() - mStartTime);
            }

        }
    }

}
