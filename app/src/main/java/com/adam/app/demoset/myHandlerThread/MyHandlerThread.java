package com.adam.app.demoset.myHandlerThread;

import android.os.Handler;
import android.os.HandlerThread;

import com.adam.app.demoset.Utils;

import java.util.ArrayList;
import java.util.List;

public class MyHandlerThread extends HandlerThread {

    // Record handler observer
    List<HandlerObserver> mObvList = new ArrayList<HandlerObserver>();

    // Used to cancel the task
    private boolean isCancel;

    //
    // Work task
    //
    private class WorkTask implements Runnable {

        private int i;

        @Override
        public void run() {

            while (isCancel == false) {
                i++;
                // Add one task time
                WorkData.newInstance().setCounter(i);

                // Notify observer
                for (int i = 0; i < mObvList.size(); i++) {
                    mObvList.get(i).updateTaskInfo();
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

        mHandler = new Handler(getLooper());
    }


    //
    // Execute task
    //
    public void executeTask() {
        Utils.inFo(this, "[executeTask] enter");
        isCancel = false;
        mHandler.post(mTask);
        Utils.inFo(this, "[executeTask] exit");
    }

    //
    // Cancel task
    //
    public void cancelTask() {
        Utils.inFo(this, "[cancelTask] enter");
        mHandler.removeCallbacks(mTask);
        isCancel = true;
        Utils.inFo(this, "[cancelTask] exit");
    }


    //
    // Register handler thread observer
    //
    public void registerObserver(HandlerObserver observer) {
        Utils.inFo(this, "[registerObserver] enter");

        if (observer == null) {
            Utils.inFo(this, "observer is null.......");
            return;
        }

        mObvList.add(observer);

        Utils.inFo(this, "[registerObserver] exit");
    }

    //
    // Unregister handler thread observer
    //
    public void unregisterObserver(HandlerObserver observer) {
        Utils.inFo(this, "[unregisterObserver] enter");
        if (observer == null) {
            Utils.inFo(this, "observer is null.......");
            return;
        }

        mObvList.remove(observer);

        Utils.inFo(this, "[unregisterObserver] exit");
    }
}
