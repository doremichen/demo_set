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

import static android.content.Context.WIFI_SERVICE;

import android.Manifest;
import android.content.Context;
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

    private final WifiManager mWifiManager;

    public WifiController(Context context) {
        this.mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
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
    public NetworkRequest createNetworkRequest(String ssid, String password, String capabilities) {
        Utils.info(this, "createNetworkRequest for: " + ssid);
        
        WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder()
                .setSsid(ssid);

        if (capabilities != null) {
            if (capabilities.contains("WPA3")) {
                builder.setWpa3Passphrase(password);
            } else if (capabilities.contains("WPA2") || capabilities.contains("WPA")) {
                builder.setWpa2Passphrase(password);
            }
        }

        return new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED)
                .setNetworkSpecifier(builder.build())
                .build();
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void connectWifiBeforeQ(@NonNull String ssid, @NonNull String password, String capabilities) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + ssid + "\"";
        
        if (capabilities != null && (capabilities.contains("WPA2") || capabilities.contains("WPA"))) {
            config.preSharedKey = "\"" + password + "\"";
            config.status = WifiConfiguration.Status.ENABLED;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        } else {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        int netId = this.mWifiManager.addNetwork(config);
        if (netId != -1) {
            this.mWifiManager.disconnect();
            this.mWifiManager.enableNetwork(netId, true);
            this.mWifiManager.reconnect();
        }
    }

    public void disconnectWifiBeforeQ() {
        if (this.mWifiManager != null) {
            this.mWifiManager.disconnect();
        }
    }
}
