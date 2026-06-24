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
package com.adam.app.demoset.encryption.database.controller;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import com.adam.app.demoset.utils.DemoAppConstants;
import com.adam.app.demoset.utils.Utils;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class CryptoManager {
    private static final String TAG = "CryptoManager";
    private static final String ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    private static final String BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM;
    private static final String PADDING = KeyProperties.ENCRYPTION_PADDING_NONE;
    private static final String TRANSFORMATION = ALGORITHM + "/" + BLOCK_MODE + "/" + PADDING;
    private static final String KEY_ALIAS = DemoAppConstants.KEY_ALIAS_ENCRYPTION;
    private static final String ANDROID_KEYSTORE = DemoAppConstants.KEYSTORE_ANDROID;

    private KeyStore mKeyStore;

    public CryptoManager() {
        try {
            mKeyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            mKeyStore.load(null);
        } catch (Exception e) {
            Utils.error(TAG, "Failed to initialize KeyStore: " + e);
        }
    }

    private SecretKey getOrCreateKey() throws Exception {
        if (mKeyStore.containsAlias(KEY_ALIAS)) {
            KeyStore.Entry entry = mKeyStore.getEntry(KEY_ALIAS, null);
            if (entry instanceof KeyStore.SecretKeyEntry) {
                return ((KeyStore.SecretKeyEntry) entry).getSecretKey();
            }
        }
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM, ANDROID_KEYSTORE);
        keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(BLOCK_MODE)
                .setEncryptionPaddings(PADDING)
                .build());
        return keyGenerator.generateKey();
    }

    public EncryptedData encrypt(String text) {
        if (text == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey());
            byte[] iv = cipher.getIV();
            byte[] encryptedBytes = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return new EncryptedData(
                    Base64.encodeToString(encryptedBytes, Base64.NO_WRAP),
                    Base64.encodeToString(iv, Base64.NO_WRAP));
        } catch (Exception e) {
            Utils.error(TAG, "Encryption failed: " + e);
            return null;
        }
    }

    public String decrypt(String encryptedText, String iv) {
        if (encryptedText == null || iv == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(128, Base64.decode(iv, Base64.NO_WRAP));
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), spec);
            byte[] decodedBytes = Base64.decode(encryptedText, Base64.NO_WRAP);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Utils.error(TAG, "Decryption failed: " + e);
            return "Decryption Error";
        }
    }

    public static class EncryptedData {
        public final String data;
        public final String iv;
        public EncryptedData(String data, String iv) { this.data = data; this.iv = iv; }
    }
}


