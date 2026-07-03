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

package com.adam.app.demoset.bluetooth.view;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.bluetooth.model.BtDeviceItem;
import com.adam.app.demoset.bluetooth.repository.BTReceiver;
import com.adam.app.demoset.bluetooth.viewmodel.BluetoothViewModel;
import com.adam.app.demoset.databinding.ActivityDemoBluetoothBinding;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Activity for demonstrating Bluetooth Low Energy (BLE) functionalities.
 */
@AndroidEntryPoint
public class DemoBLEActivity extends AppCompatActivity {

    private static final long SCAN_PERIOD = 5000L;
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final Map<Integer, String> mBoundStateMap = new HashMap<>();
    @Inject
    BTReceiver mBTReceiver;
    private BluetoothSocket mSocket;
    private ActivityDemoBluetoothBinding mBinding;
    private final ActivityResultLauncher<Intent> mEnableBtLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_CANCELED) {
                    mBinding.switchBt.setChecked(false);
                } else {
                    mBinding.tvBtStatus.setText(R.string.demo_bt_is_enable);
                }
            }
    );
    private BluetoothViewModel mViewModel;

    private final ActivityResultLauncher<String[]> mPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> {
                        boolean allGranted = result.values().stream().allMatch(granted -> granted);
                        if (allGranted) {
                            updateBTStateUI();
                        } else {
                            Utils.showToast(this, getString(R.string.demo_bt_permissions_denied));
                        }
                    });
    private final BroadcastReceiver mUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;

            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    updateBTStateUI();
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    mViewModel.refreshPairedDevices();
                    break;
            }
        }
    };
    private BTDeviceListAdapter mScanAdapter;
    private BTDeviceListAdapter mPairedAdapter;
    private final ActivityResultLauncher<Intent> mDisableBtLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_CANCELED) {
                    mBinding.switchBt.setChecked(true);
                } else {
                    mBinding.tvBtStatus.setText(R.string.demo_bt_is_disable);
                    clearDeviceLists();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDemoBluetoothBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mViewModel = new ViewModelProvider(this).get(BluetoothViewModel.class);
        initObservers();

        UIUtils.applySystemBarInsets(mBinding.getRoot(), mBinding.appBarWrapper);

        buildBoundStateMap();
        if (!initialize()) return;

        String[] actions = {
                BluetoothAdapter.ACTION_STATE_CHANGED,
                BluetoothDevice.ACTION_BOND_STATE_CHANGED
        };
        setupReceiver(mUIReceiver, actions);
        setupReceiver(mBTReceiver, actions);

        configUIComponentListener();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionsIfNeeded();
            return;
        }

        updateBTStateUI();
    }

    private void setupReceiver(BroadcastReceiver receiver, String... actions) {
        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            filter.addAction(action);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filter, RECEIVER_EXPORTED);
        } else {
            registerReceiver(receiver, filter);
        }
    }

    private void initObservers() {
        mViewModel.getScanDevices().observe(this, devices -> {
            if (devices != null) {
                mScanAdapter.setData(devices);
                mBinding.nodataScanList.setVisibility(devices.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        mViewModel.getPairedDevices().observe(this, devices -> {
            if (devices != null) {
                mPairedAdapter.setData(devices);
                mBinding.nodataPairedList.setVisibility(devices.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        mViewModel.getIsConnected().observe(this, isConnected -> {
            if (mPairedAdapter != null && mViewModel.getConnectedPosition() != -1) {
                mPairedAdapter.updateConnectionState(mViewModel.getConnectedPosition(), isConnected);
            }
        });
    }

    private boolean initialize() {
        // Initialize stable listeners
        BTDeviceListAdapter.OnItemButtonClickListener scanBtnListener = item -> {
            BluetoothDevice device = item.getDevice();
            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                device.createBond();
            } else {
                unpairDevice(device);
            }
        };

        BTDeviceListAdapter.OnItemButtonClickListener pairedBtnListener = item -> {
            BluetoothDevice device = item.getDevice();
            unpairDevice(device);
        };

        BTDeviceListAdapter.OnItemNameClickListener pairedNameListener = item -> {
            BluetoothDevice device = item.getDevice();
            List<BtDeviceItem> list = mPairedAdapter.getCurrentList();
            int position = list.indexOf(item);
            connectDevice(device, position);
        };

        mScanAdapter = new BTDeviceListAdapter(scanBtnListener, this::showDeviceInfo);
        mPairedAdapter = new BTDeviceListAdapter(pairedBtnListener, pairedNameListener);

        mBinding.scanList.setAdapter(mScanAdapter);
        mBinding.pairedList.setAdapter(mPairedAdapter);

        if (!mViewModel.isBluetoothSupported()) {
            mBinding.tvBtStatus.setText(R.string.demo_bt_not_supported);
            mBinding.switchBt.setEnabled(false);
            return false;
        }
        return true;
    }

    private void configUIComponentListener() {
        mBinding.switchBt.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!mViewModel.isBluetoothEnabled()) {
                    requestBluetoothEnable();
                }
                startBLEScan();
            } else {
                if (mViewModel.isBluetoothEnabled()) {
                    stopBLEScan();
                    requestBluetoothDisable();
                }
            }
            updateBTStateUI();
        });
    }

    private void buildBoundStateMap() {
        mBoundStateMap.put(BluetoothDevice.BOND_BONDED, getString(R.string.demo_ble_bonded_info));
        mBoundStateMap.put(BluetoothDevice.BOND_BONDING, getString(R.string.demo_ble_bonding_info));
        mBoundStateMap.put(BluetoothDevice.BOND_NONE, getString(R.string.demo_ble_none_info));
    }

    private void showDeviceInfo(BtDeviceItem item) {
        BluetoothDevice device = item.getDevice();
        String boundState = mBoundStateMap.getOrDefault(device.getBondState(), getString(R.string.demo_ble_unknown_info));
        String name = (device.getName() == null) ? getString(R.string.demo_ble_unknown_info) : device.getName();
        String message = getString(R.string.demo_ble_device_info, name, device.getAddress(), boundState);

        Utils.DialogButton positiveBtn = new Utils.DialogButton(getString(R.string.label_ok_btn), (dialog, which) -> dialog.dismiss());
        Utils.showAlertDialog(this, R.string.demo_ble_device_info_title, message, positiveBtn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUIReceiver);
        unregisterReceiver(mBTReceiver);
        stopBLEScan();
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class<?>[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startBLEScan() {
        if (!mViewModel.isBluetoothEnabled()) return;

        mViewModel.startBleScan();
        new Handler(Looper.getMainLooper()).postDelayed(this::stopBLEScan, SCAN_PERIOD);
        Utils.showToast(this, getString(R.string.demo_ble_scan_start_toast));
    }

    private void stopBLEScan() {
        mViewModel.stopBleScan();
        Utils.showToast(this, getString(R.string.demo_ble_scan_stop_toast));
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void requestPermissionsIfNeeded() {
        mPermissionLauncher.launch(Utils.getBluetoothPermissions());
    }

    private void updateBTStateUI() {
        boolean isEnabled = mViewModel.isBluetoothEnabled();
        mBinding.tvBtStatus.setText(isEnabled ? R.string.demo_bt_is_enable : R.string.demo_bt_is_disable);
        mBinding.switchBt.setChecked(isEnabled);
        if (isEnabled) {
            mViewModel.refreshPairedDevices();
        }
    }

    private void connectDevice(BluetoothDevice device, int position) {
        Utils.showToast(this, getString(R.string.demo_ble_connect_toast, device.getName()));

        new Thread(() -> {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    runOnUiThread(() -> Utils.showToast(this, getString(R.string.demo_ble_error_connect_info_toast)));
                    return;
                }

                mSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                mViewModel.cancelDiscovery();
                mSocket.connect();

                runOnUiThread(() -> {
                    Utils.showToast(this, getString(R.string.demo_ble_connect_success_toast, device.getName()));
                    mViewModel.updateConnectionState(position, true);
                });

            } catch (IOException e) {
                runOnUiThread(() -> Utils.showToast(this, getString(R.string.demo_ble_connect_failed_toast, device.getName())));
                try {
                    if (mSocket != null) mSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    private void requestBluetoothEnable() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        mEnableBtLauncher.launch(intent);
    }

    private void requestBluetoothDisable() {
        Intent intent = new Intent("android.bluetooth.adapter.action.REQUEST_DISABLE");
        mDisableBtLauncher.launch(intent);
    }

    private void clearDeviceLists() {
        mViewModel.clearDiscoveredDevices();
        mPairedAdapter.clearItems();
    }
}
