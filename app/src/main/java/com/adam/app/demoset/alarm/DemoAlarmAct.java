package com.adam.app.demoset.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

/**
 * Testing your app with Doze
 * Force the system into idle mode by running the following command: adb shell dumpsys deviceidle force-idle
 * When ready, exit idle mode by running the following command: adb shell dumpsys deviceidle unforce
 * Reactivate the device by performing the following command: adb shell dumpsys battery reset
 * <p>
 * Testing your app with App Standby
 * Force the app into App Standby mode by running the following commands:
 * $ adb shell dumpsys battery unplug
 * $ adb shell am set-inactive <packageName> true
 * Simulate waking your app using the following commands:
 * $ adb shell am set-inactive <packageName> false
 * $ adb shell am get-inactive <packageName>
 */
public class DemoAlarmAct extends AppCompatActivity {

    public static final String ACTION_UPDATE_INFO = "update alarm info";


    private boolean mNeedAlarm;

    private Button mbtnAlarm;
    private TextView mAlarmInfo;

    private TextView mTextOffsetTime;
    private SeekBar mSeekBarOffsetTime;

    private AlarmManager mAlarmManager;


    private int mCount;
    private RadioGroup mRadioGroup;


    private class UIReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.info(this, "UI onReceive");
            String action = intent.getAction();

            if (ACTION_UPDATE_INFO.equals(action)) {

                mCount++;
                // Update alarm info
                mAlarmInfo.setText("Alarm count: " + mCount);

                int type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
                long triggerTime = SystemClock.elapsedRealtime() + mOffset;

                switch (mRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.allWhileIdle:
                        Utils.info(this, "allWhileIdle");
                        Intent intent1 = new Intent(DemoAlarmAct.this, MyAlarmReceiver.class);
                        PendingIntent alarmIntent1 = PendingIntent.getBroadcast(DemoAlarmAct.this, 0, intent1, 0);
                        mAlarmManager.setAndAllowWhileIdle(type, triggerTime, alarmIntent1);
                        break;
                    case R.id.exectAllowWhileIde:
                        Utils.info(this, "exectAllowWhileIde");
                        Intent intent2 = new Intent(DemoAlarmAct.this, MyAlarmReceiver.class);
                        PendingIntent alarmIntent2 = PendingIntent.getBroadcast(DemoAlarmAct.this, 0, intent2, 0);
                        mAlarmManager.setExactAndAllowWhileIdle(type, triggerTime, alarmIntent2);
                        break;
                    default:
                        break;
                }

                Utils.makeStatusNotification("Alarm count: " + mCount, getApplicationContext());

            }
        }
    }

    private UIReceiver mUIRecv;

    private long mOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_alarm);

        mbtnAlarm = findViewById(R.id.btn_alarm);
        mAlarmInfo = findViewById(R.id.tv_alarm_info);
        mRadioGroup = findViewById(R.id.radioGroup);
        mTextOffsetTime = findViewById(R.id.offsetTimeUnit);
        mSeekBarOffsetTime = findViewById(R.id.seekBaroffSetTime);

        mSeekBarOffsetTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update offset time text view
                if (progress > 0) {
                    mTextOffsetTime.setText(progress * 15 + "min");
                } else {
                    mTextOffsetTime.setText(getString(R.string.label_time_unit));
                }

                mOffset = progress * 900000L;  //15 min

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


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
                this.finish();
                return true;
        }

        return false;
    }

    public void onAlarm(View v) {
        Utils.info(this, "onAlarm enter");
        if (mOffset == 0L) {
            Utils.showToast(this, "Please config offset time first");
            return;
        }


        if (!mNeedAlarm) {
            mSeekBarOffsetTime.setEnabled(false);
            startAlarm();
            mNeedAlarm = true;
            mbtnAlarm.setText(this.getResources().getString(R.string.action_stop_alarm));
        } else {
            stopAlarm();
            mNeedAlarm = false;
            mbtnAlarm.setText(this.getResources().getString(R.string.action_start_alarm));
            mSeekBarOffsetTime.setEnabled(true);
        }


    }

    private void startAlarm() {
        Utils.info(this, "startAlarm enter");
        Intent intent = new Intent(this, MyAlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        int type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
        long triggerTime = SystemClock.elapsedRealtime() + mOffset;
        switch (mRadioGroup.getCheckedRadioButtonId()) {
            case R.id.Repeat:
                Utils.info(this, "Repeat");
                this.mAlarmManager.setRepeating(type,
                        triggerTime, AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);
                break;
            case R.id.inexactRepeat:
                Utils.info(this, "inexactRepeat");
                this.mAlarmManager.setInexactRepeating(type,
                        triggerTime, AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);
                break;
            case R.id.allWhileIdle:
                Utils.info(this, "allWhileIdle");
                this.mAlarmManager.setAndAllowWhileIdle(type, triggerTime, alarmIntent);
                break;
            case R.id.exectAllowWhileIde:
                Utils.info(this, "exectAllowWhileIde");
                this.mAlarmManager.setExactAndAllowWhileIdle(type, triggerTime, alarmIntent);
                break;
        }


    }

    private void stopAlarm() {
        Utils.info(this, "stopAlarm enter");
        Intent intent = new Intent(this, MyAlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        // Cancel alarm
        this.mAlarmManager.cancel(alarmIntent);

    }


}
