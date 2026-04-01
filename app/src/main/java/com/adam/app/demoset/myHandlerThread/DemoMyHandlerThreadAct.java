/*
 * Copyright (c) 2026 Adam Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.adam.app.demoset.myHandlerThread;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.databinding.ActivityDemoMyHandlerThreadBinding;
import com.adam.app.demoset.utils.UIUtils;

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

        UIUtils.applySystemBarInsets(mBinding.getRoot(), mBinding.tvTitle);


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
        String msg = (isActive) ? getString(R.string.demo_handler_thread_work_task_is_active_msg)
                : getString(R.string.demo_handler_thread_work_task_is_idle_msg);
        Utils.showToast(this, msg);
    }
}
