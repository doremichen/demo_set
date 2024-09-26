package com.adam.app.demoset;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

public class SecurService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
        Utils.showToast(this, "onCreate");
        Utils.info(this, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.showToast(this, "onDestroy");
        Utils.info(this, "onDestroy");
        this.stopForeground(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.showToast(this, "onStartCommand");
        Utils.info(this, "onStartCommand");

        // Android 8.0 suggestion
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
        Notification notification = builder.build();

        this.startForeground(1, notification);


        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
