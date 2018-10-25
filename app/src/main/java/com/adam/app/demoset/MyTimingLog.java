package com.adam.app.demoset;

import android.os.SystemClock;

import java.util.ArrayList;

public class MyTimingLog {

    private String mLabel;

    ArrayList<Long> mSplits;

    ArrayList<String> mSplitLabels;

    public MyTimingLog(String label) {
        reset(label);
    }

    public void reset(String label) {
        mLabel = label;
        reset();
    }

    public void reset() {

        if (mSplits == null) {
            mSplits = new ArrayList<Long>();
            mSplitLabels = new ArrayList<String>();
        } else {
            mSplits.clear();
            mSplitLabels.clear();
        }
        addSplit(null);
    }

    public void addSplit(String splitLabel) {
        long now = SystemClock.elapsedRealtime();
        mSplits.add(now);
        mSplitLabels.add(splitLabel);
    }

    public void dumpToLog() {
        Utils.inFo(this, mLabel + ": begin");

        final long first = mSplits.get(0);
        long now = first;
        for (int i = 1; i < mSplits.size(); i++) {
            now = mSplits.get(i);
            final String splitLabel = mSplitLabels.get(i);
            final long prev = mSplits.get(i - 1);
            Utils.inFo(this, mLabel + ":      " + (now - prev) + " ms, " + splitLabel);
        }
        Utils.inFo(this, mLabel + ": end, " + (now - first) + " ms");
    }
}
