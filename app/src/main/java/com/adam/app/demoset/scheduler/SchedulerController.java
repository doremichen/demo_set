package com.adam.app.demoset.scheduler;

import com.adam.app.demoset.Utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SchedulerController {

    private ScheduledExecutorService mService;
    private ScheduledFuture<?> mFuture;

    private onControllerListener mListener;

    private long mStartTime;


    private Runnable mScheduleTask;

    private SchedulerController() {
        mService = Executors.newSingleThreadScheduledExecutor();
    }

    private static class Helper {
        public static final SchedulerController INSTANCE = new SchedulerController();
    }

    /**
     * SingleTone
     * @return
     */
    public static SchedulerController newInstance() {
        return Helper.INSTANCE;
    }

    /**
     * Register callback
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
        mService.shutdown();

        boolean isDone = false;

        do {

            try {
                isDone = mService.awaitTermination(3L, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                isDone = true;
            }

        } while (!isDone);
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
    }

}
