package com.adam.app.demoset.bluetooth;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class BTReceiver extends BroadcastReceiver {

    // Find bt device action
    public static final String ACTION_FOUND_BT_DEVICE = "find bt device";
    public static final String KEY_DEVICE_LIST = "device.list";

    // Update bt bound state
    public static final String ACTION_UPDATE_BT_BOUND_STATE = "bt bound state";
    public static final String KEY_BT_DEVICE = "bluetooth.device";
    public static final String KEY_BUNDLE_DEVICE = "bundle device";

    private BluetoothAdapter mBTAdapter;

    private WeakReference<DemoBTAct> mActRef;
    private AlertDialog mDialog;

    private ArrayList<BluetoothDevice> mBTDevices;

    private BluetoothManager mBtManager;

    public BTReceiver(DemoBTAct act) {
        mActRef = new WeakReference<DemoBTAct>(act);

        mDialog = buildAlertDialog();

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        mBTDevices = new ArrayList<BluetoothDevice>();
    }

    private AlertDialog buildAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActRef.get());
        builder.setTitle(R.string.label_bt_status);
        builder.setView(R.layout.dialog_bt_scanning);
        builder.setCancelable(false);

        return builder.create();
    }

    //Record the prev action in receiver
    private String preAction = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Utils.inFo(this, "action = " + action);

        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
//            Utils.showSnackBar(context, "ACTION_STATE_CHANGED");
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            Utils.inFo(this, "state = " + state);

            // After on it enter to scan state
            if (state == BluetoothAdapter.STATE_ON) {
                Utils.showToast(context, "bt state on");
                mBTAdapter.startDiscovery();


            } else if (state == BluetoothAdapter.STATE_OFF) {
                Utils.showToast(context, "bt state off");

            }


        } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
//            Utils.showSnackBar(context, "ACTION_DISCOVERY_STARTED");
            showAlertDialog();

        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//            Utils.showSnackBar(context, "ACTION_DISCOVERY_FINISHED");

            // This action would trigger by the two condition, one is scan and the other is bounding
            if (preAction.equals(BluetoothDevice.ACTION_FOUND)) {
                hideAlertDialog();

                // Send bt device list to UI receiver
                Intent it = new Intent();
                it.setAction(ACTION_FOUND_BT_DEVICE);
                it.putParcelableArrayListExtra(KEY_DEVICE_LIST, mBTDevices);
                context.sendBroadcast(it);
            }

            preAction = action;


        } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            // Bt device bound state
            int state = device.getBondState();
            if (state == BluetoothDevice.BOND_NONE) {
                mBTDevices.add(device);
            }

            preAction = action;

        } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
            final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
            BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Utils.inFo(this, "bound state: " + state);
            Utils.inFo(this, "bound prevstate: " + prevState);
            if (device != null) {
                Utils.inFo(this, "address: " + device.getAddress());

                // update bt information
                Intent it = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable(KEY_BT_DEVICE, device);
                it.putExtra(KEY_BUNDLE_DEVICE, bundle);
                it.setAction(ACTION_UPDATE_BT_BOUND_STATE);
                context.sendBroadcast(it);
            }

        }

    }


    private void showAlertDialog() {

        if (mDialog != null) {
            mDialog.show();
        }
    }

    private void hideAlertDialog() {

        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
}
