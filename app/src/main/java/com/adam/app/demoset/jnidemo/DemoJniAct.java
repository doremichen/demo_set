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

package com.adam.app.demoset.jnidemo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adam.app.demoset.utils.LogAdapter;
import com.adam.app.demoset.databinding.ActivityDemoJniBinding;
import com.adam.app.demoset.jnidemo.viewmodel.JNIViewModel;
import com.adam.app.demoset.utils.UIUtils;

import java.util.ArrayList;

/**
 * DemoJniAct - A demonstration of JNI (Java Native Interface) implementation.
 *
 * Refactored using Clean Architecture Principles:
 * - Domain Layer: Defines JniRepository interface and UseCases (GetHello, PerformCalculation).
 * - Data Layer: Implements JniRepository via JniRepositoryImpl and NativeUtils (JNI Bridge).
 * - Presentation Layer: JNIViewModel interacts with UseCases to handle UI logic and state.
 *
 * Features:
 * 1. Basic String retrieval from Native layer.
 * 2. Static/Instance data callbacks from Native to Java.
 * 3. Native calculations (passing parameters).
 * 4. Fetching system information directly from C++ layer.
 */
public class DemoJniAct extends AppCompatActivity {

    // View binding
    private ActivityDemoJniBinding mBinding;
    // View model
    private JNIViewModel mViewModel;

    // Log adapter
    private LogAdapter mLogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View binding initialization
        mBinding = ActivityDemoJniBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        UIUtils.applySystemBarInsets(mBinding.getRoot(), mBinding.appBarWrapper);

        // Init view model
        mViewModel = new ViewModelProvider(this).get(JNIViewModel.class);
        mBinding.setVm(mViewModel);
        mBinding.setLifecycleOwner(this);

        // Binding view model to JNI for callbacks
        NativeUtils.setViewModel(mViewModel);

        initRecycler();
        observeLog();
    }

    /**
     * Observe logs from ViewModel and update RecyclerView
     */
    private void observeLog() {
        mViewModel.getLogs().observe(this, logs -> {
            mLogAdapter.submitList(new ArrayList<>(logs), () -> {
                mBinding.recyclerLog.scrollToPosition(mLogAdapter.getItemCount() - 1);
            });
        });
    }

    /**
     * Initialize RecyclerView for logging
     */
    private void initRecycler() {
        mLogAdapter = new LogAdapter();

        // Set layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        mBinding.recyclerLog.setLayoutManager(linearLayoutManager);
        mBinding.recyclerLog.setAdapter(mLogAdapter);
    }
}
