package com.adam.app.demoset.bluetooth;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
            Utils.inFo(this, "the item button " + position + " is pressed");
            BluetoothDevice device = this.mDevices.get(position);

            if (mTask == null) {
                mTask = new ConnectTask(DemoBTAct.this, (device));
                new Thread(mTask).start();
            } else {
                // disconnect
                mTask.cancel();
                mTask = null;
            }
            Utils.inFo(this, "connect down....");
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
            Utils.inFo(this, "the item button " + position + " is pressed");
            BluetoothDevice device = this.mDevices.get(position);

            // Check BT status bound/unbound
            Utils.inFo(this, "bound state = " + device.getBondState());
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
            Utils.inFo(this, "the item button " + position + " is pressed");
            BluetoothDevice device = this.mDevices.get(position);

            // Check BT status bound/unbound
            Utils.inFo(this, "bound state = " + device.getBondState());
            unpairDevice(device);
        }
    }


    private BroadcastReceiver mUIReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (Utils.ACTION_SHOW_SNACKBAR.equals(action)) {
                String msg = intent.getStringExtra(Utils.KEY_MSG);

                Snackbar.make(mLayout, "Service status: " + msg, Snackbar.LENGTH_SHORT).show();

            } else if(BTReceiver.ACTION_FOUND_BT_DEVICE.equals(action)) {
                Utils.inFo(this, "Get ACTION_FOUND_BT_DEVICE....");
                mScanDevices = intent.getExtras().getParcelableArrayList(BTReceiver.KEY_DEVICE_LIST);

                // Show scan list
                mScanAdapter.setData(mScanDevices);
                mScanAdapter.setButtonListener(new ScanItemButtonClick(mScanDevices));
                mScanList.setAdapter(mScanAdapter);

                // Update paired list
                updatePairedList();

            } else if(BTReceiver.ACTION_UPDATE_BT_BOUND_STATE.equals(action)) {
                Utils.inFo(this, "Update bt information....");

                Bundle bundle = intent.getBundleExtra(BTReceiver.KEY_BUNDLE_DEVICE);
                BluetoothDevice device = bundle.getParcelable(BTReceiver.KEY_BT_DEVICE);

                Utils.inFo(this, "Bond state = " + device.getBondState());
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
            } else if(ConnectTask.ACTION_UPDATE_CONNECT_INFO.equals(action)) {
                // Update list
                boolean isConnect = intent.getBooleanExtra(ConnectTask.KEY_CONNECT_INFO, false);
                Utils.inFo(this, "got connect status: " + isConnect);
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
        this.setContentView(R.layout.activity_demo_bluetooth);

        mLayout = (LinearLayout)this.findViewById(R.id.btdemo_layout);
        mBTSwitch = (Switch)this.findViewById(R.id.switch_bt);
        mBTResult = (TextView)this.findViewById(R.id.tv_bt_status);
        mScanList = (ListView)this.findViewById(R.id.scan_list);
        mScanAdapter = new BTDeviceListAdapter(this);
        mPairedList = (ListView)this.findViewById(R.id.paired_list);
        mPairedAdapter = new BTDeviceListAdapter(this);

        //Set empty vliew
        TextView emptyView = (TextView)this.findViewById(android.R.id.empty);
        mScanList.setEmptyView(emptyView);
        mPairedList.setEmptyView(emptyView);

        mBTReceiver = new BTReceiver(this);

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();


        // Register snack listener
        IntentFilter uiFilter = new IntentFilter();
        uiFilter.addAction(Utils.ACTION_SHOW_SNACKBAR);
        uiFilter.addAction(BTReceiver.ACTION_FOUND_BT_DEVICE);
        uiFilter.addAction(BTReceiver.ACTION_UPDATE_BT_BOUND_STATE);
        uiFilter.addAction(ConnectTask.ACTION_UPDATE_CONNECT_INFO);
        registerReceiver(mUIReceiver, uiFilter);

        // Register switch listener
        mBTSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBTResult.setText("BT is " + (isChecked? "enable":"disable"));

                if (isChecked) {
                    askPermission();
                } else {

                    // Check bt discovery status
                    if (mBTAdapter.isDiscovering()) {
                        mBTAdapter.cancelDiscovery();
                    }

                    mBTAdapter.disable();

                    // clear list
                    mPairedDevices.clear();
                    mScanDevices.clear();
                    mScanAdapter.notifyDataSetChanged();
                    mPairedAdapter.notifyDataSetChanged();

                }

            }
        });

        // Check bt adapter is valid
        if (mBTAdapter == null) {
            Utils.showToast(this, "BT adapter is invalid");
            mBTSwitch.setChecked(false);
        } else {
            Utils.showToast(this, "BT adapter is valid...");
            // Check bt power status
            mBTSwitch.setChecked(mBTAdapter.isEnabled());
            // Auto scan
            if (mBTAdapter.isEnabled()) {
                mBTAdapter.startDiscovery();
            }

        }

        // Register BT receiver
        IntentFilter btFilter = new IntentFilter();
        btFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        btFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        btFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        btFilter.addAction(BluetoothDevice.ACTION_FOUND);
        btFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBTReceiver, btFilter);

    }

    private void updatePairedList() {
        Set<BluetoothDevice> btSet = mBTAdapter.getBondedDevices();
        mPairedDevices = new ArrayList<BluetoothDevice>(btSet);

        mPairedAdapter.setData(mPairedDevices);
        mPairedAdapter.setButtonListener(new PairedItemButtonClick(mPairedDevices));
        mPairedAdapter.setNameListner(new PairedItemNameClickListener(mPairedDevices));


        mPairedList.setAdapter(mPairedAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT_CODE) {
            Utils.inFo(this, "resultCode = " + resultCode);

            // Reject to enable bt
            if (resultCode == RESULT_CANCELED ) {
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
        this.getMenuInflater().inflate(R.menu.action_bt_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.demo_bt_exit:
                this.finish();
                return true;
        }

        return false;
    }

    /**
     * Bluetooth permission must declare is either ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION
     * because Bluetooth scans can be used to gather information about the location of the user.
     */
    private void askPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Utils.inFo(this, "ACCESS_COARSE_LOCATION is not granted");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Snackbar.make(mLayout, "permission request rational",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(DemoBTAct.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        REQUEST_ACCESS_COARSE_PERMISSION_CODE);
                            }
                        })
                        .show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_COARSE_PERMISSION_CODE);
            }
        } else {
            // Permission granted and enable bt
            enableBT();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_ACCESS_COARSE_PERMISSION_CODE) {
             if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 // Permission granted and enable bt
                 enableBT();

             } else {
                 // Permission denied
             }
        }
    }

    /**
     * Start bt enable/disable dialog
     */
    private void enableBT() {
        Intent it = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(it, REQUEST_ENABLE_BT_CODE);
    }

    /**
     * Paired BT device
     * @param device
     */
    private void pairDevice(BluetoothDevice device) {
        Utils.inFo(this, "pairDevice enter");
        device.createBond();
    }

    /**
     * Unpaired BT device
     * @param device
     */
    private void unpairDevice(BluetoothDevice device) {
        Utils.inFo(this, "unpairDevice enter");
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
