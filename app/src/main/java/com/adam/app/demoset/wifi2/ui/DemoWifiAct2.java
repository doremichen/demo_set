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

package com.adam.app.demoset.wifi2.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoWifiAct2Binding;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.wifi2.model.WifiConnectData;
import com.adam.app.demoset.wifi2.ui.adapter.ApListAdapter;
import com.adam.app.demoset.wifi2.ui.dialog.BaseWifiDialog;
import com.adam.app.demoset.wifi2.ui.dialog.WifiConnectDialog;
import com.adam.app.demoset.wifi2.ui.dialog.WifiDisconnectDialog;
import com.adam.app.demoset.wifi2.ui.dialog.WifiRequestSettingDialog;
import com.adam.app.demoset.wifi2.viewmodel.WifiViewModel;

import java.util.List;

public class DemoWifiAct2 extends AppCompatActivity {

    public static final int REQUEST_WIFI_PERMISSION_CODE = 0x1357;
    private static final String[] WIFI_PERMISSION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private ActivityDemoWifiAct2Binding mBinding;
    private WifiViewModel mViewModel;
    private ApListAdapter mAdapter;
    private AlertDialog mProgressDialog;
    private AlertDialog mWifiSettingDialog;

    private final WifiBroadcastReceiver mWifiReceiv = new WifiBroadcastReceiver();

    private final BaseWifiDialog.DialogListener mListener = new BaseWifiDialog.DialogListener() {
        @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        @Override
        public void onResult(WifiConnectData data) {
            if (data == null) return;
            if (data.getPassword() != null) {
                mViewModel.connect(data);
            } else {
                mViewModel.disconnect();
            }
        }
    };

    // --- Lifecycle ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.info(this, "onCreate");

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_demo_wifi_act2);
        mViewModel = new ViewModelProvider(this).get(WifiViewModel.class);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(this);

        UIUtils.applySystemBarInsets(mBinding.rootLayout, mBinding.appBarWrapper);

        initViews();
        observeViewModel();
        checkPermissions();
        registerWifiReceiver();
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getApplicationContext().unregisterReceiver(mWifiReceiv);
    }

    // --- Initialization ---

    private void initViews() {
        setupWifiRecyclerView();
        mBinding.btnExitWifi.setOnClickListener(v -> finish());
    }

    private void setupWifiRecyclerView() {
        mBinding.wifiList.setLayoutManager(new LinearLayoutManager(this));
        mBinding.wifiList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mAdapter = new ApListAdapter(this::onWifiItemClicked);
        mBinding.wifiList.setAdapter(mAdapter);
    }

    private void observeViewModel() {
        mViewModel.getWifiList().observe(this, this::handleWifiListUpdate);
        mViewModel.getConnectedSsid().observe(this, this::handleConnectedSsidChanged);
        mViewModel.getToastMessage().observe(this, this::handleToastMessage);
        mViewModel.getProgressMessageRes().observe(this, this::handleProgressDialog);
        mViewModel.isWifiEnabled().observe(this, isEnabled -> {
            if (isEnabled == null) return;
            if (isEnabled) {
                dismissWifiSettingDialog();
                mViewModel.refreshState();
            } else {
                showWifiSettingDialog();
            }
        });
    }

    private void checkPermissions() {
        Utils.askPermission(this, WIFI_PERMISSION, REQUEST_WIFI_PERMISSION_CODE);
    }

    private void registerWifiReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getApplicationContext().registerReceiver(mWifiReceiv, filter, RECEIVER_EXPORTED);
        } else {
            getApplicationContext().registerReceiver(mWifiReceiv, filter);
        }
    }

    // --- UI Handlers ---

    private void onWifiItemClicked(@NonNull ScanResult result) {
        Utils.info(this, "onWifiItemClicked: " + result.SSID);
        if (result.SSID == null) return;

        String connectedSsid = mViewModel.getConnectedSsid().getValue();
        if (result.SSID.equals(connectedSsid)) {
            showDisconnectDialog(result);
        } else {
            showConnectDialog(result);
        }
    }

    private void handleWifiListUpdate(List<ScanResult> list) {
        Utils.info(this, "onUpdateWifiList");
        mAdapter.updateList(list);
    }

    private void handleConnectedSsidChanged(String ssid) {
        Utils.info(this, "onConnectedSsidChanged: " + ssid);
        mAdapter.updateConnectedSsid(ssid);
    }

    private void handleToastMessage(String msg) {
        if (msg != null) {
            Utils.showToast(this, msg);
        }
    }

    private void handleProgressDialog(Integer resId) {
        if (resId != null && resId != 0) {
            showProgressDialog(resId);
        } else {
            dismissProgressDialog();
        }
    }

    // --- Dialog Management ---

    private void showConnectDialog(ScanResult result) {
        new WifiConnectDialog(this, result, mListener).create().show();
    }

    private void showDisconnectDialog(ScanResult result) {
        new WifiDisconnectDialog(this, result, mListener).create().show();
    }

    private void showWifiSettingDialog() {
        if (mWifiSettingDialog != null && mWifiSettingDialog.isShowing()) return;
        mWifiSettingDialog = new WifiRequestSettingDialog(this, data -> navigateToWifiSettings()).create();
        mWifiSettingDialog.show();
    }

    private void dismissWifiSettingDialog() {
        if (mWifiSettingDialog != null && mWifiSettingDialog.isShowing()) {
            mWifiSettingDialog.dismiss();
        }
    }

    private void navigateToWifiSettings() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }

    private void showProgressDialog(int resId) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.setMessage(getString(resId));
            return;
        }
        mProgressDialog = new AlertDialog.Builder(this)
                .setMessage(resId)
                .setCancelable(false)
                .create();
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    // --- Permission ---

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != REQUEST_WIFI_PERMISSION_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (isAllPermissionsGranted(grantResults)) {
            Utils.showToast(this, getString(R.string.wifi_permission_granted));
            mViewModel.checkWifiEnabled();
        } else {
            Utils.showToast(this, getString(R.string.wifi_permission_denied));
        }
    }

    private boolean isAllPermissionsGranted(@NonNull int[] grantResults) {
        if (grantResults.length == 0) return false;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    // --- Inner Classes ---

    private class WifiBroadcastReceiver extends BroadcastReceiver {
        @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                mViewModel.onWifiStateChanged(state);
            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                mViewModel.fetchScanResults();
            }
        }
    }
}
