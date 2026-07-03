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

package com.adam.app.demoset.bluetooth.repository;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;

import androidx.core.content.ContextCompat;

import com.adam.app.demoset.utils.DemoAppConstants;
import com.adam.app.demoset.utils.Utils;

import java.io.IOException;
import java.util.UUID;

/**
 * Task for connecting to a Bluetooth device in a background thread.
 */
public class ConnectTask implements Runnable {

    public static final String ACTION_UPDATE_CONNECT_INFO = DemoAppConstants.ACTION_UPDATE_CONNECT_INFO;
    public static final String KEY_CONNECT_INFO = DemoAppConstants.KEY_CONNECT_INFO;

    private final BluetoothDevice mDevice;
    private final BluetoothAdapter mBTAdapter;
    private final Context mContext;
    private BluetoothSocket mSocket;

    public ConnectTask(Context context, BluetoothDevice device) {
        Utils.info(this, "ConnectTask constructed");
        this.mContext = context.getApplicationContext();
        BluetoothManager manager = ContextCompat.getSystemService(context, BluetoothManager.class);
        if (manager != null) {
            this.mBTAdapter = manager.getAdapter();
        } else {
            this.mBTAdapter = null;
        }
        this.mDevice = device;
    }

    @Override
    public void run() {
        if (mDevice == null || mBTAdapter == null) {
            Utils.info(this, "Device or Adapter is null, cannot connect.");
            sendConnectInfo(false);
            return;
        }

        ParcelUuid[] uuids = mDevice.getUuids();
        if (uuids == null || uuids.length == 0) {
            Utils.info(this, "No UUIDs found for device: " + mDevice.getName());
            sendConnectInfo(false);
            return;
        }

        mBTAdapter.cancelDiscovery();

        boolean connected = false;
        for (ParcelUuid parcelUuid : uuids) {
            UUID uuid = parcelUuid.getUuid();
            Utils.info(this, "Trying UUID: " + uuid);

            try {
                mSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
                mSocket.connect();

                if (mSocket.isConnected()) {
                    Utils.info(this, "Connected to " + mDevice.getName() + " with UUID: " + uuid);
                    connected = true;
                    break;
                }
            } catch (IOException e) {
                Utils.info(this, "Connection failed with UUID " + uuid + ": " + e.getMessage());
                closeSocket();
            }
        }

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

    /**
     * Cancels the connection attempt.
     */
    public void cancel() {
        Utils.info(this, "Cancel called.");
        closeSocket();
        sendConnectInfo(false);
    }

    /**
     * Sends the connection status back to the UI via broadcast.
     *
     * @param isConnected True if connected, false otherwise.
     */
    private void sendConnectInfo(boolean isConnected) {
        Utils.info(this, "Sending connect info: " + isConnected);
        Intent intent = new Intent(ACTION_UPDATE_CONNECT_INFO);
        intent.putExtra(KEY_CONNECT_INFO, isConnected);
        mContext.sendBroadcast(intent);
    }
}
