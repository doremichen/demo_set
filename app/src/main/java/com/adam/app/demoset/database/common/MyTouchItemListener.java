/*
 * Copyright (c) 2026 Adam Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.adam.app.demoset.database.common;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener;

import com.adam.app.demoset.utils.Utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MyTouchItemListener implements OnItemTouchListener {

    private final ScheduledExecutorService mService;
    private onItemClickListener mClickListener;
    private ScheduledFuture<?> mFuture;


    public MyTouchItemListener() {
        Utils.info(this, "Constructor enter");
        mService = Executors.newSingleThreadScheduledExecutor();
    }

    public void release() {
        Utils.info(this, "release enter");
        mService.shutdown();

        try {
            mService.awaitTermination(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void setonItemClickListener(onItemClickListener listener) {
        Utils.info(this, "setonItemClickListener enter");
        mClickListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
        Utils.info(this, "onInterceptTouchEvent enter");
        View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        Utils.info(this, "child = " + child);
        if (!Utils.areAllNotNull(child)) {
            return false;
        }

        int position = recyclerView.getChildAdapterPosition(child);

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Utils.info(this, "Start long click timer: position " + position);
                // Start long click timer
                mFuture = mService.schedule(new LongClickTask(position), 2L, TimeUnit.SECONDS);
                break;
            case MotionEvent.ACTION_UP:
                // Cancel long click timer
                Utils.info(this, "cancel long click timer");
                mFuture.cancel(true);
                break;
        }

        return true;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }

    /**
     * Tell UI
     */
    public interface onItemClickListener {
        void onLongClick(int position);
    }

    /**
     * Long click task
     */
    private class LongClickTask implements Runnable {

        private final int mPos;

        public LongClickTask(int position) {
            mPos = position;
        }

        @Override
        public void run() {
            Utils.info(this, "2 second has been arrived..");
            if (mClickListener != null) {
                mClickListener.onLongClick(mPos);
            }
        }
    }
}
