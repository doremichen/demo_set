/**
 * Copyright (C) 2026 Adam Chen Demo app project. All rights reserved.
 * <p>
 * Description: This is a demo jni activity
 * </p>
 * <p>
 * Author: Adam Chen
 * Date: 2026/03/16
 */
package com.adam.app.demoset.jnidemo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adam.app.demoset.LogAdapter;
import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoJniBinding;
import com.adam.app.demoset.jnidemo.viewmodel.JNIViewModel;
import com.adam.app.demoset.utils.UIUtils;

import java.util.ArrayList;

public class DemoJniAct extends AppCompatActivity {

    // view binding
    private ActivityDemoJniBinding mBinding;
    // view model
    private JNIViewModel mViewModel;

    // log adapter
    private LogAdapter mLogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view binding
        mBinding = ActivityDemoJniBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        UIUtils.applySystemBarInsets(mBinding.getRoot(), mBinding.appBarWrapper);

        // init view model
        mViewModel = new ViewModelProvider(this).get(JNIViewModel.class);
        mBinding.setVm(mViewModel);
        mBinding.setLifecycleOwner(this);

        // binding view model to jni
        NativeUtils.setViewModel(mViewModel);

        initRecycler();

        observerLog();

    }

    private void observerLog() {
        mViewModel.getLogs().observe(this, logs -> {
            mLogAdapter.submitList(new ArrayList<>(logs), () -> {
                mBinding.recyclerLog.scrollToPosition(mLogAdapter.getItemCount() - 1);
            });
        });
    }

    private void initRecycler() {
        mLogAdapter = new LogAdapter();

        // set layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        mBinding.recyclerLog.setLayoutManager(linearLayoutManager);
        mBinding.recyclerLog.setAdapter(mLogAdapter);
    }
}