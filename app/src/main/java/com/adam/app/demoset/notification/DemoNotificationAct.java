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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

import java.util.Arrays;

public class DemoNotificationAct extends AppCompatActivity {


    public static final int NOTIFICATION_ID = 1;
    private static final String NOTIFY_CHANNEL_ID = "test_notification_channel_id";
    private NotificationManager mManager;
    private NotificationCompat.Builder mBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.info(this, "onCreate");
        setContentView(R.layout.activity_demo_notification);
        mManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = notificationBuilder();


    }

    /**
     * Create notification builder
     * @return notification builder
     */
    private NotificationCompat.Builder notificationBuilder() {
        Utils.info(this, "notificationBuilder");


        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create notification channel id
            NotificationChannel channel = new NotificationChannel(NOTIFY_CHANNEL_ID,
                    "my test notification",
                    NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription("This notification is from Demoset app");
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);

            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100L, 200L, 300L, 400L, 500L
                    , 400L, 300L, 200L, 400L});

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

    /**
     * Get pending intent
     * @return pending intent
     */
    private PendingIntent getPendingIntent() {
        Utils.info(this, "getPendingIntent");
        Intent resultIntent = new Intent(this, NotifyResultAct.class);
        // ensures that navigating backward from the Activity leads out of application to Home screen
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotifyResultAct.class);
        stackBuilder.addNextIntent(resultIntent);

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }


    public void onNotification(View v) {
        Utils.info(this, "onNotification!!!");
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

    /**
     * Updates an existing notification with new content.
     * If a notification is already active, it replaces the current notification
     * with an updated one that includes a new image and title.
     *
     * @param view the view that triggers the update (typically from a UI interaction).
     */
    public void updateNotify(View view) {
        Utils.info(this, "updateNotify");
        if (!hasNotification()) return;

        Bitmap notifyImg = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        NotificationCompat.Builder builder = notificationBuilder();
        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(notifyImg).setBigContentTitle("Notify update"));

        // Update
        mManager.notify(NOTIFICATION_ID, builder.build());

    }

    /**
     * Cancels an existing notification.
     * If a notification is currently active, this method will dismiss it.
     *
     * @param view the view that triggers the cancellation (typically from a UI interaction).
     */
    public void cancelNotify(View view) {
        Utils.info(this, "cancelNotify enter");
        if (!hasNotification()) return;

        // cancel notification
        mManager.cancel(NOTIFICATION_ID);

    }

    /**
     * Checks if a notification with the specified ID is currently active.
     * This method retrieves the active notifications from the notification manager and checks
     * if any of them matches the predefined NOTIFICATION_ID. If no matching notification is found,
     * a toast message is displayed to inform the user.
     *
     * @return true if the notification with NOTIFICATION_ID is active, false otherwise.
     */
    private boolean hasNotification() {
        boolean hasNotification = Arrays.stream(mManager.getActiveNotifications())
                .anyMatch(notify -> notify.getId() == NOTIFICATION_ID);
        if (!hasNotification) {
            Utils.showToast(this, "No notification...");
        }
        return hasNotification;
    }
}
