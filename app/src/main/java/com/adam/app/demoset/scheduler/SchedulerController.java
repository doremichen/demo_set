package com.adam.app.demoset.scheduler;

import com.adam.app.demoset.Utils;

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
            mService.awaitTermination(5L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
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
        if (mFuture != null && mFuture.isCancelled() == false) {
            mFuture.cancel(true);
        }
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

    /**
     * For UI callback
     */
    interface onControllerListener {
        void TimeArrive(long millisecond);

        void finishUI();
    }

}
