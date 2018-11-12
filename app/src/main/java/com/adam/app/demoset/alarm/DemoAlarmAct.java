package com.adam.app.demoset.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.adam.app.demoset.jnidemo.NativeUtils;

public class DemoAlarmAct extends AppCompatActivity {

    public static final String ACTION_UPDATE_INFO = "update alarm info";


    private boolean mNeedAlarm;

    private Button mbtnAlarm;
    private TextView mAlarmInfo;

    private AlarmManager mAlarmManager;


    private int mCount;


    private class UIReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.inFo(this, "UI onReceive");
            String action = intent.getAction();

            if (ACTION_UPDATE_INFO.equals(action)) {

                mCount++;
                // Update alarm info
                mAlarmInfo.setText("Alarm count: " + String.valueOf(mCount));

                MyJobService.actionNotification(getApplicationContext(), "Alarm count: " + String.valueOf(mCount));


            }
        }
    }

    private UIReceiver mUIRecv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_alarm);

        mbtnAlarm = this.findViewById(R.id.btn_alarm);
        mAlarmInfo = this.findViewById(R.id.tv_alarm_info);

        // Alarm service
        mAlarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);


        mUIRecv = new UIReceiver();
        IntentFilter filter = new IntentFilter(ACTION_UPDATE_INFO);
        this.registerReceiver(mUIRecv, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mUIRecv);

        stopAlarm();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_only_exit_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.demo_exit:
                // clear data
                NativeUtils.getInstance().clearObjData();
                NativeUtils.clearClazzData();

                this.finish();
                return true;
        }

        return false;
    }

    public void onAlarm(View v) {
        Utils.inFo(this, "onAlarm enter");

        if (!mNeedAlarm) {
            startAlarm();
            mNeedAlarm = true;
            mbtnAlarm.setText(this.getResources().getString(R.string.action_stop_alarm));
        } else {
            stopAlarm();
            mNeedAlarm = false;
            mbtnAlarm.setText(this.getResources().getString(R.string.action_start_alarm));
        }


    }

    private void startAlarm() {
        Utils.inFo(this, "startAlarm enter");
        Intent intent = new Intent(this, MyAlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        // Start alarm
        this.mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);

    }

    private void stopAlarm() {
        Utils.inFo(this, "stopAlarm enter");
        Intent intent = new Intent(this, MyAlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        // Cancel alarm
        this.mAlarmManager.cancel(alarmIntent);

    }


}
