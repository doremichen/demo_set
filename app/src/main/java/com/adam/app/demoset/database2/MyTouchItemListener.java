package com.adam.app.demoset.database2;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener;
import android.view.MotionEvent;
import android.view.View;

import com.adam.app.demoset.Utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MyTouchItemListener implements OnItemTouchListener {

    private onItemClickListener mClickListener;

    private ScheduledExecutorService mService;
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
        if (child != null) {
            int position = recyclerView.getChildAdapterPosition(child);

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                Utils.info(this, "Start long click timer");
                // Start long click timer
                mFuture = mService.schedule(new LongClickTask(position), 2L, TimeUnit.SECONDS);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                // Cancel long click timer
                Utils.info(this, "cancel long click timer");
                mFuture.cancel(true);
            }
        }

        return false;
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

        private int mPos;

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
