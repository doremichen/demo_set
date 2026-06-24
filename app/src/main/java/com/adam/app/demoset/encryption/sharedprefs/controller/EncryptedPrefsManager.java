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

import com.adam.app.demoset.utils.DemoAppConstants;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

public class EncryptedPrefsManager {

    private static final String ENCRYPTED_PREFS_NAME = DemoAppConstants.ENCRYPTED_PREFS_NAME;
    private static final String NORMAL_PREFS_NAME = DemoAppConstants.NORMAL_PREFS_NAME;

    private final SharedPreferences mEncryptedPrefs;
    private final SharedPreferences mNormalPrefs;
    private final SharedPreferences mRawEncryptedPrefs; // used for hacker view
    
    public EncryptedPrefsManager(Context context) throws GeneralSecurityException, IOException {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

        mEncryptedPrefs = EncryptedSharedPreferences.create(
                ENCRYPTED_PREFS_NAME,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        mNormalPrefs = context.getSharedPreferences(NORMAL_PREFS_NAME, Context.MODE_PRIVATE);
        
        // Opening the same file in standard mode will allow us to see the encrypted content.
        mRawEncryptedPrefs = context.getSharedPreferences(ENCRYPTED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveToEncrypted(String key, String value) {
        mEncryptedPrefs.edit().putString(key, value).apply();
    }

    public void saveToNormal(String key, String value) {
        mNormalPrefs.edit().putString(key, value).apply();
    }

    public String getFromEncrypted(String key) {
        return mEncryptedPrefs.getString(key, "");
    }

    public String getFromNormal(String key) {
        return mNormalPrefs.getString(key, "");
    }

    /**
     * Read the raw encrypted string stored in XML (from a hacking perspective)
     */
    public String getRawEncryptedString(String key) {
        // Because the Key is also encrypted, we must scan the entire Map to
        // find the corresponding encrypted Key
        // This is just for the demo to show what the hacker sees
        Map<String, ?> allEntries = mRawEncryptedPrefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            // 這裡隨便回傳一個，實際演示時建議直接顯示整個 Map 的內容或對應值
            return entry.getValue().toString();
        }
        return "No Data / File is Encrypted";
    }

    public void clearAll() {
        mEncryptedPrefs.edit().clear().apply();
        mNormalPrefs.edit().clear().apply();
    }
}
