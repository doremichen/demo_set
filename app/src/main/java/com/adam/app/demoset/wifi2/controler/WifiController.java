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

package com.adam.app.demoset.wifi2.controler;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.WIFI_SERVICE;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;

import com.adam.app.demoset.utils.Utils;

import java.util.List;

public class WifiController {

    private final ConnectivityManager mConnectMgr;
    private final WifiManager mWifiManager;
    private ConnectivityManager.NetworkCallback mNetWorkCallback;
    private WIFISTATE mState = WIFISTATE.DISCONNECTED;

    public WifiController(Context context) {
        this.mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        this.mConnectMgr = (ConnectivityManager) context.getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public String getConnectedSsid() {
        if (this.mWifiManager != null && this.mWifiManager.getConnectionInfo() != null) {
            String ssid = this.mWifiManager.getConnectionInfo().getSSID();
            if (ssid != null && ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            if ("<unknown ssid>".equals(ssid)) return null;
            return ssid;
        }
        return null;
    }

    public boolean isWifiEnabled() {
        return mWifiManager != null && mWifiManager.isWifiEnabled();
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void triggerScan() {
        Utils.info(this, "triggerScan");
        if (this.mWifiManager != null) {
            this.mWifiManager.startScan();
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public List<ScanResult> getScanResults() {
        Utils.info(this, "getScanResults");
        return this.mWifiManager != null ? this.mWifiManager.getScanResults() : null;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void connectWifiAfterQ(String ssid, String password, @NonNull ConnectListener listener) {
        Utils.info(this, "connectWifiAfterQ: " + ssid);
        
        if (this.mState == WIFISTATE.CONNECTED) {
            Utils.info(this, "Already connected to wifi!!!");
            return;
        }

        unregisterCallbackInternal();

        WifiNetworkSpecifier specifier = new WifiNetworkSpecifier.Builder()
                .setSsid(ssid)
                .setWpa2Passphrase(password)
                .build();

        NetworkRequest request =
                new NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED)
                        .setNetworkSpecifier(specifier)
                        .build();

        this.mNetWorkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                Utils.info(this, "onAvailable");
                if (WifiController.this.mConnectMgr != null) {
                    WifiController.this.mConnectMgr.bindProcessToNetwork(network);
                }
                WifiController.this.mState = WIFISTATE.CONNECTED;
                listener.onSuccess();
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();
                Utils.info(this, "onUnavailable");
                WifiController.this.mState = WIFISTATE.DISCONNECTED;
                listener.onFail("network unavailable!!!");
                unregisterCallbackInternal();
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                Utils.info(this, "onLost");
                WifiController.this.mState = WIFISTATE.DISCONNECTED;
                if (WifiController.this.mConnectMgr != null) {
                    WifiController.this.mConnectMgr.bindProcessToNetwork(null);
                }
            }
        };

        Utils.info(this, "requestNetwork started");
        this.mConnectMgr.requestNetwork(request, mNetWorkCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void disconnectWifiAfterQ() {
        Utils.info(this, "disconnectWifiAfterQ");
        unregisterCallbackInternal();
        this.mState = WIFISTATE.DISCONNECTED;
    }

    private void unregisterCallbackInternal() {
        if (this.mNetWorkCallback != null && this.mConnectMgr != null) {
            Utils.info(this, "unregistering old callback");
            try {
                this.mConnectMgr.unregisterNetworkCallback(this.mNetWorkCallback);
                this.mConnectMgr.bindProcessToNetwork(null);
            } catch (Exception e) {
                Utils.error(this, "Unregister failed: " + e.getMessage());
            }
            this.mNetWorkCallback = null;
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void connectWifiBeforeQ(@NonNull String ssid, @NonNull String password) {
        if (this.mState == WIFISTATE.CONNECTED) {
            Utils.info(this, "Has connected to wifi!!!");
            return;
        }
        
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + ssid + "\"";
        config.preSharedKey = "\"" + password + "\"";
        config.status = WifiConfiguration.Status.ENABLED;
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

        int netId = this.mWifiManager.addNetwork(config);
        if (netId != -1) {
            this.mWifiManager.disconnect();
            this.mWifiManager.enableNetwork(netId, true);
            this.mWifiManager.reconnect();
            this.mState = WIFISTATE.CONNECTED;
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void disconnectWifiBeforeQ() {
        this.mWifiManager.disconnect();
        this.mState = WIFISTATE.DISCONNECTED;
    }

    public enum WIFISTATE {
        CONNECTED,
        DISCONNECTED
    }

    public interface ConnectListener {
        void onSuccess();
        void onFail(String msg);
    }
}
