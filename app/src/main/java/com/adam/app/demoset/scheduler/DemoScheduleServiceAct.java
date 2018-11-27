package com.adam.app.demoset.scheduler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

public class DemoScheduleServiceAct extends AppCompatActivity {

    public static final String KEY_TIME = "key.time";
    private boolean mStartCounter;

    private TextView mShowCounter;
    private Button mCounterAction;
    private Chronometer mMeter;

    private SchedulerController mController;

    private Handler mUIHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Utils.inFo(this, "handleMessage enter");
            super.handleMessage(msg);
            Bundle data = msg.getData();
            long spendTime = data.getLong(KEY_TIME);

            // Update counter
            UpdateCounter(spendTime);

        }


    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.inFo(this, "onCreate enter");
        setContentView(R.layout.activity_demo_schedule_service);

        mShowCounter = this.findViewById(R.id.tv_show_counter);
        mCounterAction = this.findViewById(R.id.btn_action_counter);
        mMeter = findViewById(R.id.chronometer);

        mController = new SchedulerController();

        mController.registeronControllerListener(new SchedulerController.onControllerListener() {
            @Override
            public void TimeArrive(long millisecond) {
                Utils.inFo(this, "TimeArrive enter");
                Utils.inFo(this, "counter = " + millisecond);
                Bundle data = new Bundle();
                data.putLong(KEY_TIME, millisecond);
                Message msg = Message.obtain();
                msg.setData(data);
                // Send message to UI queue
                mUIHander.sendMessage(msg);

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
        Utils.inFo(this, "onDestroty enter");
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

        if (!mStartCounter) {

            // Reset counter ui
            UpdateCounter(0L);

            mController.startCount();

            mMeter.setBase(SystemClock.elapsedRealtime());
            mMeter.start();

            mCounterAction.setText(this.getResources().getString(R.string.action_stop_counter));
            mStartCounter = true;
        } else {

            mController.stopCount();

            mMeter.stop();

            mCounterAction.setText(this.getResources().getString(R.string.action_start_counter));
            mStartCounter = false;
        }
    }

    private void UpdateCounter(long millisecond) {
        Utils.inFo(this, "UpdateCounter");
        long second = millisecond / 1000L;
        long minute = second / 60L;
        long hour = minute / 60L;
        long day = hour / 24L;

        if (second >= 60L)
            second %= 60L;

        if (minute >= 60L)
            minute %= 60L;

        if (hour >= 24L)
            hour %= 24L;

        if (day >= 365L)
            day %= 365L;

        // Create StringBuilder
        StringBuilder stb = new StringBuilder();
        stb.append("Counter:").append("\n");
        stb.append(String.valueOf(day)).append(" ");
        stb.append("day").append(" ");
        stb.append(String.valueOf(hour)).append(" ");
        stb.append("hr").append(" ");
        stb.append(String.valueOf(minute)).append(" ");
        stb.append("min").append(" ");
        stb.append(String.valueOf(second)).append(" ");
        stb.append("Sec").append(" ");

        //Show counter information
        mShowCounter.setText(stb.toString());

    }

}
