/**
 * This is the remote service
 * <p>
 * info:
 *
 * @author: AdamChen
 * @date: 2018/9/19
 */

package com.adam.app.demoset;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

public class RemoteService extends Service {

    public static final int ACTION_ONE = 1;

    private class SvrHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case ACTION_ONE:
                    Utils.showToast(RemoteService.this, "action one....");
                    break;
            }
        }
    }

    private Messenger mMessaenger = new Messenger(new SvrHandler());

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
