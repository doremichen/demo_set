/**
 * Copyright (C) 2021 Adam Chen
 * <p>
 * This class is the handler thread
 *
 * @author Adam Chen
 * @version 1.0
 * @since 2021-11-11
 */
package com.adam.app.demoset.myHandlerThread;

import android.os.Handler;
import android.os.HandlerThread;

import com.adam.app.demoset.Utils;

import java.util.concurrent.atomic.AtomicBoolean;

public class MyHandlerThread extends HandlerThread {

    // Record handler observer
    private HandlerObserver mObserver;
//    List<HandlerObserver> mObvList = new CopyOnWriteArrayList<HandlerObserver>();

    // Used to cancel the task
    private AtomicBoolean mIsCancel = new AtomicBoolean(false);
    // Work task handler
    private Handler mHandler;
    // Work task
    private WorkTask mTask = new WorkTask();

    // used to check if the thread is active
    private boolean mIsActive;

    public MyHandlerThread() {
        super("My handler thread");
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        // initial work thread handler
        mHandler = new Handler(getLooper());
    }

    //
    // Execute task
    //
    public void executeTask() {
        Utils.info(this, "[executeTask] enter");
        if (mIsActive) {
            statusCallback(true);
            return;
        }
        mIsCancel.set(false);
        mHandler.post(mTask);
        mIsActive = true;
        Utils.info(this, "[executeTask] exit");
    }

    //
    // Cancel task
    //
    public void cancelTask() {
        Utils.info(this, "[cancelTask] enter");
        if (!mIsActive) {
            statusCallback(false);
            return;
        }
        mHandler.removeCallbacks(mTask);
        mIsCancel.set(true);
        mIsActive = false;
        Utils.info(this, "[cancelTask] exit");
    }

    //
    // Register handler thread observer
    //
    public void registerObserver(HandlerObserver observer) {
        Utils.info(this, "[registerObserver] enter");

        if (!Utils.areAllNotNull(observer)) {
            Utils.info(this, "observer is null.......");
            return;
        }

        mObserver = observer;
//        mObvList.add(observer);
        Utils.info(this, "[registerObserver] exit");
    }

    //
    // Unregister handler thread observer
    //
    public void unregisterObserver(HandlerObserver observer) {
        Utils.info(this, "[unregisterObserver] enter");
        if (!Utils.areAllNotNull(observer)) {
            Utils.info(this, "observer is null.......");
            return;
        }

        mObserver = null;
//        mObvList.remove(observer);
        Utils.info(this, "[unregisterObserver] exit");
    }

    private void statusCallback(boolean isActive) {
        if (mObserver != null) {
            mObserver.updateTaskStatus(isActive);
        }
    }

    /**
     * Work task
     */
    private class WorkTask implements Runnable {

        public static final long TIME = 1000L;
        private int mCounter;

        @Override
        public void run() {

            if (mIsCancel.get()) {
                statusCallback(false);
                return;
            }

            WorkData data = WorkData.newInstance();

            // Add one task time
            data.setCounter(mCounter++);
            // Notify observer
            if (mObserver != null) {
                mObserver.updateTaskInfo(data);
            }

            mHandler.postDelayed(this, TIME);
            Utils.info(MyHandlerThread.this, "counter: " + data.getCounter());
        }
    }
}
