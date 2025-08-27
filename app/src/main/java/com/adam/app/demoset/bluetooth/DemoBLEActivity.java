/**
 * Description: This class is used to demo ble activity.
 *
 * Author: Adam Chen
 * Date: 2025/08/26
 */
package com.adam.app.demoset.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.adam.app.demoset.databinding.ActivityDemoBluetoothBinding;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

public class DemoBLEActivity extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BT_CODE = 1000;
    public static final  int REQUEST_DISABLE_BT_CODE = 1001;

    // SCAN_PERIOD: 5000L
    private static final long SCAN_PERIOD = 5000L;

    // RFCOMM General SPP UUID
    private static final UUID SPP_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Bluetooth socket
    private BluetoothSocket mSocket;

    // view binding
    private ActivityDemoBluetoothBinding mBinding;

    // Connect task
    private ConnectTask mConnectTask;


    // create activity result map: key is request code, value is BiConsumer<Integer, Intent>
    private final Map<Integer, BiConsumer<Integer, Intent>> mActivityResultMap = new HashMap<>();

    // ble bounded state map: key is device bound state, value is string
    private final Map<Integer, String> mBoundStateMap = new HashMap<>();

    // list adapter
    private BTDeviceListAdapter mScanAdapter;
    private BTDeviceListAdapter mPairedAdapter;

    // array list
    private final ArrayList<BluetoothDevice> mScanDevices = new ArrayList<>();
    private final ArrayList<BluetoothDevice> mPairedDevices = new ArrayList<>();

    // bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter;
    // ble scanner
    private BluetoothLeScanner mBLEScanner;

    // Scan Callback
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, @NonNull ScanResult result) {
            Utils.info(DemoBLEActivity.this, "onScanResult enter");
            BluetoothDevice device = result.getDevice();
            if (device == null || device.getName() == null) {
                Utils.info(DemoBLEActivity.this, "Device name is null");
                return;
            }

            if (!mScanDevices.contains(device)) {
                Utils.info(DemoBLEActivity.this, "Add new device: " + device.getName());
                mScanDevices.add(device);
                mScanAdapter.setData(mScanDevices);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Utils.info(DemoBLEActivity.this, "onScanFailed enter");
            Utils.showToast(DemoBLEActivity.this, "Scan failed: " + errorCode);
        }
    };

    // permission launcher
    private final ActivityResultLauncher<String[]> mPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> {
                        boolean allGranted = true;
                        for (Boolean granted : result.values()) {
                            if (!Boolean.TRUE.equals(granted)) {
                                allGranted = false;
                                break;
                            }
                        }
                        if (allGranted) {
                            Utils.info(DemoBLEActivity.this, "All permissions granted");
                            Utils.showToast(DemoBLEActivity.this, "All permissions granted");

                            updateBTStateUI();
                        } else {
                            Utils.info(DemoBLEActivity.this, "Some permissions denied");
                            Utils.showToast(DemoBLEActivity.this, "Some permissions denied");

                        }
            });


    // Broadcast receiver to monitor bluetooth state and bond state changes
    private final BroadcastReceiver mBTReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;

            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    // Bluetooth on/off state changed
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    Utils.info(DemoBLEActivity.this, "Bluetooth state changed: " + state);
                    updateBTStateUI();
                    break;

                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    // Device bond (pair/unpair) state changed
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    int prevBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                    Utils.info(DemoBLEActivity.this,
                            "Bond state changed: " + (device != null ? device.getName() : "Unknown") +
                                    " from " + prevBondState + " to " + bondState);

                    // Update paired device list when bond state changes
                    updatePairedList();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.info(this, "onCreate enter");
        super.onCreate(savedInstanceState);
        // view binding
        mBinding = ActivityDemoBluetoothBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        // build bound state map
        buildBoundStateMap();

        if (!initialize()) return;

        // setup activity result handler
        setupActivityResultHandler();

        // register bluetooth broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBTReceiver, filter);

        configUIComponentListener();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionsIfNeeded();
            return;
        }

        updateBTStateUI();

    }

    private boolean initialize() {
        // initialize bt device list adapter
        this.mScanAdapter = new BTDeviceListAdapter(this);
        this.mPairedAdapter = new BTDeviceListAdapter(this);
        // set adapter
        mBinding.scanList.setAdapter(mScanAdapter);
        mBinding.pairedList.setAdapter(mPairedAdapter);
        // set empty view
        mBinding.scanList.setEmptyView(mBinding.nodataScanList);
        mBinding.pairedList.setEmptyView(mBinding.nodataPairedList);


        // initialize bt adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Utils.info(this, "Bluetooth not supported");
            Utils.showToast(this, "Bluetooth not supported");
            // show bt status
            this.mBinding.tvBtStatus.setText("Bluetooth not supported");
            // switch bt function
            this.mBinding.switchBt.setEnabled(false);
            return false;
        }
        return true;
    }

    private void configUIComponentListener() {
        // set switch bt listener
        mBinding.switchBt.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Utils.info(this, "switchBt checked: " + isChecked);
            if (isChecked) {
                if (!mBluetoothAdapter.isEnabled()) {
                    requestBluetoothEnable();
                }
                startBLEScan();
            } else {
                if (mBluetoothAdapter.isEnabled()) {
                    stopBLEScan();
                    requestBluetoothDisable();
                }
            }
            updateBTStateUI();
        });

        // set adapter action: name and button
        this.mScanAdapter.setButtonListener(position -> {
            Utils.info(this, "Button clicked: " + position);
            // get bt device
            BluetoothDevice device = mScanDevices.get(position);
            // check if device is paired
            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                // create bound
                device.createBond();
            } else {
                // unbond
                unpairDevice(device);
            }
        });
        this.mScanAdapter.setNameListener(position -> {
            Utils.info(this, "Name clicked: " + position);
            // get bt device
            BluetoothDevice device = mScanDevices.get(position);
            // show device info
            showDeviceInfo(device);
        });

        this.mPairedAdapter.setButtonListener(position ->{
            Utils.info(this, "Button clicked: " + position);
            // get bt device
            BluetoothDevice device = mPairedDevices.get(position);
            // unbond
            unpairDevice(device);
        });

        this.mPairedAdapter.setNameListener(position -> {
            // get bt device
            BluetoothDevice device = mPairedDevices.get(position);
            // connect to device by thread
            connectDevice(device, position);

        });
    }


    private void buildBoundStateMap() {
        mBoundStateMap.put(BluetoothDevice.BOND_BONDED, getString(R.string.demo_ble_bonded_info));
        mBoundStateMap.put(BluetoothDevice.BOND_BONDING, getString(R.string.demo_ble_bonding_info));
        mBoundStateMap.put(BluetoothDevice.BOND_NONE, getString(R.string.demo_ble_none_info));
    }


    private void showDeviceInfo(BluetoothDevice device) {
        Utils.info(this, "showDeviceInfo enter");
        String boundState;
        if (mBoundStateMap.containsKey(device.getBondState())) {
            boundState = mBoundStateMap.get(device.getBondState());
        } else {
            boundState = getString(R.string.demo_ble_unknown_info);
        }
        String name = (device.getName() == null) ? getString(R.string.demo_ble_unknown_info) : device.getName();
        String message = getString(R.string.demo_ble_device_info, name, device.getAddress(), boundState);
        //post DialogButton
        Utils.DialogButton positiveBtn = new Utils.DialogButton(getString(R.string.label_ok_btn), (dialog, which) -> {
            dialog.dismiss();
        });
        Utils.showAlertDialog(this, R.string.demo_ble_device_info_title, message, positiveBtn);

    }

    /**
     * setup activity result handler
     */
    private void setupActivityResultHandler() {
        mActivityResultMap.put(REQUEST_ENABLE_BT_CODE, (resultCode, data) -> {
            // Reject to enable bt
            if (resultCode == RESULT_CANCELED) {
                mBinding.switchBt.setChecked(false);
            } else {
                // update bt status
                mBinding.tvBtStatus.setText(getString(R.string.demo_bt_is_enable));
            }
        });
        mActivityResultMap.put(REQUEST_DISABLE_BT_CODE, (resultCode, data) -> {
            // Reject to disable bt
            if (resultCode == RESULT_CANCELED) {
                mBinding.switchBt.setChecked(true);
            } else {
                // update bt status
                mBinding.tvBtStatus.setText(getString(R.string.demo_bt_is_disable));
                clearDeviceLists();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // get activity result handler from mActivityResultMap
        BiConsumer<Integer, Intent> handler = mActivityResultMap.get(requestCode);
        if (handler != null) {
            handler.accept(resultCode, data);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unregister bluetooth broadcast receiver
        unregisterReceiver(mBTReceiver);

        // clear scan device list
        mScanDevices.clear();
        mScanAdapter.clearItems();
        // clear paired device list
        mPairedDevices.clear();
        mPairedAdapter.clearItems();
        // stop scan
        stopBLEScan();
    }

    private void unpairDevice(BluetoothDevice device) {
        Utils.info(this, "unpairDevice enter");
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startBLEScan() {
        // null check and bt is enable
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) return;

        if (mBLEScanner == null) {
            mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }
        if (mBLEScanner != null) {
            // clear scan device list
            mScanDevices.clear();
            mScanAdapter.clearItems();
            // start scan
            mBLEScanner.startScan(mScanCallback);

            // auto stop scan after SCAN_PERIOD
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                stopBLEScan();
            }, SCAN_PERIOD);

            // show toast
            Utils.showToast(this, getString(R.string.demo_ble_scan_start_toast));
        }

    }

    private void stopBLEScan() {
        if (mBLEScanner != null) {
            mBLEScanner.stopScan(mScanCallback);
            Utils.showToast(this, getString(R.string.demo_ble_scan_stop_toast));
        }
    }

    private void requestPermissionsIfNeeded() {
        Utils.info(this, "requestPermissionsIfNeeded enter");
        String[] needPermissions = new String[]{
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_ADVERTISE,
                android.Manifest.permission.BLUETOOTH_CONNECT};
        mPermissionLauncher.launch(needPermissions);
    }

    private void updateBTStateUI() {
        Utils.info(this, "updateBTStateUI enter");
        // check bt is enable by adapter
        boolean isEnabled = mBluetoothAdapter.isEnabled();
        // update bt status info
        this.mBinding.tvBtStatus.setText(isEnabled ? getString(R.string.demo_bt_is_enable) : getString(R.string.demo_bt_is_disable));
        // set switch bt state
        this.mBinding.switchBt.setChecked(isEnabled);
        Utils.info(this, "Bt is " + (isEnabled ? "enable" : "disable"));
        // update list if bt is enable
        if (isEnabled) {
            updatePairedList();
        }
    }

    private void updatePairedList() {
        Utils.info(this, "updatePairedList enter");
        // get paired device from adapter
        mPairedDevices.clear();
        final Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
        mPairedDevices.addAll(bondedDevices);
        // update list
        mPairedAdapter.setData(mPairedDevices);
    }

    private void connectDevice(BluetoothDevice device, int position) {
        // show toast  to tell user to try to connect
        Utils.showToast(this, getString(R.string.demo_ble_connect_toast, device.getName()));

        new Thread(() -> {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    runOnUiThread(() -> Utils.showToast(this, getString(R.string.demo_ble_error_connect_info_toast)));
                    return;
                }

                mSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                mBluetoothAdapter.cancelDiscovery();
                mSocket.connect();

                runOnUiThread(() -> {
                    Utils.showToast(this, getString(R.string.demo_ble_connect_success_toast, device.getName()));
                    mPairedAdapter.updateConnectionState(position, true);
                });

            } catch (IOException e) {
                Utils.error(this, "Connect failed: " + device.getName());
                runOnUiThread(() ->
                        Utils.showToast(this, getString(R.string.demo_ble_connect_failed_toast, device.getName()))
                );

                try {
                    if (mSocket != null) mSocket.close();
                } catch (IOException ex) {
                    Utils.error(this, "Close socket failed:\n" + ex.getMessage());
                }
            }
        }).start();
    }

    /**
     * Start bt enable dialog
     */
    private void requestBluetoothEnable() {
        Utils.info(this, "[requestBluetoothEnable]");
        Intent EnableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(EnableBtIntent, REQUEST_ENABLE_BT_CODE);
    }

    /**
     * Start bt disable dialog
     */
    private void requestBluetoothDisable() {
        Utils.info(this, "[requestBluetoothDisable]");
        Intent disableBtIntent = new Intent("android.bluetooth.adapter.action.REQUEST_DISABLE");
        startActivityForResult(disableBtIntent, REQUEST_DISABLE_BT_CODE);
    }

    private void clearDeviceLists() {

        // check mScanAdapter and mPairedAdapter Validity
        if (!Utils.checkValidObject(mScanAdapter, mPairedAdapter)) {
            // show error message in toast
            Utils.showToast(this, "mScanAdapter or mPairedAdapter is null");
            return;
        }

        // clear device lists
        mScanAdapter.clearItems();
        mPairedAdapter.clearItems();
    }

}