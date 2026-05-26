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

package com.adam.app.demoset.dynamicdelivery;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDynamicDeliveryBinding;
import com.adam.app.demoset.dynamicdelivery.viewmodel.DynamicDeliveryViewModel;
import com.adam.app.demoset.utils.Utils;

public class DynamicDeliveryActivity extends AppCompatActivity {

    private DynamicDeliveryViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDynamicDeliveryBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_dynamic_delivery);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_demo_dynamic_delivery);
        }

        mViewModel = new ViewModelProvider(this).get(DynamicDeliveryViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        setupObservers();
    }

    private void setupObservers() {
        mViewModel.getToastMessage().observe(this, message -> {
            if (message != null) {
                Utils.showToast(this, message);
            }
        });

        mViewModel.getLaunchEvent().observe(this, shouldLaunch -> {
            if (Boolean.TRUE.equals(shouldLaunch)) {
                launchModule();
                mViewModel.onLaunchEventHandled();
            }
        });
    }

    private void launchModule() {
        try {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), "com.adam.app.demoset.dynamicfeature.DynamicFeatureActivity");
            startActivity(intent);
        } catch (Exception e) {
            Utils.showToast(this, "Launch failed: " + e.getMessage());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
