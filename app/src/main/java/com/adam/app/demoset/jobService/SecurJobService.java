package com.adam.app.demoset.jobService;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Handler;
import android.os.Message;

import com.adam.app.demoset.Utils;

/**
 * This is service that would enter to scheduler
 * <p>
 * info:
 *
 * @author: AdamChen
 * @date: 2018/9/26
 */
public class SecurJobService extends JobService {


    private Handler mJobH = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Utils.showToast(SecurJobService.this, "Job is running...");
            Utils.inFo(this, "Job is running...");
            Utils.makeStatusNotification("JobService is triggerd...", getApplicationContext());
            jobFinished((JobParameters) msg.obj, true);
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        Utils.showToast(this, "onCreate");
        Utils.inFo(this, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.showToast(this, "onDestroy");
        Utils.inFo(this, "onDestroy");
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Utils.inFo(this, "onStartJob");
        Utils.showToast(this, "onStartJob");
        Message msg = Message.obtain(mJobH, 1, params);
        mJobH.sendMessage(msg);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Utils.inFo(this, "onSopJob");
        Utils.showToast(this, "onStopJob");
        mJobH.removeMessages(1);
        return false;
    }
}
