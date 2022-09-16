package com.adam.app.demoset.myHandlerThread;

import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

public class DemoMyHandlerThreadAct extends AppCompatActivity implements HandlerObserver {

    // Task info
    private TextView mTaskInfo;


    // My handler thread
    private MyHandlerThread mWorkThread;

    // UI handler
    private Handler mUIHandler = new Handler();

    // UI task
    private class UITask implements Runnable {

        @Override
        public void run() {
            // Update infomation
            int value = WorkData.newInstance().getCounter();
            mTaskInfo.setText("Work: " + value + " time");
        }
    }

    private UITask mUITask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_my_handler_thread);

        //Initial text view
        mTaskInfo = findViewById(R.id.tv_work_info);


        mUITask = new UITask();

        // start my handler thread
        mWorkThread = new MyHandlerThread();
        // register observer
        mWorkThread.registerObserver(this);
        // Start my handler thread
        mWorkThread.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // quit the my handler thread
        mWorkThread.quit();
        // Unregister observer
        mWorkThread.unregisterObserver(this);

        // reset task counter
        WorkData.newInstance().setCounter(0);
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

    public void executeTask(View view) {
        Utils.showToast(this, "execute button is pressed");
        mWorkThread.executeTask();
    }


    public void cancelTask(View view) {
        Utils.showToast(this, "cancel button is pressed");
        mWorkThread.cancelTask();
    }

    //
    // This is callback when the handler thread notification is triggered
    //
    @Override
    public void updateTaskInfo() {
        Utils.info(this, "[update] enter");

        mUIHandler.post(mUITask);

        Utils.info(this, "[update] exit");
    }
}
