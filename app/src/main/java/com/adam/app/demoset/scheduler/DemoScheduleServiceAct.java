package com.adam.app.demoset.scheduler;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
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
    private boolean mStartCounter;

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
        Utils.inFo(this, "onCreate enter");
        setContentView(R.layout.activity_demo_schedule_service);

        mCounterAction = findViewById(R.id.btn_action_counter);
        mMeter = findViewById(R.id.chronometer);
        mSbPeriodic = findViewById(R.id.seekBar_periodic);
        mPeriodic = findViewById(R.id.label_periodic_unit);

        mSbPeriodic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Utils.inFo(this, "onProgressChanged enter");
                if (progress > 0) {
                    mPeriodic.setText(String.valueOf(progress) + " s");
                    mPeriodicTime = progress;
                } else {
                    mPeriodic.setText(getString(R.string.label_time_unit));
                    mPeriodicTime = 0L;
                }

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
                Utils.inFo(this, "TimeArrive enter");
                Utils.inFo(this, "counter = " + millisecond);
                // show notification
                Utils.makeStatusNotification("Time is arrived", DemoScheduleServiceAct.this);

            }

            @Override
            public void finishUI() {
                Utils.inFo(this, "finishUI");
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.inFo(this, "onDestroy enter");
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
                mController.finishTask();
                return true;
        }

        return false;
    }

    public void onCounterAction(View v) {
        Utils.inFo(this, "onCounterAction enter");
        // Range check
        if (mPeriodicTime == 0L) {
            Utils.showToast(this, getString(R.string.label_show_non_zero_input_info));
            return;
        }

        if (!mStartCounter) {

            // disable seekbar
            mSbPeriodic.setEnabled(false);

            mController.startCount(mPeriodicTime);

            mMeter.setBase(SystemClock.elapsedRealtime());
            mMeter.start();

            mCounterAction.setText(getString(R.string.action_stop_counter));
            mStartCounter = true;
        } else {

            mController.stopCount();
            mMeter.stop();

            // enable seekbar
            mSbPeriodic.setEnabled(true);

            mCounterAction.setText(getString(R.string.action_start_counter));
            mStartCounter = false;
        }
    }

}
