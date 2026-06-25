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

package com.adam.app.demoset.quicksetting.presentation;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityQuickSettingResultBinding;
import com.adam.app.demoset.utils.DemoAppConstants;
import com.adam.app.demoset.utils.UIUtils;

/**
 * Activity that displays the result of a Quick Setting interaction.
 * Triggered by QuickSettingIntentService.
 */
public class QuickSettingResultAct extends AppCompatActivity {

    public static final String KEY_RESULT_SETTING_TITLE = "key.result.title";
    public static final String KEY_RESULT_SETTING_STATE = "key.result.state";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Data Binding
        ActivityQuickSettingResultBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_quick_setting_result);
        
        // Extract data from Intent
        String qsTitle = getIntent().getStringExtra(KEY_RESULT_SETTING_TITLE);
        String qsStatus = getIntent().getStringExtra(KEY_RESULT_SETTING_STATE);

        // Set data to binding
        binding.setTitle(qsTitle != null ? qsTitle : "Unknown Title");
        binding.setStatus("Current Status: " + (qsStatus != null ? qsStatus : DemoAppConstants.UNKNOWN_STATUS));

        setupUI(binding);
    }

    /**
     * Sets up UI components and accessibility.
     */
    private void setupUI(ActivityQuickSettingResultBinding binding) {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            binding.toolbar.setNavigationOnClickListener(v -> finish());
        }

        binding.btnBack.setOnClickListener(v -> finish());

        // Apply system bar insets for edge-to-edge support
        UIUtils.applySystemBarInsets(binding.rootLayout, binding.resultTitle);
    }
}
