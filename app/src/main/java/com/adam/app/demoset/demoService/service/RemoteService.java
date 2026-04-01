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

package com.adam.app.demoset.demoService.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import androidx.annotation.NonNull;

import com.adam.app.demoset.demoService.util.ServiceLogBus;

public class RemoteService extends Service {

    public static final int ACTION_ONE = 1;
    private final Messenger mMessaenger = new Messenger(new ServiceHandler());

    @Override
    public void onCreate() {
        super.onCreate();
        // log
        ServiceLogBus.send(this, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // log
        ServiceLogBus.send(this, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // log
        ServiceLogBus.send(this, "onDestroy");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // log
        ServiceLogBus.send(this, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // log
        ServiceLogBus.send(this, "onBind");
        return mMessaenger.getBinder();
    }

    private class ServiceHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == ACTION_ONE) {
                // log
                ServiceLogBus.send(RemoteService.this, "ACTION_ONE @ Remote Service");
            }
        }
    }
}
