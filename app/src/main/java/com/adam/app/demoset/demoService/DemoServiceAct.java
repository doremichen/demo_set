/**
 * Copyright (C) Adam demo app Project
 * <p>
 * Description: This class is the main activity of the demo service.
 * <p>
 * Author: Adam Chen
 * Date: 2025/09/17
 */
package com.adam.app.demoset.demoService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.adam.app.demoset.databinding.ActivityDemoServiceBinding;
import com.adam.app.demoset.demoService.adapter.StringListAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoServiceAct extends AppCompatActivity {

    private static final String TAG = DemoServiceAct.class.getSimpleName();

    // view binding
    private ActivityDemoServiceBinding mBinding;

    private final Map<Integer, Runnable> menuActions = new HashMap<Integer, Runnable>() {{
        put(R.id.local_svr, () -> {
            Utils.showToast(DemoServiceAct.this, "Local service is configured.");
            ServiceHelper.getInstance().setRemoteServiceMode(false);
        });
        put(R.id.remote_svr, () -> {
            Utils.showToast(DemoServiceAct.this, "Remote service is configured.");
            ServiceHelper.getInstance().setRemoteServiceMode(true);
        });
        put(R.id.exit, () -> {
            DemoServiceAct.this.finish();
        });
    }};


    // receive snackbar message from service according to Utils.ACTION_SHOW_SNACKBAR
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // log
            log("onReceive enter");
            // check intent
            if (intent == null) {
                log("intent is null");
                return;
            }
            String action = intent.getAction();
            // LOG
            log("action: " + action);
            if (Utils.ACTION_SHOW_SNACKBAR.equals(action)) {
                String msg = intent.getStringExtra(Utils.KEY_MSG);
                setSnackMessage(msg);
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // view binding
        mBinding = ActivityDemoServiceBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // set context to ServiceActItems
        ServiceActItems.setActivityContext(this);

        // build string map: key -> actual string in enum
        Map<String, String> stringMap = Utils.buildStringMap(this);

        // Covert arrayList to array string
        String[] items = Arrays.stream(ServiceActItems.values())
                .map(ServiceActItems::getType)
                .map(key -> stringMap.getOrDefault(key, key))
                .toArray(String[]::new);
        List<String> list = Arrays.asList(items);
        // log list
        log("list: " + list);

        // set layout manager
        mBinding.listAction.setLayoutManager(new LinearLayoutManager(this));

        // set adapter
        StringListAdapter adapter = new StringListAdapter(list);
        mBinding.listAction.setAdapter(adapter);
        // set empty view
        mBinding.empty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        mBinding.listAction.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);

        // register receiver
        this.registerReceiver(this.mReceiver, new IntentFilter(Utils.ACTION_SHOW_SNACKBAR), RECEIVER_EXPORTED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unbind service
        ServiceHelper.getInstance().unbindService(this);

        // unregister receiver
        this.unregisterReceiver(this.mReceiver);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Service Helper
        ServiceHelper serviceHelper = ServiceHelper.getInstance();

        // unbind service
        serviceHelper.unbindService(this);

        // action
        if (menuActions.containsKey(item.getItemId())) {
            menuActions.get(item.getItemId()).run();
            return true;
        }

        return false;

    }

    /**
     * setSnackMessage
     * set snack message
     *
     * @param msg: String
     */
    public void setSnackMessage(String msg) {
        // log
        log("setSnackMessage enter");
        // show snackbar
        Snackbar.make(mBinding.getRoot(), msg, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * log
     * log message for debug
     * @param msg
     */
    private void log(String msg) {
        Utils.log(TAG, msg);
    }

}