/**
 * Copyright (C) Adam demo app Project
 * <p>
 * Description: This class is the remote service that is used Messenger to communicate with client .
 * <p>
 * Author: Adam Chen
 * Date: 2025/09/17
 */
package com.adam.app.demoset.demoService;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import androidx.annotation.NonNull;

import com.adam.app.demoset.Utils;

public class RemoteService extends Service {

    public static final int ACTION_ONE = 1;

    private class ServiceHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == ACTION_ONE) {
                Utils.showToast(RemoteService.this, "action one from remote service!!!");
            }
        }
    }

    private final Messenger mMessaenger = new Messenger(new ServiceHandler());

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.showSnackBar(this, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.showSnackBar(this, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
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
        return mMessaenger.getBinder();
    }
}
