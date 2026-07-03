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
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.bluetooth.model.BtState;
import com.adam.app.demoset.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * Repository for managing Bluetooth operations and data.
 */
@Singleton
public class BluetoothRepository {

    private final BluetoothAdapter mBTAdapter;
    private final MutableLiveData<List<BluetoothDevice>> mDiscoveredDevices = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<BtState> mBtState = new MutableLiveData<>(BtState.IDLE);

    /**
     * Internal ScanCallback to prevent memory leaks in Activity.
     */
    private final ScanCallback mBleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if (device != null && device.getName() != null) {
                addDiscoveredDevice(device);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Utils.error(BluetoothRepository.this, "BLE Scan failed: " + errorCode);
            setBtState(BtState.IDLE);
        }
    };

    @Inject
    public BluetoothRepository(@ApplicationContext Context context) {
        BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (manager != null) {
            mBTAdapter = manager.getAdapter();
        } else {
            mBTAdapter = null;
        }
    }

    public LiveData<List<BluetoothDevice>> getDiscoveredDevices() {
        return mDiscoveredDevices;
    }

    public LiveData<BtState> getBtState() {
        return mBtState;
    }

    public void setBtState(BtState state) {
        mBtState.postValue(state);
    }

    public void startDiscovery() {
        if (mBTAdapter != null) {
            Utils.info(this, "startDiscovery");
            mBTAdapter.startDiscovery();
            mBtState.setValue(BtState.SCANNING);
        }
    }

    public void startBleScan() {
        if (mBTAdapter != null && mBTAdapter.isEnabled()) {
            BluetoothLeScanner scanner = mBTAdapter.getBluetoothLeScanner();
            if (scanner != null) {
                try {
                    clearDiscoveredDevices();
                    scanner.startScan(mBleScanCallback);
                    mBtState.setValue(BtState.SCANNING);
                } catch (SecurityException e) {
                    Utils.error(this, "SecurityException: Lack of permissions for BLE scan");
                }
            }
        }
    }

    public void stopBleScan() {
        if (mBTAdapter != null && mBTAdapter.isEnabled()) {
            BluetoothLeScanner scanner = mBTAdapter.getBluetoothLeScanner();
            if (scanner != null) {
                try {
                    scanner.stopScan(mBleScanCallback);
                    mBtState.setValue(BtState.IDLE);
                } catch (SecurityException e) {
                    Utils.error(this, "SecurityException: Lack of permissions to stop BLE scan");
                }
            }
        }
    }

    public void cancelDiscovery() {
        if (mBTAdapter != null && mBTAdapter.isDiscovering()) {
            try {
                mBTAdapter.cancelDiscovery();
                mBtState.setValue(BtState.IDLE);
            } catch (SecurityException e) {
                Utils.error(this, "SecurityException: Lack of permissions to cancel discovery");
            }
        }
    }

    public void addDiscoveredDevice(BluetoothDevice device) {
        List<BluetoothDevice> current = mDiscoveredDevices.getValue();
        if (current != null && !current.contains(device)) {
            current.add(device);
            mDiscoveredDevices.postValue(current);
        }
    }

    public void clearDiscoveredDevices() {
        mDiscoveredDevices.postValue(new ArrayList<>());
    }

    public List<BluetoothDevice> getPairedDevices() {
        if (mBTAdapter != null) {
            try {
                Set<BluetoothDevice> pairedSet = mBTAdapter.getBondedDevices();
                return new ArrayList<>(pairedSet);
            } catch (SecurityException e) {
                Utils.error(this, "SecurityException: Lack of BLUETOOTH_CONNECT permission");
                return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }

    public boolean isBluetoothEnabled() {
        return mBTAdapter != null && mBTAdapter.isEnabled();
    }

    public BluetoothAdapter getAdapter() {
        return mBTAdapter;
    }
}
