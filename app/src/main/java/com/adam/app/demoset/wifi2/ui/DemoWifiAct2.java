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
import android.net.NetworkInfo;
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

public class DemoWifiAct2 extends AppCompatActivity {

    public static final int REQUEST_WIFI_PERMISSION_CODE = 0x1357;
    private static final String[] WIFI_PERMISSION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    
    private final WifiBroadcastReceiver mWifiReceiv = new WifiBroadcastReceiver();
    private WifiViewModel mViewModel;
    private ActivityDemoWifiAct2Binding mBinding;
    private ApListAdapter mAdapter;
    private boolean mPermissionGranted;
    private AlertDialog mProgressDialog;
    private AlertDialog mWifiSettingDialog;

    private final BaseWifiDialog.DialogListener mListener = new BaseWifiDialog.DialogListener() {
        @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        @Override
        public void onResult(WifiConnectData data) {
            if (data != null) {
                if (data.getPassword() != null) {
                    mViewModel.connectWifi(data.getSsid(), data.getPassword());
                } else {
                    mViewModel.disconnect();
                }
            }
        }
    };


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

        if (Utils.askPermission(this, WIFI_PERMISSION, REQUEST_WIFI_PERMISSION_CODE)) {
            Utils.info(this, getString(R.string.wifi_permission_granted));
            this.mPermissionGranted = true;
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.getApplicationContext().registerReceiver(this.mWifiReceiv, filter, RECEIVER_EXPORTED);
        } else {
            this.getApplicationContext().registerReceiver(this.mWifiReceiv, filter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPermissionGranted) {
            checkWifiState();
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private void checkWifiState() {
        if (!mViewModel.checkWifiEnabled()) {
            if (mWifiSettingDialog != null && mWifiSettingDialog.isShowing()) {
                return;
            }
            mWifiSettingDialog = new WifiRequestSettingDialog(this, data -> {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
            }).create();
            mWifiSettingDialog.show();
        } else {
            if (mWifiSettingDialog != null && mWifiSettingDialog.isShowing()) {
                mWifiSettingDialog.dismiss();
            }
            mViewModel.refreshState();
        }
    }

    private void initViews() {
        mBinding.wifiList.setLayoutManager(new LinearLayoutManager(this));
        mBinding.wifiList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        
        this.mAdapter = new ApListAdapter(result -> {
            String connectedSsid = mViewModel.getConnectedSsid().getValue();
            if (result.SSID != null && result.SSID.equals(connectedSsid)) {
                showDisconnectDialog(result);
            } else {
                Utils.info(DemoWifiAct2.this, getString(R.string.wifi_scan_result_log, result.toString()));
                new WifiConnectDialog(DemoWifiAct2.this, result, DemoWifiAct2.this.mListener).create().show();
            }
        });
        mBinding.wifiList.setAdapter(this.mAdapter);

        mBinding.btnExitWifi.setOnClickListener(v -> finish());
    }

    private void showDisconnectDialog(android.net.wifi.ScanResult result) {
        new WifiDisconnectDialog(this, result, mListener).create().show();
    }

    private void observeViewModel() {
        mViewModel.getWifiList().observe(this, list -> {
            Utils.info(this, "onUpdateWifiList");
            mAdapter.updateList(list);
        });

        mViewModel.getConnectedSsid().observe(this, ssid -> {
            Utils.info(this, "onConnectedSsidChanged: " + ssid);
            mAdapter.updateConnectedSsid(ssid);
        });

        mViewModel.getToastMessage().observe(this, msg -> {
            if (msg != null) {
                Utils.showToast(this, msg);
            }
        });

        mViewModel.getProgressMessageRes().observe(this, resId -> {
            if (resId != null && resId != 0) {
                showProgressDialog(resId);
            } else {
                dismissProgressDialog();
            }
        });
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

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.getApplicationContext().unregisterReceiver(this.mWifiReceiv);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WIFI_PERMISSION_CODE) {
            if (grantResults.length == WIFI_PERMISSION.length) {
                boolean allGranted = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                        break;
                    }
                }
                if (allGranted) {
                    Utils.showToast(this, getString(R.string.wifi_permission_granted));
                    this.mPermissionGranted = true;
                    checkWifiState();
                } else {
                    Utils.showToast(this, getString(R.string.wifi_permission_denied));
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private class WifiBroadcastReceiver extends BroadcastReceiver {
        @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                handleWifiState(state);
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    handleNetworkState(info.getState());
                }
            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                Utils.info(DemoWifiAct2.this, "Scan results available!");
                mViewModel.fetchScanResults();
            }
        }

        private void handleWifiState(int state) {
            switch (state) {
                case WifiManager.WIFI_STATE_DISABLED:
                    Utils.showToast(DemoWifiAct2.this, getString(R.string.wifi_state_disabled));
                    checkWifiState();
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    Utils.showToast(DemoWifiAct2.this, getString(R.string.wifi_state_enabled));
                    checkWifiState();
                    break;
            }
        }

        private void handleNetworkState(NetworkInfo.State state) {
            switch (state) {
                case DISCONNECTED:
                    Utils.showToast(DemoWifiAct2.this, getString(R.string.wifi_network_disconnected));
                    break;
                case CONNECTED:
                    Utils.showToast(DemoWifiAct2.this, getString(R.string.wifi_network_connected));
                    break;
                case CONNECTING:
                    Utils.showToast(DemoWifiAct2.this, getString(R.string.wifi_network_connecting));
                    break;
            }
        }
    }
}
