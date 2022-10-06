//
// Wifi control model
//
package com.adam.app.demoset.wifi;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

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
        return Helper.INSTANCE;
    }

    //
    // initialization
    //
    public void init(Context context, CallBack cb) {
        Utils.info(this, "[init]");
        mWifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mCB = cb;
    }

    //
    // start wifi scan
    // note: This method startScan of WifiManager was deprecated in API level 28.
    // The ability for apps to trigger scan requests will be removed in a future release.
    //
    public boolean startScan(Context ctx) {
        Utils.info(this, "[startScan] enter");
        boolean ret = false;

        // Check wifi enable
        if (!mWifimanager.isWifiEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ctx.startActivity(new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY));
            } else {
                mWifimanager.setWifiEnabled(true);
                mCB.onInfo("Wifi function is enabled");
            }
        }

        // Start scan
        mWifimanager.startScan();

        Utils.info(this, "[startScan] exit ret[" + ret + "]");
        return ret;
    }

    //
    // Connect to AP
    //
    public void connectToAP(String ssid, String pass) {
        Utils.info(this, "[connectToAP] enter");
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

        Utils.info(this, "[connectToAP] exit");
    }

    //
    // Get Scan result
    //
    public List<ScanResult> getResult() {
        Utils.info(this, "[getResult] enter");
        if (mWifimanager == null) {
            Utils.info(this, "mWifimanager is null");
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
        Utils.info(this, "[getWifiConfig] enter");
        List<WifiConfiguration> configList = mWifimanager.getConfiguredNetworks();
        for (WifiConfiguration config : configList) {
            // Get the wifi configuration
            if (config.SSID != null && config.SSID.equals(ssid)) {
                return config;
            }
        }
        Utils.info(this, "[getWifiConfig] exit");
        return null;
    }

    //
    // Create wifi configuration
    //
    private void createWPAProfile(String ssid, String pass) {
        Utils.info(this, "[createWPAProfile] enter");
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = ssid;
        config.preSharedKey = pass;
        // Add config to netWork
        mWifimanager.addNetwork(config);
        Utils.info(this, "[createWPAProfile] exit");
    }


    interface CallBack {
        void onInfo(String msg);
    }


}
