package com.adam.app.demoset.myHandlerThread;

import android.os.Handler;
import android.os.HandlerThread;

import com.adam.app.demoset.Utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyHandlerThread extends HandlerThread {

    // Record handler observer
    List<HandlerObserver> mObvList = new CopyOnWriteArrayList<HandlerObserver>();

    // Used to cancel the task
    private AtomicBoolean mIsCancel = new AtomicBoolean(false);

    //
    // Work task
    //
    private class WorkTask implements Runnable {

        public static final long TIME = 1000L;
        private int mTaskId;

        @Override
        public void run() {

            WorkData data = WorkData.newInstance();

            while (!mIsCancel.get()) {
                mTaskId++;
                // Add one task time
                data.setCounter(mTaskId);

                // Notify observer
                mObvList.stream()
                        .forEach(observer -> observer.updateTaskInfo(data));

                // sleep 1 sec
                Utils.delay(TIME);
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

        if (!Utils.areAllNotNull(observer)) {
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
        if (!Utils.areAllNotNull(observer)) {
            Utils.info(this, "observer is null.......");
            return;
        }

        mObvList.remove(observer);
        Utils.info(this, "[unregisterObserver] exit");
    }
}
