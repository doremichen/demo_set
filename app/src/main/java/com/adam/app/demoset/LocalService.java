/**
 * This is the local service
 * <p>
 * info:
 *
 * @author: AdamChen
 * @date: 2018/9/19
 */

package com.adam.app.demoset;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class LocalService extends Service {

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

    final private IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {

        LocalService getService() {
            return LocalService.this;
        }
    }

    public void action1() {
        Utils.showToast(this, "action1");
    }
}
