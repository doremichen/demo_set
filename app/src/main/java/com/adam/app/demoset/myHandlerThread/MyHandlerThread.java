package com.adam.app.demoset.myHandlerThread;

import android.os.Handler;
import android.os.HandlerThread;

import com.adam.app.demoset.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyHandlerThread extends HandlerThread {

    // Record handler observer
    List<HandlerObserver> mObvList = new ArrayList<HandlerObserver>();

    // Used to cancel the task
    private AtomicBoolean mIsCancel = new AtomicBoolean(false);

    //
    // Work task
    //
    private class WorkTask implements Runnable {

        private int mId;

        @Override
        public void run() {
            while (mIsCancel.get() == false) {
                mId++;
                // Add one task time
                WorkData.newInstance().setCounter(mId);

                // Notify observer
                for (HandlerObserver observer: mObvList) {
                    observer.updateTaskInfo();
                }

                // sleep 1 sec
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // Work task handler
    private Handler mHandler;

    // Work task
    private WorkTask mTask = new WorkTask();


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
        mIsCancel.set(false);
        mHandler.post(mTask);
        Utils.info(this, "[executeTask] exit");
    }

    //
    // Cancel task
    //
    public void cancelTask() {
        Utils.info(this, "[cancelTask] enter");
        mHandler.removeCallbacks(mTask);
        mIsCancel.set(true);
        Utils.info(this, "[cancelTask] exit");
    }


    //
    // Register handler thread observer
    //
    public void registerObserver(HandlerObserver observer) {
        Utils.info(this, "[registerObserver] enter");

        if (observer == null) {
            Utils.info(this, "observer is null.......");
            return;
        }

        mObvList.add(observer);

        Utils.info(this, "[registerObserver] exit");
    }

    //
    // Unregister handler thread observer
    //
    public void unregisterObserver(HandlerObserver observer) {
        Utils.info(this, "[unregisterObserver] enter");
        if (observer == null) {
            Utils.info(this, "observer is null.......");
            return;
        }

        mObvList.remove(observer);

        Utils.info(this, "[unregisterObserver] exit");
    }
}
