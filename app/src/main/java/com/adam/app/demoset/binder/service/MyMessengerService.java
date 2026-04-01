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

package com.adam.app.demoset.binder.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.adam.app.demoset.utils.Utils;

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
