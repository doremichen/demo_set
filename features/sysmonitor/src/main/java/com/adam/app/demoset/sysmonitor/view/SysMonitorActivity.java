/*
 * MIT License
 *
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

package com.adam.app.demoset.sysmonitor.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.sysmonitor.R;
import com.adam.app.demoset.sysmonitor.databinding.ActivitySysMonitorBinding;
import com.adam.app.demoset.sysmonitor.viewmodel.SysMonitorViewModel;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * SysMonitorActivity provides the user interface to display system status.
 * It uses Hilt for dependency injection and DataBinding for UI updates.
 */
@AndroidEntryPoint
public class SysMonitorActivity extends AppCompatActivity {

    private SysMonitorViewModel viewModel;
    private ActivitySysMonitorBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize DataBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sys_monitor);
        binding.setLifecycleOwner(this);

        // Initialize ViewModel using Hilt
        viewModel = new ViewModelProvider(this).get(SysMonitorViewModel.class);

        // Bind ViewModel to the layout
        binding.setViewModel(viewModel);
    }
}
