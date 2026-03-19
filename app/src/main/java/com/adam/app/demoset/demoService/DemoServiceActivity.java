/**
 * Copyright (C) Adam demo app Project. All rights reserved.
 * <p>
 * Description: This is the demo service activity.
 * <p>
 * Author: Adam Chen
 * Date: 2026/03/11
 */
package com.adam.app.demoset.demoService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adam.app.demoset.databinding.ActivityDemoServiceBinding;
import com.adam.app.demoset.demoService.adapter.LogAdapter;
import com.adam.app.demoset.demoService.model.ServiceEvent;
import com.adam.app.demoset.demoService.service.ServiceHelper;
import com.adam.app.demoset.demoService.util.ServiceLogBus;
import com.adam.app.demoset.demoService.viewmodel.ServiceMonitorViewModel;

import java.util.ArrayList;
import java.util.List;

public class DemoServiceActivity extends AppCompatActivity {

    // view binding
    private ActivityDemoServiceBinding mBinding;
    // view model
    private ServiceMonitorViewModel mViewModel;
    // service status log receiver
    private final BroadcastReceiver mLogReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra(ServiceLogBus.KEY_MSG);
            mViewModel.addLog(msg);
        }
    };
    // log adapter
    private LogAdapter mLogAdapter;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // view binding
        mBinding = ActivityDemoServiceBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        // view model
        mViewModel = new ViewModelProvider(this).get(ServiceMonitorViewModel.class);
        // data binding
        mBinding.setVm(mViewModel);
        mBinding.setLifecycleOwner(this);

        // init log adapter
        mLogAdapter = new LogAdapter();
        // set layout  manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // set stack from bottom
        layoutManager.setStackFromEnd(true);

        mBinding.recyclerLog.setLayoutManager(layoutManager);
        // set adapter
        mBinding.recyclerLog.setAdapter(mLogAdapter);

        // observer log
        mViewModel.getLogs().observe(this, this::updateLog);
        // observer start service request
        mViewModel.getStartService().observe(this, this::updateStartService);
        // observer bind service request
        mViewModel.getBindRequest().observe(this, this::updateBindService);


        // register log receiver
        registerReceiver(mLogReceiver, new IntentFilter(ServiceLogBus.ACTION_LOG), RECEIVER_EXPORTED);

    }

    private void updateBindService(Boolean wouldBind) {
        if (wouldBind) {
            // bind service
            ServiceHelper.getInstance().bindService(this);
            // add log
            mViewModel.addLog("bind service");
        } else {
            // unbind service
            ServiceHelper.getInstance().unbindService(this);
            // add log
            mViewModel.addLog("unbind service");
        }
    }

    private void updateStartService(Boolean wouldStart) {
        if (wouldStart) {
            // start service
            ServiceHelper.getInstance().startService(this);
            // add log
            mViewModel.addLog("start service");
        } else {
            // stop service
            ServiceHelper.getInstance().stopService(this);
            // add log
            mViewModel.addLog("stop service");
        }

    }

    private void updateLog(List<ServiceEvent> logs) {
        // input validation
        if (logs == null || logs.isEmpty()) {
            return;
        }

        // updat log list
        List<ServiceEvent> newList = new ArrayList<>(logs);

        mLogAdapter.submitList(newList, () -> {

            int position = newList.size() - 1;

            if (position >= 0) {
                mBinding.recyclerLog.smoothScrollToPosition(position);
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unregister log receiver
        unregisterReceiver(mLogReceiver);
    }
}