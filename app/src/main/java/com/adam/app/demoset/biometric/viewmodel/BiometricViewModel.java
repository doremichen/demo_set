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

package com.adam.app.demoset.biometric.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.R;

/**
 * BiometricViewModel
 *
 * ViewModel for Biometric Authentication.
 * It manages the availability state and authentication events.
 * This class is decoupled from Activity/Fragment and Toast logic.
 */
public class BiometricViewModel extends AndroidViewModel {

    private final MutableLiveData<String> mAuthStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mCanAuthenticate = new MutableLiveData<>();
    private final MutableLiveData<Void> mAuthRequestEvent = new MutableLiveData<>();
    private BiometricPrompt.PromptInfo mPromptInfo;

    public BiometricViewModel(@NonNull Application application) {
        super(application);
        checkBiometricAvailability();
    }

    public LiveData<String> getAuthStatus() {
        return mAuthStatus;
    }

    public LiveData<Boolean> getCanAuthenticate() {
        return mCanAuthenticate;
    }

    public LiveData<Void> getAuthRequestEvent() {
        return mAuthRequestEvent;
    }

    public BiometricPrompt.PromptInfo getPromptInfo() {
        if (mPromptInfo == null) {
            setupBiometricPrompt();
        }
        return mPromptInfo;
    }

    /**
     * Enum for Biometric status strategy
     */
    public enum BiometricStatusStrategy {
        SUCCESS(BiometricManager.BIOMETRIC_SUCCESS, R.string.biometric_status_success, true),
        NO_HARDWARE(BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE, R.string.biometric_status_no_hardware, false),
        HW_UNAVAILABLE(BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE, R.string.biometric_status_hw_unavailable, false),
        NONE_ENROLLED(BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED, R.string.biometric_status_none_enrolled, false),
        SECURITY_UPDATE(BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED, R.string.biometric_status_security_update, false),
        UNKNOWN(BiometricManager.BIOMETRIC_STATUS_UNKNOWN, R.string.biometric_status_unknown, false),
        UNEXPECTED(-1, R.string.biometric_status_unexpected, false);

        private final int mResultCode;
        private final int mStringResId;
        private final boolean mCanAuth;

        BiometricStatusStrategy(int resultCode, int stringResId, boolean canAuth) {
            this.mResultCode = resultCode;
            this.mStringResId = stringResId;
            this.mCanAuth = canAuth;
        }

        public static BiometricStatusStrategy from(int result) {
            for (BiometricStatusStrategy strategy : values()) {
                if (strategy.mResultCode == result) {
                    return strategy;
                }
            }
            return UNEXPECTED;
        }

        public void apply(MutableLiveData<String> authStatus, MutableLiveData<Boolean> canAuthenticate, Context context) {
            authStatus.setValue(context.getString(mStringResId));
            canAuthenticate.setValue(mCanAuth);
        }
    }

    public void checkBiometricAvailability() {
        BiometricManager biometricManager = BiometricManager.from(getApplication());
        int result = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG 
                | BiometricManager.Authenticators.DEVICE_CREDENTIAL);

        BiometricStatusStrategy strategy = BiometricStatusStrategy.from(result);
        strategy.apply(mAuthStatus, mCanAuthenticate, getApplication());
    }

    private void setupBiometricPrompt() {
        mPromptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getApplication().getString(R.string.biometric_prompt_title))
                .setSubtitle(getApplication().getString(R.string.biometric_prompt_subtitle))
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG 
                        | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();
    }

    public void onAuthenticateClick() {
        mAuthRequestEvent.setValue(null);
    }

    public void updateStatus(String status) {
        mAuthStatus.setValue(status);
    }
}
