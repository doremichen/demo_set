/**
 * Copyright (C) Adam Demo app Project
 * <p>
 * Description: This class is for BT Connect Task
 * <p>
 * Author: Adam Chen
 * Date: 2019/12/17
 */
package com.adam.app.demoset.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;

import androidx.core.content.ContextCompat;

import com.adam.app.demoset.utils.Utils;

import java.io.IOException;
import java.util.UUID;

public class ConnectTask implements Runnable {

    public static final String ACTION_UPDATE_CONNECT_INFO = "connect.info";
    public static final String KEY_CONNECT_INFO = "key.connect";

    private final BluetoothDevice mDevice;
    private final BluetoothAdapter mBTAdapter;
    private final Context mContext;
    private BluetoothSocket mSocket;

    public ConnectTask(Context context, BluetoothDevice device) {
        Utils.info(this, "ConnectTask constructed");
        this.mContext = context.getApplicationContext();
        BluetoothManager manager = ContextCompat.getSystemService(context, BluetoothManager.class);
        this.mBTAdapter = manager.getAdapter();
        this.mDevice = device;
    }

    @Override
    public void run() {
        if (mDevice == null) {
            Utils.info(this, "Device is null, cannot connect.");
            sendConnectInfo(false);
            return;
        }

        // Get uuid list
        ParcelUuid[] uuids = mDevice.getUuids();
        if (uuids == null || uuids.length == 0) {
            Utils.info(this, "No UUIDs found for device: " + mDevice.getName());
            sendConnectInfo(false);
            return;
        }

        // cancel discovery to prevent connection failure
        mBTAdapter.cancelDiscovery();

        boolean connected = false;

        for (ParcelUuid parcelUuid : uuids) {
            UUID uuid = parcelUuid.getUuid();
            Utils.info(this, "Trying UUID: " + uuid);

            try {
                mSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
                mSocket.connect(); // Block until connected successful

                if (mSocket.isConnected()) {
                    Utils.info(this, "Connected to " + mDevice.getName() + " with UUID: " + uuid);
                    connected = true;
                    break; // Exit the loop if connected
                }

            } catch (IOException e) {
                Utils.info(this, "Connection failed with UUID " + uuid + ": " + e.getMessage());
                closeSocket();
            }
        }

        // Report connection status
        sendConnectInfo(connected);

        if (!connected) {
            Utils.info(this, "Connection failed to all UUIDs, task ending.");
        }
    }

    private void closeSocket() {
        if (mSocket != null) {
            try {
                mSocket.close();
                Utils.info(this, "Socket closed.");
            } catch (IOException e) {
                Utils.info(this, "Could not close socket: " + e.getMessage());
            }
            mSocket = null;
        }
    }

    // Cancel the connection
    public void cancel() {
        Utils.info(this, "Cancel called.");
        closeSocket();
        sendConnectInfo(false);
    }

    /**
     * Send connect info to UI
     * @param isConnected boolean
     */
    private void sendConnectInfo(boolean isConnected) {
        Utils.info(this, "Sending connect info: " + isConnected);
        Intent it = new Intent(ACTION_UPDATE_CONNECT_INFO);
        it.putExtra(KEY_CONNECT_INFO, isConnected);
        mContext.sendBroadcast(it);
    }
}
