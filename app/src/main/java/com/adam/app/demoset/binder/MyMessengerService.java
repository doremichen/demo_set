/**
 * Copyright (C) Adam demo app Project
 *
 * Description: This class is the messenger service of the demo binder.
 *
 * Author: Adam Chen
 * Date: 2019/12/17
 */
package com.adam.app.demoset.binder;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.adam.app.demoset.Utils;

public class MyMessengerService extends Service {

    public static final int ACTION_ADD = 0x1357;
    public static final int ACTION_REPLY_RESULT = 0X2468;

    private final Handler mIncomingHancler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Utils.info(this, "service handler");

            if (msg.what == ACTION_ADD) {
                int result = calculateResult(msg.arg1, msg.arg2);
                sendReply(msg.replyTo, result);
            }
        }

        private int calculateResult(int a, int b) {
            try {
                return Math.addExact(a, b);
            } catch (ArithmeticException e) {
                return -1;
            }
        }

        private void sendReply(Messenger uiMessenger, int result) {
            try {
                Message replyMsg = Message.obtain();
                replyMsg.what = ACTION_REPLY_RESULT;
                replyMsg.arg1 = result;
                uiMessenger.send(replyMsg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    };

    private Messenger mMessenger = new Messenger(mIncomingHancler);

    @Override
    public IBinder onBind(Intent intent) {
        Utils.info(this, "onBinder");
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.info(this, "onBinder");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.info(this, "onStartCommand");
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.info(this, "onDestroy");
    }
}
