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

    public SchedulerController() {

    }


    /**
     * Register callback
     *
     * @param listener
     */
    public void registeronControllerListener(onControllerListener listener) {
        Utils.inFo(this, "registeronControllerListener enter");
        mListener = listener;
    }

    /**
     * Start counter
     */
    public void startCount() {
        Utils.inFo(this, "startCount enter");
        // Check if task is running
        cancelTask();

        // Record the current time
        mStartTime = System.currentTimeMillis();

        // New task
        mScheduleTask = new MyScheduleTask();

        // Start schedule
        mFuture = mService.scheduleAtFixedRate(mScheduleTask, 1L, 1L, TimeUnit.SECONDS);

    }

    /**
     * Stop counter
     */
    public void stopCount() {
        Utils.inFo(this, "stopCount enter");
        cancelTask();

    }

    /**
     * Release resource
     */
    public void finishTask() {
        Utils.inFo(this, "finishTask enter");
        cancelTask();

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

        Utils.inFo(this, "finishTask exit");
        if (mListener != null) {
            mListener.finishUI();
        }

    }

    /**
     * Cancel counter task
     */
    private void cancelTask() {
        Utils.inFo(this, "cancelTask enter");
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
            Utils.inFo(this, "Schedule task is running....");
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
