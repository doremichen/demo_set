/*
 * Copyright (c) 2024 Adam Chen
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

package com.adam.app.demoset.encryption.sharedprefs.controller;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

public class EncryptedPrefsManager {

    private static final String ENCRYPTED_PREFS_NAME = "encrypted_demo_settings";
    private static final String NORMAL_PREFS_NAME = "normal_demo_settings";

    private final SharedPreferences encryptedPrefs;
    private final SharedPreferences normalPrefs;
    private final SharedPreferences rawEncryptedPrefs; // 用於展示加密後的亂碼

    public EncryptedPrefsManager(Context context) throws GeneralSecurityException, IOException {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

        encryptedPrefs = EncryptedSharedPreferences.create(
                ENCRYPTED_PREFS_NAME,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        normalPrefs = context.getSharedPreferences(NORMAL_PREFS_NAME, Context.MODE_PRIVATE);
        
        // 使用標準模式開啟同一個檔案，這會讓我們看到加密後的內容
        rawEncryptedPrefs = context.getSharedPreferences(ENCRYPTED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveToEncrypted(String key, String value) {
        encryptedPrefs.edit().putString(key, value).apply();
    }

    public void saveToNormal(String key, String value) {
        normalPrefs.edit().putString(key, value).apply();
    }

    public String getFromEncrypted(String key) {
        return encryptedPrefs.getString(key, "");
    }

    public String getFromNormal(String key) {
        return normalPrefs.getString(key, "");
    }

    /**
     * 讀取儲存在 XML 裡的原始加密字串 (Hack 角度)
     */
    public String getRawEncryptedString(String key) {
        // 因為 Key 也是被加密過的，我們必須掃描整個 Map 找到對應的加密 Key
        // 這只是為了 Demo 展示駭客看到的內容
        Map<String, ?> allEntries = rawEncryptedPrefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            // 這裡隨便回傳一個，實際演示時建議直接顯示整個 Map 的內容或對應值
            return entry.getValue().toString();
        }
        return "No Data / File is Encrypted";
    }

    public void clearAll() {
        encryptedPrefs.edit().clear().apply();
        normalPrefs.edit().clear().apply();
    }
}
