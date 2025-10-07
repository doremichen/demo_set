package com.adam.app.demoset.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.AndroidRuntimeException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

import java.util.HashMap;
import java.util.Map;

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
    // strategy map
    Map<Integer, AlarmStrategy> mAlarmStategyMap = new HashMap<>() {
        {
            put(R.id.Repeat, new RepeatAlarmStrategy());
            put(R.id.inexactRepeat, new InexactRepeatAlarmStrategy());
            put(R.id.allWhileIdle, new AllowWhileIdleAlarmStrategy());
            put(R.id.exectAllowWhileIde, new ExactAllowWhileIdleAlarmStrategy());
        }
    };
    private Button mAlarmButton;
    private TextView mAlarmInfo;
    private AlarmManager mAlarmManager;
    private PendingIntent mAlarmIntent;
    private int mCount;
    private RadioGroup mRadioGroup;
    private UIReceiver mUIRecv;
    private long mOffset;
    private AlarmAction mAlarmAction;
    private EditText mInputDelayNumber;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_alarm);


        this.mInputDelayNumber = findViewById(R.id.input_delay_number);
        mAlarmButton = findViewById(R.id.btn_alarm);
        mAlarmInfo = findViewById(R.id.tv_alarm_info);
        mRadioGroup = findViewById(R.id.radioGroup);


        // Alarm action
        this.mAlarmAction = new AlarmAction();

        // Alarm service
        mAlarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        mAlarmIntent = getPendingIntent();

        mUIRecv = new UIReceiver();
        IntentFilter filter = new IntentFilter(ACTION_UPDATE_INFO);
        filter.addAction(AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED);
        this.registerReceiver(mUIRecv, filter, RECEIVER_EXPORTED);

        // set button click listener
        mAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAlarm(v);
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mUIRecv);

        stopAlarm();
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

    private void onAlarm(View v) {
        Utils.info(this, "onAlarm enter");
        try {
            this.mOffset = Long.parseLong(this.mInputDelayNumber.getText().toString()) * 1000L;
            Utils.info(this, "mOffset: " + String.valueOf(mOffset));
            if (mOffset == 0L) {
                Utils.showToast(this, getString(R.string.demo_alarm_input_offset_zero));
                return;
            }
            this.mAlarmAction.toggle();
        } catch (NumberFormatException e) {
            Utils.showToast(this, getString(R.string.demo_alarm_input_offset_invalid));
        }

    }

    private void startAlarm() {
        Utils.info(this, "startAlarm enter");

        if (mOffset == 0L) {
            Utils.showToast(this, getString(R.string.demo_alarm_input_offset_zero));
            throw new ArithmeticException("the input value is invalid!!!");
        }

        // alarm permission check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!this.mAlarmManager.canScheduleExactAlarms()) {
                Utils.info(this, "start request scheduled exact alarm!!!");
                Intent request = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                request.setData(Uri.fromParts("package", getOpPackageName(), null));
                startActivity(request);
            } else {
                Utils.info(this, "can scheduled exact alarms!!!");
            }
        }

        int type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
        long triggerTime = SystemClock.elapsedRealtime() + mOffset;
        Utils.info(this, "interval: " + this.mOffset);
        int id = mRadioGroup.getCheckedRadioButtonId();
        AlarmStrategy strategy = this.mAlarmStategyMap.get(id);
        if (strategy == null) {
            Utils.showToast(this, getString(R.string.demo_alarm_no_action));
            throw new AndroidRuntimeException("No this alarm function!!!");
        }
        strategy.setAlarm(type, triggerTime);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, MyAlarmReceiver.class);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private void stopAlarm() {
        Utils.info(this, "stopAlarm enter");
        // Cancel alarm
        this.mAlarmManager.cancel(mAlarmIntent);

    }

    /**
     * Alarm strategy pattern
     */
    interface AlarmStrategy {
        void setAlarm(int type, long triggerTime);
    }

    private class UIReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.info(this, "UI onReceive");
            String action = intent.getAction();
            if (AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED.equals(action)) {
                Utils.info(this, "Got permission to use alarm!!!");
            } else if (ACTION_UPDATE_INFO.equals(action)) {
                mCount++;
                // Update alarm info
                runOnUiThread(() -> mAlarmInfo.setText(getString(R.string.alarm_count, mCount)));

                int type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
                long triggerTime = SystemClock.elapsedRealtime() + mOffset;

                int id = mRadioGroup.getCheckedRadioButtonId();
                AlarmStrategy strategy = mAlarmStategyMap.get(id);
                if (strategy == null) {
                    Utils.info(this, "NO this alarm strategy!!!");
                    return;
                }
                strategy.setAlarm(type, triggerTime);

                Utils.makeStatusNotification(getApplicationContext(), "Alarm count: " + mCount);

            }
        }
    }

    /**
     * Alarm action state pattern
     */
    class AlarmAction {

        private AlarmState mCurrentState;

        public AlarmAction() {
            this.mCurrentState = new AlarmOnState();
        }

        void toggle() {
            this.mCurrentState.toggle();
        }

        void setState(AlarmState state) {
            this.mCurrentState = state;
        }

        abstract class AlarmState {

            // implement by subclass
            abstract void toggle();
        }

        // On
        class AlarmOnState extends AlarmState {

            @Override
            void toggle() {
                Utils.info(this, "toggle@AlarmOnState");
                try {
                    // start alarm
                    DemoAlarmAct.this.startAlarm();

                    // update ui component
                    DemoAlarmAct.this.mAlarmButton.setText(DemoAlarmAct.this.getResources().getString(R.string.action_stop));
                    DemoAlarmAct.this.mInputDelayNumber.setEnabled(false);

                    AlarmAction.this.setState(new AlarmOffState());
                } catch (ArithmeticException | AndroidRuntimeException e) {
                    Utils.info(this, e.getMessage());
                }

            }
        }

        // Off
        class AlarmOffState extends AlarmState {

            @Override
            void toggle() {
                Utils.info(this, "toggle@AlarmOffState");
                // stop alarm
                DemoAlarmAct.this.stopAlarm();

                // update ui component
                DemoAlarmAct.this.mAlarmButton.setText(DemoAlarmAct.this.getResources().getString(R.string.action_start));
                DemoAlarmAct.this.mInputDelayNumber.setEnabled(true);
                DemoAlarmAct.this.mInputDelayNumber.getText().clear();


                AlarmAction.this.setState(new AlarmOnState());
            }
        }

    }

    class RepeatAlarmStrategy implements AlarmStrategy {
        @Override
        public void setAlarm(int type, long triggerTime) {
            Utils.info(this, "setAlarm@RepeatAlarmStrategy");
            if (mOffset < 60000) {
                Utils.showCustomizedToast(DemoAlarmAct.this, getString(R.string.demo_alarm_set_time_LT_60));
                throw new ArithmeticException("the input value is invalid!!!");
            }
            mAlarmManager.setRepeating(type, triggerTime, mOffset, mAlarmIntent);
        }
    }

    class InexactRepeatAlarmStrategy implements AlarmStrategy {

        @Override
        public void setAlarm(int type, long triggerTime) {
            Utils.info(this, "setAlarm@InexactRepeatAlarmStrategy");
            if (mOffset < AlarmManager.INTERVAL_FIFTEEN_MINUTES) {
                Utils.showCustomizedToast(DemoAlarmAct.this, getString(R.string.demo_alram_set_time_LT_15));
                throw new ArithmeticException("the input value is invalid!!!");
            }
            mAlarmManager.setInexactRepeating(type, triggerTime, mOffset, mAlarmIntent);
        }
    }

    class AllowWhileIdleAlarmStrategy implements AlarmStrategy {

        @Override
        public void setAlarm(int type, long triggerTime) {
            Utils.info(this, "setAlarm@AllowWhileIdleAlarmStrategy");
            mAlarmManager.setAndAllowWhileIdle(type, triggerTime, mAlarmIntent);
        }
    }

    class ExactAllowWhileIdleAlarmStrategy implements AlarmStrategy {

        @Override
        public void setAlarm(int type, long triggerTime) {
            Utils.info(this, "setAlarm@ExactAllowWhileIdleAlarmStrategy");
            mAlarmManager.setExactAndAllowWhileIdle(type, triggerTime, mAlarmIntent);
        }
    }

}
