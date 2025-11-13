/**
 * Copyright (C) 2021 Adam Chen
 * <p>
 * This class is the demo of handler thread
 *
 * @author Adam Chen
 * @version 1.0
 * @since 2021-11-11
 */
package com.adam.app.demoset.myHandlerThread;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.adam.app.demoset.databinding.ActivityDemoMyHandlerThreadBinding;

/**
 * This is a demo of handler thread
 */
public class DemoMyHandlerThreadAct extends AppCompatActivity implements HandlerObserver {

    // view binding
    private ActivityDemoMyHandlerThreadBinding mBinding;

    // My handler thread
    private MyHandlerThread mWorkThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityDemoMyHandlerThreadBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());


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
     *
     * @param view button view
     */
    public void executeTask(View view) {
        // get string from resource
        String msg = getResources().getString(R.string.toast_execute_hdthrd);
        Utils.showToast(this, msg);
        mWorkThread.executeTask();
    }


    /**
     * Cancel task by press button
     *
     * @param view button view
     */
    public void cancelTask(View view) {
        String msg = getResources().getString(R.string.toast_cancel_hdthrd);
        Utils.showToast(this, msg);
        mWorkThread.cancelTask();
    }

    /**
     * This is callback when the handler thread notification is triggered
     *
     * @param data work data
     */
    @Override
    public void updateTaskInfo(WorkData data) {
        Utils.info(this, "[update] enter");

        // Perform network request
        String result = String.valueOf(data.getCounter()); //"Work: " + data.getCounter() + " time";
        // update task info
        runOnUiThread(() -> {
            mBinding.tvWorkValue.setText(result);
        });

        Utils.info(this, "[update] exit");
    }

    @Override
    public void updateTaskStatus(boolean isActive) {
        String msg = (isActive)? getString(R.string.demo_handler_thread_work_task_is_active_msg)
                : getString(R.string.demo_handler_thread_work_task_is_idle_msg);
        Utils.showToast(this, msg);
    }
}
