/**
 * Handle wifi procedure
 */
package com.adam.app.demoset.wifi2;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.WIFI_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.adam.app.demoset.Utils;

import java.util.List;

class WifiController {

    private ConnectivityManager mConnectMgr;
    private WifiManager mWifiManager;
    private ConnectivityManager.NetworkCallback mNetWorkCallnack;

    private enum WIFISTATE {
        CONNECTED,
        DISCONNECTED;
    }

    private WIFISTATE mState = WIFISTATE.DISCONNECTED;


    public WifiController(Context context) {
        this.mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        this.mConnectMgr = (ConnectivityManager) context.getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
    }


    interface WifiScanListener {
        void onUpdateWifiList(List<ScanResult> list);
    }

    public void wifiScan(@NonNull WifiScanListener listener) {
        Utils.info(this, "wifiScan");
        // enable wifi
        this.mWifiManager.setWifiEnabled(true);
        // start scan
        this.mWifiManager.startScan();
        // get wifi list
        List<ScanResult> list = this.mWifiManager.getScanResults();
        Utils.info(this, "list: " + list.toString());
        // notify ui
        listener.onUpdateWifiList(list);
    }

    interface ConnectListener {
        void onSuccess();

        void onFail(String msg);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void connectWifiAfterQ(String ssid, String password, @NonNull ConnectListener listener) {
        Utils.info(this, "connectWifiAfterQ");
        if (this.mState == WIFISTATE.CONNECTED) {
            Utils.info(this, "Has connected to wifi!!!");
            return;
        }

        // reference: https://developer.android.com/reference/android/net/wifi/WifiManager#reconnect()
        WifiNetworkSpecifier specifier = new WifiNetworkSpecifier.Builder()
                .setSsid(ssid)
                .setWpa2Passphrase(password)
                .build();

        NetworkRequest request =
                new NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED)
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
                        .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                        .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)
                        .removeCapability(NetworkCapabilities.NET_CAPABILITY_FOREGROUND)
                        .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_CONGESTED)
                        .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED)
                        .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_ROAMING)
                        .setNetworkSpecifier(specifier)
                        .build();
        // connect to ap
        this.mNetWorkCallnack = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                Utils.info(this, "onAvailable");
                assert WifiController.this.mConnectMgr != null;
                // bind to ap
                WifiController.this.mConnectMgr.bindProcessToNetwork(network);
                // notify ui
                listener.onSuccess();
                //
                WifiController.this.mState = WIFISTATE.CONNECTED;
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();
                Utils.info(this, "onUnavailable");
                // notify ui
                listener.onFail("network unavailable!!!");
                //
                WifiController.this.mState = WIFISTATE.DISCONNECTED;

            }
        };
        Utils.info(this, "requestNetwork");
        this.mConnectMgr.requestNetwork(request, mNetWorkCallnack);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void disconnectWifiAfterQ() {
        if (this.mState == WIFISTATE.DISCONNECTED) {
            Utils.info(this, "Has disconnected to wifi!!!");
            return;
        }

        assert this.mConnectMgr != null;
        assert this.mNetWorkCallnack != null;
        this.mConnectMgr.unregisterNetworkCallback(this.mNetWorkCallnack);
        this.mState = WIFISTATE.DISCONNECTED;
    }

    public void connectWifiBeforeQ(@NonNull String ssid, @NonNull String password) {
        if (this.mState == WIFISTATE.CONNECTED) {
            Utils.info(this, "Has connected to wifi!!!");
            return;
        }
        String ssidStr = TextUtils.concat("\"", ssid, "\"").toString();
        String passwordStr = TextUtils.concat("\"", password, "\"").toString();
        // wifi config
        WifiConfiguration config = new WifiConfiguration();
        config.allowedProtocols.clear();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.SSID = ssid;
        config.preSharedKey = password;
        config.status = WifiConfiguration.Status.ENABLED;
        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

        // add config by wifi manager
        this.mWifiManager.addNetwork(config);
        // connect to ap
        List<WifiConfiguration> list = this.mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration configuration : list) {
            // get the assigned ssid ap and connect
            if (ssid.equals(configuration.SSID)) {
                // disconnect
                this.mWifiManager.disconnect();
                // connect
                this.mWifiManager.enableNetwork(configuration.networkId, true);
                this.mWifiManager.reconnect();
                this.mState = WIFISTATE.CONNECTED;
                break;
            }
        }
    }

    public void disconnectWifiBeforeQ() {
        if (this.mState == WIFISTATE.DISCONNECTED) {
            Utils.info(this, "Has disconnected to wifi!!!");
            return;
        }
        assert this.mWifiManager != null;
        List<WifiConfiguration> list = this.mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration config: list) {
            this.mWifiManager.removeNetwork(config.networkId);
        }
        this.mWifiManager.disconnect();
        this.mState = WIFISTATE.DISCONNECTED;
    }

}
