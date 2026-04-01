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
import android.os.Binder;
import android.os.IBinder;

import com.adam.app.demoset.demoService.util.ServiceLogBus;

public class LocalService extends Service {

    final private IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        // log
        ServiceLogBus.send(this, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Utils.showSnackBar(this, "onStartCommand");
//        Utils.info(this, "flags = " + flags);
//        Utils.info(this, "intent = " + intent);
        // log
        ServiceLogBus.send(this, "onStartCommand");
        ServiceLogBus.send(this, "flags = " + flags);
        ServiceLogBus.send(this, "intent = " + intent);

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        return mBinder;
    }

    public void action1() {
        // log
        ServiceLogBus.send(this, "action1 @ LocalService");
    }

    public class LocalBinder extends Binder {

        public LocalService getService() {
            return LocalService.this;
        }
    }
}
