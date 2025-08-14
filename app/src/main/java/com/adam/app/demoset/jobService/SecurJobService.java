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


    private Handler mJobH = new Handler(getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Utils.showToast(SecurJobService.this, "Job is running...");
            Utils.info(this, "Job is running...");
            Utils.makeStatusNotification(getApplicationContext(), "JobService is triggerd...");
            jobFinished((JobParameters) msg.obj, false);
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        Utils.showToast(this, "onCreate");
        Utils.info(this, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.showToast(this, "onDestroy");
        Utils.info(this, "onDestroy");
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Utils.info(this, "onStartJob");
        Utils.showToast(this, "onStartJob");
        Message msg = Message.obtain(mJobH, 1, params);
        mJobH.sendMessage(msg);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Utils.info(this, "onSopJob");
        Utils.showToast(this, "onStopJob");
        mJobH.removeMessages(1);
        return false;
    }
}
