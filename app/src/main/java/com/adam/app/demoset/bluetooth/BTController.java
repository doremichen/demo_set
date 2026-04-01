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

package com.adam.app.demoset.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

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
        BluetoothManager manager = (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
        mBTAdapter = manager.getAdapter(); //BluetoothAdapter.getDefaultAdapter();
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
        return this.mBTDevices;
    }


    public void add(BluetoothDevice device) {
        this.mBTDevices.add(device);
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

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ctx);

        // Set custom view
        View customView = LayoutInflater.from(ctx).inflate(R.layout.dialog_bt_scanning, null);
        builder.setView(customView);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();

        // Set background transparent
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        return dialog;

//        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
//        builder.setTitle(R.string.label_bt_status);
//        builder.setView(R.layout.dialog_bt_scanning);
//        builder.setCancelable(false);
//
//        return builder.create();
    }



}
