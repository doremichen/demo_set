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