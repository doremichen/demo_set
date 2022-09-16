package com.adam.app.demoset.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

public class DemoNotificationAct extends AppCompatActivity {


    private static final String NOTIFY_CHANNEL_ID = "test_notification_channel_id";
    public static final int NOTIFICATION_ID = 1;

    private NotificationManager mManager;
    private NotificationCompat.Builder mBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_notification);

        mBuilder = notificationBuilder();


    }

    private NotificationCompat.Builder notificationBuilder() {

        mManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create notification channel id
            NotificationChannel channel = new NotificationChannel(NOTIFY_CHANNEL_ID,
                    "my test notification",
                    NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription("This notification is from Demoset app");
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);

            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100L, 200L, 300L, 400L, 500L, 400L, 300L, 200L, 400L});

            channel.setSound(null, null);

            mManager.createNotificationChannel(channel);

        }

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
        builder.setAutoCancel(true);

//        builder.setOngoing(true);  // Not removable

        // Config result activity to notification
        PendingIntent resultPT = getPendingIntent();
        builder.setContentIntent(resultPT);

        return builder;
    }

    private PendingIntent getPendingIntent() {
        Intent resultIntent = new Intent(this, NotifyResultAct.class);
        // ensures that navigating backward from the Activity leads out of application to Home screen
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotifyResultAct.class);
        stackBuilder.addNextIntent(resultIntent);

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public void onNotification(View v) {

        mManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_exit, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.demo_exit:
                this.finish();
                return true;
        }

        return false;
    }

    public void updateNotify(View view) {
        Utils.info(this, "updateNotify");
        if (hasNotification()) return;

        Bitmap notifyImg = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        NotificationCompat.Builder builder = notificationBuilder();
        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(notifyImg).setBigContentTitle("Notify update"));

        // Update
        mManager.notify(NOTIFICATION_ID, builder.build());

    }

    public void cancelNotify(View view) {
        Utils.info(this, "cancelNotify enter");
        if (hasNotification()) return;

        // cancel notification
        mManager.cancel(NOTIFICATION_ID);

    }

    private boolean hasNotification() {
        boolean hasNotification = false;
        // Get active notification
        StatusBarNotification[] notifications = mManager.getActiveNotifications();
        // Check whether or not the notification exists
        for (StatusBarNotification notify : notifications) {
            if (notify.getId() == NOTIFICATION_ID) {
                hasNotification = true;
            }
        }

        if (hasNotification == false) {
            Utils.showToast(this, "No notification...");
            return true;
        }
        return false;
    }
}
