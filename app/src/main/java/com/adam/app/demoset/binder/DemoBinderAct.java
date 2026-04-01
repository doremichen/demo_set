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

import com.adam.app.demoset.utils.LogAdapter;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.binder.service.BinderType;
import com.adam.app.demoset.binder.service.MyAidlService;
import com.adam.app.demoset.binder.service.MyMessengerService;
import com.adam.app.demoset.binder.viewmodel.BinderViewModel;
import com.adam.app.demoset.databinding.ActivityDemoBinderBinding;
import com.adam.app.demoset.utils.UIUtils;

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

        UIUtils.applySystemBarInsets(mBinding.getRoot(), mBinding.appBarWrapper);

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