/**
 * Copyright (C) Adam demo app Project
 * <p>
 * Description: This class is the local service.
 * <p>
 * Author: Adam Chen
 * Date: 2025/09/17
 */
package com.adam.app.demoset.demoService;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.adam.app.demoset.Utils;

public class LocalService extends Service {

    final private IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.showSnackBar(this, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.showSnackBar(this, "onStartCommand");
        Utils.info(this, "flags = " + flags);
        Utils.info(this, "intent = " + intent);
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.showSnackBar(this, "onDestroy");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Utils.showSnackBar(this, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Utils.showSnackBar(this, "onBind");
        return mBinder;
    }

    public void action1() {
        Utils.showToast(this, "action one from local service!!!");
    }

    class LocalBinder extends Binder {

        public LocalService getService() {
            return LocalService.this;
        }
    }
}
