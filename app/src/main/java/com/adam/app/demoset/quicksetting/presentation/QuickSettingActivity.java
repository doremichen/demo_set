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

import android.app.StatusBarManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoQuickSettingBinding;
import com.adam.app.demoset.quicksetting.service.DemoAppWidgetProvider;
import com.adam.app.demoset.quicksetting.service.QuickSettingService;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Main activity for the Quick Setting and App Widget demo.
 * Demonstrates how to interact with TileServices and request pinning widgets.
 */
public class QuickSettingActivity extends AppCompatActivity {

    private QuickSettingViewModel viewModel;
    private ActivityDemoQuickSettingBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize View Binding and Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_demo_quick_setting);
        
        // Initialize MVVM components
        viewModel = new ViewModelProvider(this).get(QuickSettingViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        setupUI();
        observeViewModel();
        
        Utils.showAlertDialog(this, getResources().getString(R.string.welcome_to_demo_quick_setting), null);
    }

    /**
     * Configures UI elements such as toolbar and edge-to-edge insets.
     */
    private void setupUI() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            binding.toolbar.setNavigationOnClickListener(v -> finish());
        }
        
        UIUtils.applySystemBarInsets(binding.rootLayout, binding.welcomeInfo);
    }

    /**
     * Observes navigation events from the ViewModel.
     */
    private void observeViewModel() {
        viewModel.getNavigationEvent().observe(this, event -> {
            if (event == null) return;
            switch (event) {
                case REQUEST_ADD_TILE:
                    requestAddTile();
                    break;
                case REQUEST_PIN_WIDGET:
                    requestPinWidget();
                    break;
                case EXIT:
                    finish();
                    break;
            }
            viewModel.onNavigationHandled();
        });
    }

    /**
     * Demonstrates requesting to add a tile to the Quick Settings panel (API 33+).
     */
    private void requestAddTile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            StatusBarManager statusBarManager = getSystemService(StatusBarManager.class);
            ComponentName componentName = new ComponentName(this, QuickSettingService.class);
            Icon icon = Icon.createWithResource(this, R.drawable.ic_demo_qs1_active);
            Executor executor = Executors.newSingleThreadExecutor();
            
            statusBarManager.requestAddTileService(
                    componentName,
                    getString(R.string.title_demo_quick_setting),
                    icon,
                    executor,
                    result -> runOnUiThread(() -> {
                        if (result == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED) {
                            Utils.showToast(this, getString(R.string.qs_msg_add_tile_success));
                        } else if (result == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED) {
                            Utils.showToast(this, getString(R.string.qs_msg_add_tile_already_exists));
                        } else {
                            Utils.showToast(this, getString(R.string.qs_msg_add_tile_failed, result));
                        }
                    })
            );
        } else {
            Utils.showToast(this, getString(R.string.qs_msg_api_unavailable));
        }
    }

    /**
     * Demonstrates requesting the system to pin an App Widget to the Home Screen (API 26+).
     */
    private void requestPinWidget() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AppWidgetManager appWidgetManager = getSystemService(AppWidgetManager.class);
            ComponentName myProvider = new ComponentName(this, DemoAppWidgetProvider.class);

            if (appWidgetManager != null && appWidgetManager.isRequestPinAppWidgetSupported()) {
                appWidgetManager.requestPinAppWidget(myProvider, null, null);
                Utils.showToast(this, getString(R.string.qs_msg_widget_pin_requested));
            }
        } else {
            Utils.showToast(this, getString(R.string.qs_msg_widget_api_unavailable));
        }
    }
}
