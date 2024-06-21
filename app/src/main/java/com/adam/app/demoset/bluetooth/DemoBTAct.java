package com.adam.app.demoset.bluetooth;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;


public class DemoBTAct extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BT_CODE = 1000;
    public static final int REQUEST_ACCESS_COARSE_PERMISSION_CODE = 1;
    private LinearLayout mLayout;
    private Switch mBTSwitch;
    private TextView mBTResult;

    private BluetoothAdapter mBTAdapter;
    private ArrayList<BluetoothDevice> mScanDevices;
    private ArrayList<BluetoothDevice> mPairedDevices;

    /**
     * BT CheckBox Listener
     */
    private CompoundButton.OnCheckedChangeListener mCheckBoxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mBTResult.setText("BT is " + (isChecked ? "enable" : "disable"));
            Utils.info(DemoBTAct.this, "BT checkbox status: " + isChecked);
            if (isChecked) {
                if (!checkValidObject(mBTAdapter)) return;

                // Check bt power status
                boolean enabled = mBTAdapter.isEnabled();
                if (enabled == false) {
                    enableBT();
                }
            } else {
                Utils.info(DemoBTAct.this, "Reset status because the BT choice is off.");

                // Check bt discovery status
                if (mBTAdapter.isDiscovering()) {
                    mBTAdapter.cancelDiscovery();
                }
                mBTAdapter.disable();

                // clear list
                if (checkValidObject(mPairedDevices))
                    mPairedDevices.clear();
                if (checkValidObject(mScanDevices))
                    mScanDevices.clear();
                mScanAdapter.notifyDataSetChanged();
                mPairedAdapter.notifyDataSetChanged();
            }
        }
    };;


    /**
     * Callback for the name pressed of the item in paired list
     */
    private class PairedItemNameClickListener implements BTDeviceListAdapter.OnItemNameClickListener {
        private ArrayList<BluetoothDevice> mDevices;

        private ConnectTask mTask;


        public PairedItemNameClickListener(ArrayList<BluetoothDevice> devices) {
            mDevices = devices;
        }

        @Override
        public void onClick(int position) {
            Utils.info(this, "the item button " + position + " is pressed");
            BluetoothDevice device = this.mDevices.get(position);

            if (mTask == null) {
                mTask = new ConnectTask(DemoBTAct.this, (device));
                new Thread(mTask).start();
            } else {
                // disconnect
                mTask.cancel();
                mTask = null;
            }
            Utils.info(this, "connect down....");
        }
    }

    /**
     * Callback for the button pressed of the item in list
     */
    private class ScanItemButtonClick implements BTDeviceListAdapter.OnItemButtonClickListener {

        private ArrayList<BluetoothDevice> mDevices;

        public ScanItemButtonClick(ArrayList<BluetoothDevice> devices) {
            mDevices = devices;
        }

        @Override
        public void onClick(int position) {
            Utils.info(this, "the item button " + position + " is pressed");
            BluetoothDevice device = this.mDevices.get(position);

            // Check BT status bound/unbound
            Utils.info(this, "bound state = " + device.getBondState());
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                unpairDevice(device);
            } else {
                pairDevice(device);
            }
        }
    }

    /**
     * Callback for the button pressed of the item in list
     */
    private class PairedItemButtonClick implements BTDeviceListAdapter.OnItemButtonClickListener {

        private ArrayList<BluetoothDevice> mDevices;

        public PairedItemButtonClick(ArrayList<BluetoothDevice> devices) {
            mDevices = devices;
        }

        @Override
        public void onClick(int position) {
            Utils.info(this, "the item button " + position + " is pressed");
            BluetoothDevice device = this.mDevices.get(position);

            // Check BT status bound/unbound
            Utils.info(this, "bound state = " + device.getBondState());
            unpairDevice(device);
        }
    }


    private BroadcastReceiver mUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.info(DemoBTAct.this, "[onReceive@UI receiver]");

            String action = intent.getAction();
            if (Utils.ACTION_SHOW_SNACKBAR.equals(action)) {
                String msg = intent.getStringExtra(Utils.KEY_MSG);

                Snackbar.make(mLayout, "Service status: " + msg, Snackbar.LENGTH_SHORT).show();

            } else if (BTReceiver.ACTION_FOUND_BT_DEVICE.equals(action)) {
                Utils.info(this, "Get ACTION_FOUND_BT_DEVICE....");
                mScanDevices = intent.getExtras().getParcelableArrayList(BTReceiver.KEY_DEVICE_LIST);
                // null check
                if (!checkValidObject(mScanDevices, mScanAdapter, mScanList)) return;

                // Show scan list
                mScanAdapter.setData(mScanDevices);
                mScanAdapter.setButtonListener(new ScanItemButtonClick(mScanDevices));
                mScanList.setAdapter(mScanAdapter);

                // Update paired list
                updatePairedList();

            } else if (BTReceiver.ACTION_UPDATE_BT_BOUND_STATE.equals(action)) {
                Utils.info(this, "Update bt information....");

                Bundle bundle = intent.getBundleExtra(BTReceiver.KEY_BUNDLE_DEVICE);
                BluetoothDevice device = bundle.getParcelable(BTReceiver.KEY_BT_DEVICE);

                // null check
                if (!checkValidObject(mPairedDevices, mScanDevices, mScanAdapter, mPairedAdapter)) return;

                Utils.info(this, "Bond state = " + device.getBondState());
                int state = device.getBondState();
                if (state == BluetoothDevice.BOND_BONDED) {
                    mPairedDevices.add(device);
                    mScanDevices.remove(device);

                } else if (state == BluetoothDevice.BOND_NONE) {
                    mScanDevices.add(device);
                    mPairedDevices.remove(device);
                }

                // Update list
                mScanAdapter.notifyDataSetChanged();
                mPairedAdapter.notifyDataSetChanged();
            } else if (ConnectTask.ACTION_UPDATE_CONNECT_INFO.equals(action)) {
                // null check
                if (!checkValidObject(mScanAdapter, mPairedAdapter)) return;

                // Update list
                boolean isConnect = intent.getBooleanExtra(ConnectTask.KEY_CONNECT_INFO, false);
                Utils.info(this, "got connect status: " + isConnect);
                mPairedAdapter.updateAdressContent(isConnect);
                mPairedAdapter.notifyDataSetChanged();
            }

        }
    };

    private BTReceiver mBTReceiver;
    private ListView mScanList;
    private BTDeviceListAdapter mScanAdapter;
    private ListView mPairedList;
    private BTDeviceListAdapter mPairedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.info(this, "[onCreate]");
        this.setContentView(R.layout.activity_demo_bluetooth);

        // Use this check to determine whether Bluetooth classic is supported on the device.
        // Then you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Utils.showToast(this, getResources().getString(R.string.bluetooth_not_supported));
            finish();
        }
        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Utils.showToast(this, getResources().getString(R.string.ble_not_supported));
            finish();
        }

        mLayout = this.findViewById(R.id.btdemo_layout);
        mBTSwitch = this.findViewById(R.id.switch_bt);
        mBTResult = this.findViewById(R.id.tv_bt_status);
        mScanList = this.findViewById(R.id.scan_list);
        mPairedList = this.findViewById(R.id.paired_list);

        mScanAdapter = new BTDeviceListAdapter(this);
        mPairedAdapter = new BTDeviceListAdapter(this);

        //Set empty vliew
        TextView emptyScanView = this.findViewById(R.id.nodata_scan_list);
        TextView emptyPairedView = this.findViewById(R.id.nodata_paired_list);
        mScanList.setEmptyView(emptyScanView);
        mPairedList.setEmptyView(emptyPairedView);


        // Register switch listener
        mBTSwitch.setOnCheckedChangeListener(mCheckBoxListener);


        mBTReceiver = new BTReceiver(this);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        // Register snack listener
        String[] uiActions = {
                Utils.ACTION_SHOW_SNACKBAR,
                BTReceiver.ACTION_FOUND_BT_DEVICE,
                BTReceiver.ACTION_UPDATE_BT_BOUND_STATE,
                ConnectTask.ACTION_UPDATE_CONNECT_INFO,
        };
        registerUIReceiver(mUIReceiver, uiActions);

        // Register BT receiver
        String[] btActions = {
                BluetoothAdapter.ACTION_STATE_CHANGED,
                BluetoothAdapter.ACTION_DISCOVERY_STARTED,
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED,
                BluetoothDevice.ACTION_FOUND,
                BluetoothDevice.ACTION_BOND_STATE_CHANGED
        };
        registerUIReceiver(mBTReceiver, btActions);

        handleBTPowerState();
        Utils.info(this, "onCreate Done!!!");
    }


    private void registerUIReceiver(BroadcastReceiver receiver, String ... actions) {
        IntentFilter filter = new IntentFilter();
        for (String action: actions) {
            filter.addAction(action);
        }
        registerReceiver(receiver, filter, RECEIVER_EXPORTED);
    }

    /**
     * Process BT power state
     */
    private void handleBTPowerState() {
        Utils.info(this, "handleBTPowerState");
        // Check bt adapter is valid
        if (!checkValidObject(mBTAdapter)) {
            Utils.showToast(this, "BT adapter is invalid");
            mBTSwitch.setChecked(false);
            return;
        }

        Utils.showToast(this, "BT adapter is valid...");
        Utils.info(this, "check bt permission!!!");
        // ask permission
        String[] needPermissions = {Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        Utils.info(this, "Need check BT permission");
        if (Utils.askPermission(DemoBTAct.this,
                needPermissions,
                REQUEST_ACCESS_COARSE_PERMISSION_CODE)) {
            autoDiscoveryIfBTOn();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT_CODE) {
            Utils.info(this, "resultCode = " + resultCode);

            // Reject to enable bt
            if (resultCode == RESULT_CANCELED) {
                mBTSwitch.setChecked(false);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBTReceiver);
        unregisterReceiver(mUIReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_exit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.demo_exit:
                this.finish();
                return true;
        }

        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_ACCESS_COARSE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.info(this, "Permission granted and enable bt");
                // Permission granted
                autoDiscoveryIfBTOn();
            } else {
                // Permission denied
                Utils.showAlertDialog(this, "This APP permission denied", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
            }
        }
    }

    /**
     * Auto discovery if BT power on otherwise turn on BT first
     */
    private void autoDiscoveryIfBTOn() {
        Utils.info(this, "autoDiscoveryIfBTOn");
        // null check
        if (!checkValidObject(mBTAdapter, mBTSwitch)) return;

        // Check bt power status
        boolean enabled = mBTAdapter.isEnabled();
        mBTSwitch.setChecked(enabled);
        Utils.info(this, "Bt power status: " + enabled);
        // Auto scan when permission is ok and bt power status is on state
        if (enabled) {
            this.mBTAdapter.startDiscovery();
        }
    }

    private void updatePairedList() {
        Set<BluetoothDevice> btSet = mBTAdapter.getBondedDevices();
        mPairedDevices = new ArrayList<BluetoothDevice>(btSet);

        mPairedAdapter.setData(mPairedDevices);
        mPairedAdapter.setButtonListener(new PairedItemButtonClick(mPairedDevices));
        mPairedAdapter.setNameListner(new PairedItemNameClickListener(mPairedDevices));


        mPairedList.setAdapter(mPairedAdapter);
    }

    /**
     * Start bt enable/disable dialog
     */
    private void enableBT() {
        Utils.info(this, "[enableBT]");
        Intent actionRequestEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(actionRequestEnable, REQUEST_ENABLE_BT_CODE);
    }

    /**
     * Paired BT device
     *
     * @param device
     */
    private void pairDevice(BluetoothDevice device) {
        Utils.info(this, "[pairDevice]");
        device.createBond();
    }

    /**
     * Unpaired BT device
     *
     * @param device
     */
    private void unpairDevice(BluetoothDevice device) {
        Utils.info(this, "unpairDevice enter");
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Check the object validity
     * @param objs
     * @return
     */
    private boolean checkValidObject(Object... objs) {
        for (Object o: objs) {
            if (o == null) {
                return false;
            }

        }
        return true;
    }

}
