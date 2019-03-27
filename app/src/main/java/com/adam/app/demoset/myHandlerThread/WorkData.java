//
//  Data model
//
package com.adam.app.demoset.myHandlerThread;

public class WorkData {

    // Record count times of the work
    private int mCount;

    private WorkData() {}

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
        mCount = value;
    }

    //
    // Get counter value
    //
    public int getCounter() {
        return  mCount;
    }

}
