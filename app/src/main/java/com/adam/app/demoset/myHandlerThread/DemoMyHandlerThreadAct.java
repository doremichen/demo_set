package com.adam.app.demoset.myHandlerThread;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

/**
 * This is a demo of handler thread
 */
public class DemoMyHandlerThreadAct extends AppCompatActivity implements HandlerObserver {

    // Task info
    private TextView mTaskInfo;


    // My handler thread
    private MyHandlerThread mWorkThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_my_handler_thread);

        //Initial text view
        mTaskInfo = findViewById(R.id.tv_work_info);

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

    /**
     * Execute task by press button
     * @param view button view
     */
    public void executeTask(View view) {
        Utils.showToast(this, "execute button is pressed");
        mWorkThread.executeTask();
    }


    /**
     * Cancel task by press button
     * @param view
     */
    public void cancelTask(View view) {
        Utils.showToast(this, "cancel button is pressed");
        mWorkThread.cancelTask();
    }

    /**
     * This is callback when the handler thread notification is triggered
     * @param data work data
     */
    @Override
    public void updateTaskInfo(WorkData data) {
        Utils.info(this, "[update] enter");

        // Perform network request
        String result = "Work: " + data.getCounter() + " time";
        // update task info
        runOnUiThread(() -> {
            mTaskInfo.setText(result);
        });

        Utils.info(this, "[update] exit");
    }
}
