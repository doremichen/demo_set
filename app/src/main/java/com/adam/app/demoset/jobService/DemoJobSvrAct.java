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

package com.adam.app.demoset.jobService;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoJobSvrBinding;
import com.adam.app.demoset.jobService.viewmodel.DemoJobSvrViewModel;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;

/**
 * Demo Job Service Activity
 * Refactored to MVVM with Material 3 components.
 */
public class DemoJobSvrAct extends AppCompatActivity {

    private ActivityDemoJobSvrBinding mBinding;
    private DemoJobSvrViewModel mViewModel;

    // Spinner trigger type constants
    private static final int SPINNER_SET_PERIODIC = 0;

    /**
     * Spinner display items
     */
    private final String[] mSpinnerItems = {
            "setPeriodic",
            "setOverrideDeadline",
            "setMinimumLatency"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Data Binding and ViewModel
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_demo_job_svr);
        mViewModel = new ViewModelProvider(this).get(DemoJobSvrViewModel.class);
        
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(this);

        // Apply window insets for edge-to-edge support
        UIUtils.applySystemBarInsets(mBinding.rootLayout, mBinding.toolbar);

        buildSpinner();
        setupActionButtons();
    }

    /**
     * Set up the trigger interval type spinner
     */
    private void buildSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, mSpinnerItems);
        mBinding.spinnerSetInterval.setAdapter(adapter);

        mBinding.spinnerSetInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.onTriggerTypeSelected(position);

                // Show information dialog when "Periodic" is selected
                if (position == SPINNER_SET_PERIODIC) {
                    Utils.DialogButton okButton = new Utils.DialogButton(
                            getResources().getString(R.string.label_ok_btn),
                            null);
                    Utils.showAlertDialog(DemoJobSvrAct.this,
                            R.string.label_dialog_info,
                            R.string.label_job_spinner_info,
                            okButton);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    /**
     * Set up direct button listeners
     */
    private void setupActionButtons() {
        // Start and Stop are handled via XML Data Binding to ViewModel
        
        // Handle Exit button
        mBinding.btnExit.setOnClickListener(v -> finish());
    }
}
