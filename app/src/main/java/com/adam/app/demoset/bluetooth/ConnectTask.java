package com.adam.app.demoset.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;

import com.adam.app.demoset.Utils;

import java.io.IOException;

import java.util.UUID;

public class ConnectTask implements Runnable {

    public static final String ACTION_UPDATE_CONNECT_INFO = "connect.info";
    public static final String KEY_CONNECT_INFO = "key.connect";

    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private BluetoothAdapter mBTAdapter;
    private Context mContext;

    public ConnectTask(Context context, BluetoothDevice device) {

        Utils.inFo(this, "Connect task construct");
        this.mContext = context;

        this.mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        this.mDevice = device;

        ParcelUuid[] uuids = device.getUuids();

        //Test
        for (ParcelUuid uuid : uuids) {
            Utils.inFo(this, "uuid: " + uuid.toString());
            try {
                mSocket = mDevice.createRfcommSocketToServiceRecord(UUID.fromString(String.valueOf(uuid)));
            } catch (IOException e) {
                Utils.inFo(this, "Socket's create() method failed");
            }
        }

//        try {
//            mSocket = mDevice.createRfcommSocketToServiceRecord(UUID.fromString(String.valueOf(uuids[8])));
//        } catch (IOException e) {
//            Utils.inFo(this, "Socket's create() method failed");
//        }

    }

    @Override
    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        mBTAdapter.cancelDiscovery();

        // Connect to the remote device through the socket. This call blocks
        // until it succeeds or throws an exception.
        try {
            mSocket.connect();
        } catch (IOException e) {
            // Unable to connect; close the socket and return.
            try {
                mSocket.close();
            } catch (IOException e1) {
                Utils.inFo(this, "Could not close the client socket");
            }
        }
        Utils.inFo(this, "Connect succeeded");
        Utils.inFo(this, "Connect status: " + mSocket.isConnected());

        // test
        if (!mSocket.isConnected()) {
            cancel();
        }

        sendConnectInfo();
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        Utils.inFo(this, "cancel enter....");
        try {
            mSocket.close();
        } catch (IOException e) {
            Utils.inFo(this,"Could not close the client socket");
        }

        sendConnectInfo();
        Utils.inFo(this, "Connect status: " + mSocket.isConnected());
    }

    private void sendConnectInfo() {
        Utils.inFo(this, "send connect infomation");
        Intent it = new Intent();
        it.setAction(ACTION_UPDATE_CONNECT_INFO);
        it.putExtra(KEY_CONNECT_INFO, mSocket.isConnected());
        this.mContext.sendBroadcast(it);
    }

}
