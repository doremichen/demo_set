/**
 * Copyright (C) Adam demo app Project. All rights reserved.
 * <p>
 * Description: This is the callback of that complete log list
 * </p>
 * Author: Adam Chen
 * Date: 2026/03/24
 */
package com.adam.app.demoset.material.util;

import androidx.recyclerview.widget.RecyclerView;

import com.adam.app.demoset.LogAdapter;

public class CompleteCallback implements Runnable {

    private final RecyclerView mRecyclerView;
    private final LogAdapter mAdapter;

    public CompleteCallback(RecyclerView recyclerView, LogAdapter adapter) {
        mRecyclerView = recyclerView;
        mAdapter = adapter;

    }

    @Override
    public void run() {
        // scroll to up
        mRecyclerView.scrollToPosition(0);
    }
}
