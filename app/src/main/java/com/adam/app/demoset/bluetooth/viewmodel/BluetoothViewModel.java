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

package com.adam.app.demoset.bluetooth.viewmodel;

import android.bluetooth.BluetoothDevice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.adam.app.demoset.bluetooth.model.BtState;
import com.adam.app.demoset.bluetooth.repository.BluetoothRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for managing Bluetooth state and device lists.
 */
@HiltViewModel
public class BluetoothViewModel extends ViewModel {

    private final BluetoothRepository mRepository;

    private final MutableLiveData<List<BluetoothDevice>> mPairedDevices = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Integer> mConnectedPosition = new MutableLiveData<>(-1);
    private final MutableLiveData<Boolean> mIsConnected = new MutableLiveData<>(false);

    @Inject
    public BluetoothViewModel(BluetoothRepository repository) {
        this.mRepository = repository;
    }

    public LiveData<List<BluetoothDevice>> getScanDevices() {
        return mRepository.getDiscoveredDevices();
    }

    public LiveData<List<BluetoothDevice>> getPairedDevices() {
        return mPairedDevices;
    }

    public LiveData<BtState> getBtState() {
        return mRepository.getBtState();
    }

    public LiveData<Boolean> getIsConnected() {
        return mIsConnected;
    }

    public void refreshPairedDevices() {
        mPairedDevices.setValue(mRepository.getPairedDevices());
    }

    public void updateConnectionState(int position, boolean isConnected) {
        mConnectedPosition.setValue(position);
        mIsConnected.setValue(isConnected);
    }

    public int getConnectedPosition() {
        return mConnectedPosition.getValue() != null ? mConnectedPosition.getValue() : -1;
    }

    public void startDiscovery() {
        mRepository.startDiscovery();
    }

    public void startBleScan() {
        mRepository.startBleScan();
    }

    public void stopBleScan() {
        mRepository.stopBleScan();
    }

    public void cancelDiscovery() {
        mRepository.cancelDiscovery();
    }

    public boolean isBluetoothEnabled() {
        return mRepository.isBluetoothEnabled();
    }

    public boolean isBluetoothSupported() {
        return mRepository.getAdapter() != null;
    }

    public void addDiscoveredDevice(BluetoothDevice device) {
        mRepository.addDiscoveredDevice(device);
    }

    public void clearDiscoveredDevices() {
        mRepository.clearDiscoveredDevices();
    }

    public BluetoothRepository getRepository() {
        return mRepository;
    }
}
