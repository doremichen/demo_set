/**
 * Wifi input data
 */
package com.adam.app.demoset.wifi2;

import android.text.TextUtils;

import androidx.annotation.NonNull;

class WifiConnectData {

    private String mSsid;
    private String mPassword;

    WifiConnectData(String ssid, String password) {
        this.mSsid = ssid;
        this.mPassword = password;
    }

    public void clear() {
        this.mSsid = null;
        this.mPassword = null;
    }

    public String getSsid() {
        return this.mSsid;
    }

    public String getPassword() {
        return this.mPassword;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stb = new StringBuilder();
        stb.append("\n===================================\n");
        stb.append("ssid: ").append(this.mSsid).append("\n");
        stb.append("password: ").append(this.mPassword).append("\n");
        stb.append("===================================\n");
        return stb.toString();
    }
}
