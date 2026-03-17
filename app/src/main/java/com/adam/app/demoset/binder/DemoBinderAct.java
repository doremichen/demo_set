/**
 * Copyright (C) Adam demo app Project. All rights reserved.
 * <p>
 * Description: This class is the main activity of the demo binder.
 * </p>
 * <p>
 * Author: Adam Chen
 * Date: 2026/03/17
 */
package com.adam.app.demoset.binder;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adam.app.demoset.LogAdapter;
import com.adam.app.demoset.Utils;
import com.adam.app.demoset.binder.service.BinderType;
import com.adam.app.demoset.binder.service.MyAidlService;
import com.adam.app.demoset.binder.service.MyMessengerService;
import com.adam.app.demoset.binder.viewmodel.BinderViewModel;
import com.adam.app.demoset.databinding.ActivityDemoBinderBinding;

import java.util.ArrayList;

public class DemoBinderAct extends AppCompatActivity {

    // view binding
    private ActivityDemoBinderBinding mBinding;
    // view model
    private BinderViewModel mViewModel;

    // log adapter
    private LogAdapter mLogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set fit system windows
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        // view binding
        mBinding = ActivityDemoBinderBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(mBinding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // init view model
        mViewModel = new ViewModelProvider(this).get(BinderViewModel.class);
        mBinding.setVm(mViewModel);
        mBinding.setLifecycleOwner(this);

        // bindservice
        binderMyService(this, MyAidlService.class, BinderType.AIDL.getConnect());
        binderMyService(this, MyMessengerService.class, BinderType.MESSENGER.getConnect());

        initRecycler();

        observerLog();
    }

    private void observerLog() {
        mViewModel.getOperationLogs().observe(this, logs -> {
            mLogAdapter.submitList(new ArrayList<>(logs), () ->{
                mBinding.recyclerLog.scrollToPosition(logs.size()-1);
            });
        });
    }

    private void initRecycler() {
        mLogAdapter = new LogAdapter();

        // set layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mBinding.recyclerLog.setLayoutManager(layoutManager);
        mBinding.recyclerLog.setAdapter(mLogAdapter);
    }

    private void binderMyService(Context context, Class<?> target, ServiceConnection connection) {
        Utils.info(this, "binderMyService enter");
        Intent intent = new Intent(context, target);
        this.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }
}