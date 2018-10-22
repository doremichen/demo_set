package com.adam.app.demoset.biner;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.adam.app.demoset.Utils;

public class MyMessengerService extends Service {

    public static final int ACTION_ADD = 0x1357;
    public static final int ACTION_REPLY_RESULT = 0X2468;

    private Handler mIncomingHancler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Utils.inFo(this, "service handler");
            int flag = msg.what;

            if (flag == ACTION_ADD) {
                // Get arg from UI
                int a = msg.arg1;
                int b = msg.arg2;
                // Service operation
                int c = a + b;

                try {
                    // Get UI messenger
                    Messenger uiMessenger = msg.replyTo;
                    // Get message
                    Message replymsg = Message.obtain();
                    replymsg.what = ACTION_REPLY_RESULT;
                    replymsg.arg1 = c;
                    // Reply to UI
                    uiMessenger.send(replymsg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    private Messenger mMessenger = new Messenger(mIncomingHancler);

    @Override
    public IBinder onBind(Intent intent) {
        Utils.inFo(this, "onBinder");
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.inFo(this, "onBinder");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.inFo(this, "onStartCommand");
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.inFo(this, "onDestroy");
    }
}
