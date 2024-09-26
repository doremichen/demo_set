/**
 * BT controller
 */
package com.adam.app.demoset.bluetooth;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton
 */
public enum BTController {
    INSTANCE;

    private AlertDialog mDialog;
    private BluetoothAdapter mBTAdapter;
    private ArrayList<BluetoothDevice> mBTDevices;


    /**
     * Initial data
     * @param ctx
     */
    public void init(Context ctx) {
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mBTDevices = new ArrayList<>();
        mDialog = buildAlertDialog(ctx);
    }

    /**
     * Start bt discovery
     */
    public void startDiscovery() {
        Utils.info(this, "startDiscovery");
        mBTAdapter.startDiscovery();
    }


    /**
     * Return the list of bt device
     * @return
     */
    public ArrayList<BluetoothDevice> getBluetoothDevices() {
        Utils.info(this, "getListOfBTDevice");
        return new ArrayList<>(this.mBTDevices);
    }


    /**
     * Show scanning dialog
     */
    public void showAlertDialog() {
        //null check
        if (!Utils.areAllNotNull(this.mDialog)) {
            Utils.info(this, "Not valid dialog instance!!!");
            return;
        }
        mDialog.show();
    }

    /**
     * close scanning dialog
     */
    public void closeAlertDialog() {
        //null check
        if (!Utils.areAllNotNull(this.mDialog)) {
            Utils.info(this, "Not valid dialog instance!!!");
            return;
        }
        mDialog.dismiss();
    }

    /**
     * Create scanning alert dialog
     * @param ctx
     * @return
     */
    private AlertDialog buildAlertDialog(Context ctx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(R.string.label_bt_status);
        builder.setView(R.layout.dialog_bt_scanning);
        builder.setCancelable(false);

        return builder.create();
    }



}
