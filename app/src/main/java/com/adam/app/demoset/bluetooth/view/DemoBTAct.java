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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.bluetooth.model.BtDeviceItem;
import com.adam.app.demoset.bluetooth.model.BtState;
import com.adam.app.demoset.bluetooth.repository.BTReceiver;
import com.adam.app.demoset.bluetooth.repository.ConnectTask;
import com.adam.app.demoset.bluetooth.viewmodel.BluetoothViewModel;
import com.adam.app.demoset.databinding.ActivityDemoBluetoothBinding;
import com.adam.app.demoset.utils.DemoAppConstants;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Activity for demonstrating classic Bluetooth functionalities.
 */
@AndroidEntryPoint
public class DemoBTAct extends AppCompatActivity {

    public static final int REQUEST_ACCESS_COARSE_PERMISSION_CODE = 1;

    @Inject
    BTReceiver mBTReceiver;

    private BluetoothViewModel mViewModel;
    private BTDeviceListAdapter mScanAdapter;
    private BTDeviceListAdapter mPairedAdapter;
    private PairedItemNameClickListener mPairedItemNameClicklistener;
    private ActivityDemoBluetoothBinding mBinding;
    private final ActivityResultLauncher<String[]> mPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                boolean allGranted = result.values().stream().allMatch(granted -> granted);
                if (allGranted) {
                    autoDiscoveryIfBTOn();
                } else {
                    Utils.showToast(this, getString(R.string.demo_bt_permissions_denied));
                }
            }
    );
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
    private final ActivityResultLauncher<Intent> mDisableBtLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_CANCELED) {
                    mBinding.switchBt.setChecked(true);
                } else {
                    mViewModel.cancelDiscovery();
                    mBinding.tvBtStatus.setText(R.string.demo_bt_is_disable);
                    clearDeviceLists();
                }
            }
    );
    private final CompoundButton.OnCheckedChangeListener mCheckBoxListener = (buttonView, isChecked) -> {
        Utils.info(this, "BT checkbox status: " + isChecked);
        if (isChecked) {
            if (!mViewModel.isBluetoothEnabled()) {
                requestBluetoothEnable();
            }
        } else {
            Utils.info(this, "Reset status because the BT choice is off.");
            requestBluetoothDisable();
        }
    };
    private final BroadcastReceiver mUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DemoAppConstants.ACTION_SHOW_SNACKBAR.equals(action)) {
                String msg = intent.getStringExtra(DemoAppConstants.KEY_SNACKBAR_MSG);
                Snackbar.make(mBinding.getRoot(), "Service status: " + msg, Snackbar.LENGTH_SHORT).show();
            } else if (BTReceiver.ACTION_FOUND_BT_DEVICE.equals(action)) {
                mViewModel.refreshPairedDevices();
            } else if (BTReceiver.ACTION_UPDATE_BT_BOUND_STATE.equals(action)) {
                Bundle bundle = intent.getBundleExtra(BTReceiver.KEY_BUNDLE_DEVICE);
                if (bundle == null) return;
                BluetoothDevice device = bundle.getParcelable(BTReceiver.KEY_BT_DEVICE);
                if (device == null) return;
                // log
                Utils.info(DemoBTAct.this, "Device name: " + device.getName() + ", address: " + device.getAddress());
                mViewModel.refreshPairedDevices();
            } else if (ConnectTask.ACTION_UPDATE_CONNECT_INFO.equals(action)) {
                if (mPairedAdapter == null || mPairedItemNameClicklistener == null) return;
                boolean isConnect = intent.getBooleanExtra(ConnectTask.KEY_CONNECT_INFO, false);
                mViewModel.updateConnectionState(mPairedItemNameClicklistener.getSelectedPosition(), isConnect);
            }
        }
    };
    private AlertDialog mScanningDialog;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDemoBluetoothBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mViewModel = new ViewModelProvider(this).get(BluetoothViewModel.class);
        initObservers();

        UIUtils.applySystemBarInsets(mBinding.getRoot(), mBinding.appBarWrapper);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Utils.showToast(this, getString(R.string.bluetooth_not_supported));
            finish();
            return;
        }

        // Initialize listeners once
        ScanItemButtonClick scanBtnListener = new ScanItemButtonClick();
        PairedItemButtonClick pairedBtnListener = new PairedItemButtonClick();
        mPairedItemNameClicklistener = new PairedItemNameClickListener();

        mScanAdapter = new BTDeviceListAdapter(scanBtnListener, null);
        mPairedAdapter = new BTDeviceListAdapter(pairedBtnListener, mPairedItemNameClicklistener);

        mBinding.scanList.setAdapter(mScanAdapter);
        mBinding.pairedList.setAdapter(mPairedAdapter);

        mBinding.switchBt.setOnCheckedChangeListener(mCheckBoxListener);

        String[] uiActions = {
                DemoAppConstants.ACTION_SHOW_SNACKBAR,
                BTReceiver.ACTION_FOUND_BT_DEVICE,
                BTReceiver.ACTION_UPDATE_BT_BOUND_STATE,
                ConnectTask.ACTION_UPDATE_CONNECT_INFO,
        };
        setupReceiver(mUIReceiver, uiActions);

        String[] btActions = {
                BluetoothAdapter.ACTION_STATE_CHANGED,
                BluetoothAdapter.ACTION_DISCOVERY_STARTED,
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED,
                BluetoothDevice.ACTION_FOUND,
                BluetoothDevice.ACTION_BOND_STATE_CHANGED
        };
        setupReceiver(mBTReceiver, btActions);

        mScanningDialog = buildScanningDialog();

        initializeBluetoothWithPermissions();
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

        mViewModel.getBtState().observe(this, state -> {
            if (state == BtState.SCANNING) {
                showScanningDialog();
            } else {
                hideScanningDialog();
            }

            if (state == BtState.ON) {
                mBinding.tvBtStatus.setText(R.string.demo_bt_is_enable);
                mBinding.switchBt.setChecked(true);
            } else if (state == BtState.OFF) {
                mBinding.tvBtStatus.setText(R.string.demo_bt_is_disable);
                mBinding.switchBt.setChecked(false);
            }
        });
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

    private void initializeBluetoothWithPermissions() {
        if (mViewModel.isBluetoothEnabled()) {
            mBinding.switchBt.setChecked(true);
        }

        String[] permissions = Utils.getBluetoothPermissions();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mPermissionLauncher.launch(permissions);
        } else {
            if (Utils.askPermission(this, permissions, REQUEST_ACCESS_COARSE_PERMISSION_CODE)) {
                autoDiscoveryIfBTOn();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewModel.cancelDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBTReceiver);
        unregisterReceiver(mUIReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_exit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.demo_exit) {
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ACCESS_COARSE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                autoDiscoveryIfBTOn();
            } else {
                Utils.showAlertDialog(this, getString(R.string.demo_bt_permission_denied_title), (dialog, i) -> finish());
            }
        }
    }

    private void autoDiscoveryIfBTOn() {
        boolean enabled = mViewModel.isBluetoothEnabled();
        mBinding.switchBt.setChecked(enabled);
        if (enabled) {
            mBinding.tvBtStatus.setText(R.string.demo_bt_is_enable);
            mViewModel.refreshPairedDevices();
            mViewModel.startDiscovery();
        } else {
            mBinding.tvBtStatus.setText(R.string.demo_bt_is_disable);
        }
    }

    private void requestBluetoothEnable() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        mEnableBtLauncher.launch(intent);
    }

    private void requestBluetoothDisable() {
        Intent intent = new Intent("android.bluetooth.adapter.action.REQUEST_DISABLE");
        mDisableBtLauncher.launch(intent);
    }

    private void pairDevice(BluetoothDevice device) {
        device.createBond();
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class<?>[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Utils.info(this, e.toString());
        }
    }

    private void clearDeviceLists() {
        mScanAdapter.clearItems();
        mPairedAdapter.clearItems();
    }

    private AlertDialog buildScanningDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View customView = LayoutInflater.from(this).inflate(R.layout.dialog_bt_scanning, null);
        builder.setView(customView);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        return dialog;
    }

    private void showScanningDialog() {
        if (mScanningDialog != null && !mScanningDialog.isShowing()) {
            mScanningDialog.show();
        }
    }

    private void hideScanningDialog() {
        if (mScanningDialog != null && mScanningDialog.isShowing()) {
            mScanningDialog.dismiss();
        }
    }

    private class PairedItemNameClickListener implements BTDeviceListAdapter.OnItemNameClickListener {
        private ConnectTask mTask;
        private int mSelectedPosition = -1;

        public int getSelectedPosition() {
            return mSelectedPosition;
        }

        @Override
        public void onClick(BtDeviceItem item) {
            BluetoothDevice device = item.getDevice();
            List<BtDeviceItem> list = mPairedAdapter.getCurrentList();
            mSelectedPosition = list.indexOf(item);
            if (mTask == null) {
                mTask = new ConnectTask(DemoBTAct.this, device);
                new Thread(mTask).start();
            } else {
                mTask.cancel();
                mTask = null;
            }
        }
    }

    private class ScanItemButtonClick implements BTDeviceListAdapter.OnItemButtonClickListener {
        @Override
        public void onClick(BtDeviceItem item) {
            BluetoothDevice device = item.getDevice();
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                unpairDevice(device);
            } else {
                pairDevice(device);
            }
        }
    }

    private class PairedItemButtonClick implements BTDeviceListAdapter.OnItemButtonClickListener {
        @Override
        public void onClick(BtDeviceItem item) {
            BluetoothDevice device = item.getDevice();
            unpairDevice(device);
        }
    }
}
