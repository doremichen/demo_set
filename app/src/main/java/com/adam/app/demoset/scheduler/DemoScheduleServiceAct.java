package com.adam.app.demoset.scheduler;

import android.os.Bundle;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.SeekBar;
import android.widget.TextView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

public class DemoScheduleServiceAct extends AppCompatActivity {

    public static final String KEY_TIME = "key.time";
    private boolean mEnableCounter;

    private Button mCounterAction;
    private Chronometer mMeter;
    private SeekBar mSbPeriodic;
    private TextView mPeriodic;

    private SchedulerController mController;

    // Periodic time
    long mPeriodicTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.info(this, "onCreate enter");
        setContentView(R.layout.activity_demo_schedule_service);

        mCounterAction = findViewById(R.id.btn_action_counter);
        mMeter = findViewById(R.id.chronometer);

        mSbPeriodic = findViewById(R.id.seekBar_periodic);
        mPeriodic = findViewById(R.id.label_periodic_unit);

        mMeter.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                // format hh:mm:ss
                int h   = (int)(time /3600000);
                int m = (int)(time  - h*3600000)/60000;
                int s= (int)(time  - h*3600000 - m*60000)/1000 ;
                String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0"+m: m+"";
                String ss = s < 10 ? "0"+s: s+"";
                // update info
                chronometer.setText(TextUtils.concat(hh, ":", mm, ":", ss));
            }
        });

        mSbPeriodic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Utils.info(this, "onProgressChanged enter");
                mPeriodic.setText(progress > 0 ? progress + " s" : getString(R.string.label_time_unit));
                mPeriodicTime = progress > 0 ? progress : 0L;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mController = new SchedulerController();

        mController.registeronControllerListener(new SchedulerController.onControllerListener() {
            @Override
            public void TimeArrive(long millisecond) {
                Utils.info(this, "TimeArrive enter");
                Utils.info(this, "counter = " + millisecond);
                // show notification
                Utils.makeStatusNotification("Time is arrived", DemoScheduleServiceAct.this);

            }

            @Override
            public void finishUI() {
                Utils.info(this, "finishUI");
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.info(this, "onDestroy enter");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_exit, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.demo_exit) {
            mController.finishTask();
            return true;
        }
        return false;
    }

    public void onCounterAction(View v) {
        Utils.info(this, "onCounterAction enter");

        if (mPeriodicTime == 0L) {
            Utils.showToast(this, getString(R.string.label_show_non_zero_input_info));
            return;
        }

        mSbPeriodic.setEnabled(mEnableCounter);

        if (!mEnableCounter) {
            mController.startCount(mPeriodicTime);
            mMeter.setBase(SystemClock.elapsedRealtime());
            mMeter.start();
        } else {
            mController.stopCount();
            mMeter.stop();
        }

        // update button info
        mCounterAction.setText(!mEnableCounter ? R.string.action_stop_counter : R.string.action_start_counter);
        mEnableCounter = !mEnableCounter;
    }

}
