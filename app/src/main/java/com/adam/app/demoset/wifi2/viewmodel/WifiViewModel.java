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

package com.adam.app.demoset.wifi2.viewmodel;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.wifi2.controler.WifiController;
import com.adam.app.demoset.wifi2.model.WifiConnectData;

import java.util.Collections;
import java.util.List;

public class WifiViewModel extends AndroidViewModel {

    private static final int MSG_SCAN_WIFI = 1;

    // Controllers
    private final WifiController mWifiCtl;
    private final ConnectivityManager mConnectivityManager;

    // Observables
    private final MutableLiveData<List<ScanResult>> mWifiList = new MutableLiveData<>();
    private final MutableLiveData<String> mToastMessage = new MutableLiveData<>();
    private final MutableLiveData<Integer> mProgressMessageRes = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> mIsScanning = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mIsWifiEnabled = new MutableLiveData<>(false);
    private final MutableLiveData<String> mConnectedSsid = new MutableLiveData<>(null);

    // Threading
    private final HandlerThread mScanHandlerThread;
    private final Handler mHandler;

    // Connectivity
    private ConnectivityManager.NetworkCallback mConnectionCallback;

    public WifiViewModel(@NonNull Application application) {
        super(application);
        mWifiCtl = new WifiController(application);
        mConnectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        mScanHandlerThread = new HandlerThread("scan_wifi_thread");
        mScanHandlerThread.start();
        mHandler = new Handler(mScanHandlerThread.getLooper()) {
            @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == MSG_SCAN_WIFI) {
                    Utils.info(this, "triggering scan@handleMessage");
                    mWifiCtl.triggerScan();
                }
            }
        };
        // Initial state
        checkWifiEnabled();
    }

    // --- Getters ---

    public LiveData<List<ScanResult>> getWifiList() {
        return mWifiList;
    }

    public LiveData<String> getToastMessage() {
        return mToastMessage;
    }

    public LiveData<Integer> getProgressMessageRes() {
        return mProgressMessageRes;
    }

    public LiveData<Boolean> isScanning() {
        return mIsScanning;
    }

    public LiveData<Boolean> isWifiEnabled() {
        return mIsWifiEnabled;
    }

    public LiveData<String> getConnectedSsid() {
        return mConnectedSsid;
    }

    public WifiController getWifiController() {
        return mWifiCtl;
    }

    // --- Actions ---

    public void startScan() {
        if (!mWifiCtl.isWifiEnabled()) return;
        if (Boolean.TRUE.equals(mIsScanning.getValue())) return;

        mIsScanning.postValue(true);
        mProgressMessageRes.postValue(R.string.wifi_status_scanning);
        mHandler.removeMessages(MSG_SCAN_WIFI);
        mHandler.sendEmptyMessage(MSG_SCAN_WIFI);
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void fetchScanResults() {
        List<ScanResult> results = mWifiCtl.getScanResults();
        mWifiList.postValue(results != null ? results : Collections.emptyList());
        mIsScanning.postValue(false);
        mProgressMessageRes.postValue(0);
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void refreshState() {
        boolean enabled = mWifiCtl.isWifiEnabled();
        if (Boolean.FALSE.equals(mIsWifiEnabled.getValue()) && enabled) {
            mIsWifiEnabled.postValue(true);
        }
        
        if (enabled) {
            mConnectedSsid.postValue(mWifiCtl.getConnectedSsid());
            startScan();
        } else {
            handleWifiDisabled();
        }
    }

    public boolean checkWifiEnabled() {
        boolean enabled = mWifiCtl.isWifiEnabled();
        if (!Boolean.valueOf(enabled).equals(mIsWifiEnabled.getValue())) {
            mIsWifiEnabled.postValue(enabled);
        }

        if (!enabled) {
            handleWifiDisabled();
        }
        return enabled;
    }

    public void onWifiStateChanged(int state) {
        checkWifiEnabled();
        switch (state) {
            case WifiManager.WIFI_STATE_DISABLED:
                mToastMessage.postValue(getApplication().getString(R.string.wifi_state_disabled));
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                mToastMessage.postValue(getApplication().getString(R.string.wifi_state_enabled));
                mHandler.removeMessages(MSG_SCAN_WIFI);
                mHandler.sendEmptyMessageDelayed(MSG_SCAN_WIFI, 1000);
                break;
        }
    }

    // --- Connection Logic ---

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void connect(WifiConnectData data) {
        mProgressMessageRes.postValue(R.string.wifi_status_connecting);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            unregisterConnectionCallback();
            NetworkRequest request = mWifiCtl.createNetworkRequest(
                    data.getSsid(), data.getPassword(), data.getCapabilities());

            mConnectionCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    Utils.info(this, "Network Available: " + data.getSsid());
                    if (mConnectivityManager != null) {
                        mConnectivityManager.bindProcessToNetwork(network);
                    }
                    onConnectionSuccess(data.getSsid());
                }

                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                    Utils.info(this, "Network Unavailable");
                    onConnectionFailed("Unavailable");
                    unregisterConnectionCallback();
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    Utils.info(this, "Network Lost");
                    if (mConnectivityManager != null) {
                        mConnectivityManager.bindProcessToNetwork(null);
                    }
                    mToastMessage.postValue(getApplication().getString(R.string.wifi_network_disconnected));
                }
            };
            mConnectivityManager.requestNetwork(request, mConnectionCallback);
        } else {
            mWifiCtl.connectWifiBeforeQ(data.getSsid(), data.getPassword(), data.getCapabilities());
            onConnectionSuccess(data.getSsid());
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void disconnect() {
        mProgressMessageRes.postValue(R.string.wifi_status_disconnecting);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            unregisterConnectionCallback();
        } else {
            mWifiCtl.disconnectWifiBeforeQ();
        }
        mConnectedSsid.postValue(null);
        mProgressMessageRes.postValue(0);
    }

    private void unregisterConnectionCallback() {
        if (mConnectionCallback != null && mConnectivityManager != null) {
            try {
                mConnectivityManager.unregisterNetworkCallback(mConnectionCallback);
                mConnectivityManager.bindProcessToNetwork(null);
            } catch (Exception e) {
                Utils.error(this, "Unregister failed: " + e.getMessage());
            }
            mConnectionCallback = null;
        }
    }

    private void onConnectionSuccess(String ssid) {
        mConnectedSsid.postValue(ssid);
        mProgressMessageRes.postValue(0);
        mToastMessage.postValue(getApplication().getString(R.string.wifi_connect_success));
    }

    private void onConnectionFailed(String error) {
        mConnectedSsid.postValue(null);
        mProgressMessageRes.postValue(0);
        mToastMessage.postValue(getApplication().getString(R.string.wifi_connect_fail, error));
    }

    private void handleWifiDisabled() {
        mWifiList.postValue(Collections.emptyList());
        mConnectedSsid.postValue(null);
        mIsScanning.postValue(false);
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @Override
    protected void onCleared() {
        super.onCleared();
        unregisterConnectionCallback();
        mScanHandlerThread.quit();
    }
}
