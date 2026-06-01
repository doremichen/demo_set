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

public class WifiViewModel extends AndroidViewModel implements WifiController.WifiScanListener {

    private static final int SCAN_WIFI = 1;
    private final WifiController mWifiCtl;
    private final MutableLiveData<List<ScanResult>> mWifiList = new MutableLiveData<>();
    private final MutableLiveData<String> mToastMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsScanning = new MutableLiveData<>(false);
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
                    Utils.info(this, "start to scan@handleMessage");
                    mIsScanning.postValue(true);
                    mWifiCtl.wifiScan(WifiViewModel.this);
                }
            }
        };
    }

    public LiveData<List<ScanResult>> getWifiList() {
        return mWifiList;
    }

    public LiveData<String> getToastMessage() {
        return mToastMessage;
    }

    public LiveData<Boolean> isScanning() {
        return mIsScanning;
    }

    public LiveData<String> getConnectedSsid() {
        return mConnectedSsid;
    }

    public void startScan() {
        mHandler.sendEmptyMessage(SCAN_WIFI);
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void connectWifi(String ssid, String password) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mWifiCtl.connectWifiAfterQ(ssid, password, new WifiController.ConnectListener() {
                @Override
                public void onSuccess() {
                    mConnectedSsid.postValue(ssid);
                    mToastMessage.postValue(getApplication().getString(R.string.wifi_connect_success));
                }

                @Override
                public void onFail(String msg) {
                    mConnectedSsid.postValue(null);
                    mToastMessage.postValue(getApplication().getString(R.string.wifi_connect_fail, msg));
                }
            });
        } else {
            mWifiCtl.connectWifiBeforeQ(ssid, password);
            mConnectedSsid.postValue(ssid); // In before Q, it's usually immediate in this simple impl
            mToastMessage.postValue(getApplication().getString(R.string.wifi_connecting_legacy));
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void disconnect() {
        mConnectedSsid.postValue(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mWifiCtl.disconnectWifiAfterQ();
        } else {
            mWifiCtl.disconnectWifiBeforeQ();
        }
    }

    @Override
    public void onUpdateWifiList(List<ScanResult> list) {
        mWifiList.postValue(list);
        mIsScanning.postValue(false);
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @Override
    protected void onCleared() {
        super.onCleared();
        mScanHandlerThread.quit();
        disconnect();
    }
}
