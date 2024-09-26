package com.adam.app.demoset.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;

import com.adam.app.demoset.Utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class ConnectTask implements Runnable {

    public static final String ACTION_UPDATE_CONNECT_INFO = "connect.info";
    public static final String KEY_CONNECT_INFO = "key.connect";

    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private BluetoothAdapter mBTAdapter;
    private Context mContext;

    public ConnectTask(Context context, BluetoothDevice device) {
        Utils.info(this, "Connect task construct");
        this.mContext = context;
        this.mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mDevice = device;
        connectToDevice(device);
    }

    private void connectToDevice(BluetoothDevice device) {
        ParcelUuid[] uuids = device.getUuids();
        if (uuids != null) {
            Arrays.stream(uuids)
                    .forEach(uuid -> {
                        Utils.info(this, "uuid: " + uuid.toString());
                        try {
                            mSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid.toString()));
                        } catch (IOException e) {
                            Utils.info(this, "Socket's create() method failed");
                        }
                    });
        }
    }

    @Override
    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        mBTAdapter.cancelDiscovery();

        // Connect to the remote device through the socket. This call blocks
        // until it succeeds or throws an exception.
        try {
            mSocket.connect();
            Utils.info(this, "Connect succeeded");
            Utils.info(this, "Connect status: " + mSocket.isConnected());
        } catch (IOException e) {
            Utils.info(this, "IOException!!!");
            // Unable to connect; close the socket and return.
            closeSocket();
        }

        // test
        if (!mSocket.isConnected()) {
            cancel();
        }

        sendConnectInfo();
    }

    private void closeSocket() {
        try {
            mSocket.close();
        } catch (IOException e) {
            Utils.info(this, "Could not close the client socket");
        }
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        Utils.info(this, "cancel enter....");
        closeSocket();

        sendConnectInfo();
        Utils.info(this, "Connect status: " + mSocket.isConnected());
    }

    private void sendConnectInfo() {
        Utils.info(this, "send connect infomation");
        Intent it = new Intent();
        it.setAction(ACTION_UPDATE_CONNECT_INFO);
        it.putExtra(KEY_CONNECT_INFO, mSocket.isConnected());
        this.mContext.sendBroadcast(it);
    }

}
