package com.adam.app.demoset.alarm;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.adam.app.demoset.Utils;

public class MyJobService extends JobIntentService {

    private static final String ACTION_MAKE_NOTIFY = "com.adam.app.demoset.action.make.notify";
    private static final String EXTRA_MESSAGE = "key.message";
    private static final int JOB_ID = 0x1357;


    public static void actionNotification(Context ctx, String message) {
        Utils.inFo(MyJobService.class, "enqueneWork enter");
        Intent work = new Intent();
        work.putExtra(EXTRA_MESSAGE, message);
        work.setAction(ACTION_MAKE_NOTIFY);
        JobIntentService.enqueueWork(ctx, MyJobService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Utils.inFo(this, "onHandleWork");
        String action = intent.getAction();
        if (ACTION_MAKE_NOTIFY.equals(action)) {
            String message = intent.getStringExtra(EXTRA_MESSAGE);
            handleInfo(message);
        }

    }

    final Handler mMainHandler = new Handler();

    private void handleInfo(final String info) {
        Utils.inFo(this, "handlerInfo enter");
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                Utils.showToast(getApplicationContext(), info);
                Utils.makeStatusNotification(info, getApplicationContext());
            }
        });
    }
}
