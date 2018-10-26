package com.adam.app.demoset.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.adam.app.demoset.R;

public class DemoNotificationAct extends AppCompatActivity {


    private static final String NOTIFY_CHANNEL_ID = "test channel id";

    private NotificationManager mManager;
    private Notification mNotify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_notification);

        mNotify = buildNotification();

    }

    private Notification buildNotification() {

        mManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create Notification channel <Android 8.0>
        NotificationChannel channel = new NotificationChannel(NOTIFY_CHANNEL_ID,
                "my test notification",
                NotificationManager.IMPORTANCE_HIGH);

        channel.setDescription("This is notification demo");
        channel.enableLights(true);
        channel.setLightColor(Color.GREEN);

        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{100L, 200L, 300L, 400L, 500L, 400L, 300L, 200L, 400L});

        mManager.createNotificationChannel(channel);


        Bitmap largeIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_notification_test);

        // Create Notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFY_CHANNEL_ID);

        // Config notification
        builder.setSmallIcon(R.drawable.ic_notification_test);
        builder.setContentTitle("Demo Notification");
        builder.setContentInfo("Info: 1");
        builder.setContentText("This is Demo text");
        builder.setLargeIcon(largeIcon);
        builder.setWhen(System.currentTimeMillis());
        builder.setOngoing(true);  // Not removable

        // Config result activity to notification
        Intent resultIntent = new Intent(this, NotifyResultAct.class);
        // ensures that navigating backward from the Activity leads out of application to Home screen
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotifyResultAct.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPT = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPT);


        return builder.build();
    }


    public void onNotification(View v) {

        mManager.notify(1, mNotify);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_only_exit_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.demo_bt_exit:
                this.finish();
                return true;
        }

        return false;
    }

}
