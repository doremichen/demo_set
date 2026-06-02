package com.adam.app.demoset.wifi2.viewmodel;

import android.Manifest;
import android.app.Application;
import android.net.wifi.ScanResult;
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

import java.util.List;

public class WifiViewModel extends AndroidViewModel {

    private static final int SCAN_WIFI = 1;
    private final WifiController mWifiCtl;
    private final MutableLiveData<List<ScanResult>> mWifiList = new MutableLiveData<>();
    private final MutableLiveData<String> mToastMessage = new MutableLiveData<>();
    private final MutableLiveData<Integer> mProgressMessageRes = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> mIsScanning = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mIsWifiEnabled = new MutableLiveData<>(false);
    private final MutableLiveData<String> mConnectedSsid = new MutableLiveData<>(null);
    private final HandlerThread mScanHandlerThread;
    private final Handler mHandler;

    public WifiViewModel(@NonNull Application application) {
        super(application);
        mWifiCtl = new WifiController(application);
        mScanHandlerThread = new HandlerThread("scan_wifi_thread");
        mScanHandlerThread.start();
        mHandler = new Handler(mScanHandlerThread.getLooper()) {
            @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == SCAN_WIFI) {
                    Utils.info(this, "triggering scan@handleMessage");
                    mWifiCtl.triggerScan();
                }
            }
        };
        // Initial state
        mIsWifiEnabled.setValue(mWifiCtl.isWifiEnabled());
        if (mWifiCtl.isWifiEnabled()) {
            startScan();
        }
    }

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

    public void startScan() {
        if (!mWifiCtl.isWifiEnabled()) return;
        mIsScanning.postValue(true);
        mProgressMessageRes.postValue(R.string.wifi_status_scanning);
        mHandler.sendEmptyMessage(SCAN_WIFI);
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void fetchScanResults() {
        List<ScanResult> results = mWifiCtl.getScanResults();
        if (results == null) {
            mWifiList.postValue(java.util.Collections.emptyList());
        } else {
            mWifiList.postValue(results);
        }
        mIsScanning.postValue(false);
        mProgressMessageRes.postValue(0);
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void refreshState() {
        boolean enabled = mWifiCtl.isWifiEnabled();
        mIsWifiEnabled.postValue(enabled);
        if (enabled) {
            mConnectedSsid.postValue(mWifiCtl.getConnectedSsid());
            startScan();
        } else {
            mWifiList.postValue(java.util.Collections.emptyList());
            mConnectedSsid.postValue(null);
        }
    }

    public boolean checkWifiEnabled() {
        boolean enabled = mWifiCtl.isWifiEnabled();
        mIsWifiEnabled.postValue(enabled);
        if (!enabled) {
            mWifiList.postValue(java.util.Collections.emptyList());
            mConnectedSsid.postValue(null);
            mIsScanning.postValue(false);
        }
        return enabled;
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void connectWifi(String ssid, String password) {
        mProgressMessageRes.postValue(R.string.wifi_status_connecting);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mWifiCtl.connectWifiAfterQ(ssid, password, new WifiController.ConnectListener() {
                @Override
                public void onSuccess() {
                    mConnectedSsid.postValue(ssid);
                    mProgressMessageRes.postValue(0);
                    mToastMessage.postValue(getApplication().getString(R.string.wifi_connect_success));
                }

                @Override
                public void onFail(String msg) {
                    mConnectedSsid.postValue(null);
                    mProgressMessageRes.postValue(0);
                    mToastMessage.postValue(getApplication().getString(R.string.wifi_connect_fail, msg));
                }
            });
        } else {
            mWifiCtl.connectWifiBeforeQ(ssid, password);
            mConnectedSsid.postValue(ssid);
            mProgressMessageRes.postValue(0);
            mToastMessage.postValue(getApplication().getString(R.string.wifi_connecting_legacy));
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void disconnect() {
        mProgressMessageRes.postValue(R.string.wifi_status_disconnecting);
        mConnectedSsid.postValue(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mWifiCtl.disconnectWifiAfterQ();
        } else {
            mWifiCtl.disconnectWifiBeforeQ();
        }
        mProgressMessageRes.postValue(0);
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @Override
    protected void onCleared() {
        super.onCleared();
        mScanHandlerThread.quit();
        disconnect();
    }
}
