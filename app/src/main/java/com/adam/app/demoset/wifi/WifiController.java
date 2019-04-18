//
// Wifi control model
//
package com.adam.app.demoset.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.adam.app.demoset.Utils;

import java.util.List;

class WifiController {

    // Wifi manager
    private WifiManager mWifimanager;

    private CallBack mCB;

    private WifiController() {
        // singleton
    }

    private static class Helper {
        private static WifiController INSTANCE = new WifiController();
    }

    //
    // Return object
    //
    public static WifiController newInstance() {
        return  Helper.INSTANCE;
    }

    //
    // initialization
    //
    public void init(Context context, CallBack cb) {
        Utils.inFo(this, "[init]");
        mWifimanager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        mCB = cb;
    }

    //
    // start wifi scan
    // note: This method startScan of WifiManager was deprecated in API level 28.
    // The ability for apps to trigger scan requests will be removed in a future release.
    //
    public boolean startScan() {
        Utils.inFo(this, "[startScan] enter");
        boolean ret = false;

        // Check wifi enable
        if (!mWifimanager.isWifiEnabled()) {
            mWifimanager.setWifiEnabled(true);
            mCB.onInfo("Wifi function is enabled");
        }

        // Start scan
        mWifimanager.startScan();

        Utils.inFo(this, "[startScan] exit ret[" + ret +"]");
        return ret;
    }

    //
    // Connect to AP
    //
    public void connectToAP(String ssid, String pass) {
        Utils.inFo(this, "[connectToAP] enter");
        // Check ssid is already connected
        String getSSID = mWifimanager.getConnectionInfo().getSSID();
        if ((getSSID != null) && (getSSID.equals(ssid))) {
            mCB.onInfo("Already connect to " + ssid);
            return;
        }

        // Get wifi configuration by the ssid
        WifiConfiguration config = getWifiConfig(ssid);
        if (config == null) {
            // Create wifi configuration
            createWPAProfile(ssid, pass);
            config = getWifiConfig(ssid);
        }

        mWifimanager.disconnect();
        mWifimanager.enableNetwork(config.networkId, true);
        mWifimanager.reconnect();

        Utils.inFo(this, "[connectToAP] exit");
    }

    //
    // Get Scan result
    //
    public List<ScanResult> getResult() {
        Utils.inFo(this, "[getResult] enter");
        if (mWifimanager == null) {
            Utils.inFo(this, "mWifimanager is null");
            return null;
        }

        return mWifimanager.getScanResults();
    }

    // ===============================================
    // Subroutine
    // ===============================================

    //
    // Get ssid from wifi configuration
    //
    private WifiConfiguration getWifiConfig(String ssid) {
        Utils.inFo(this, "[getWifiConfig] enter");
        List<WifiConfiguration> configList = mWifimanager.getConfiguredNetworks();
        for (WifiConfiguration config: configList) {
            // Get the wifi configuration
            if (config.SSID != null && config.SSID.equals(ssid)) {
                return config;
            }
        }
        Utils.inFo(this, "[getWifiConfig] exit");
        return null;
    }

    //
    // Create wifi configuration
    //
    private void createWPAProfile(String ssid, String pass) {
        Utils.inFo(this, "[createWPAProfile] enter");
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = ssid;
        config.preSharedKey = pass;
        // Add config to netWork
        mWifimanager.addNetwork(config);
        Utils.inFo(this, "[createWPAProfile] exit");
    }


    interface CallBack {
        void onInfo(String msg);
    }


}
