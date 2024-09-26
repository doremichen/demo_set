//
//  Data model
//
package com.adam.app.demoset.myHandlerThread;

import java.util.concurrent.atomic.AtomicInteger;

public class WorkData {

    // Record count times of the work
    private AtomicInteger mCount = new AtomicInteger(0);

    private WorkData() {
    }

    private static class Helper {
        private static WorkData INSTANCE = new WorkData();
    }

    public static WorkData newInstance() {
        return Helper.INSTANCE;
    }

    //
    // Set counter value
    //
    public void setCounter(int value) {
        mCount.set(value);
    }

    //
    // Get counter value
    //
    public int getCounter() {
        return mCount.get();
    }

}
