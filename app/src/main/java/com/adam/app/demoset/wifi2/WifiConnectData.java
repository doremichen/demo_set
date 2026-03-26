/**
 * Copyright (C) Adam demo app Project. All rights reserved.
 * <p>
 * Description: This class is the Wifi connect data
 * </p>
 * <p>
 * Author: Adam Chen
 * Date: 2025/10/07
 */
package com.adam.app.demoset.wifi2;

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
        String stb = "\n===================================\n" +
                "ssid: " + this.mSsid + "\n" +
                "password: " + this.mPassword + "\n" +
                "===================================\n";
        return stb;
    }
}
