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

package com.adam.app.demoset.biometric;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.biometric.viewmodel.BiometricViewModel;
import com.adam.app.demoset.databinding.ActivityDemoBiometricBinding;
import com.adam.app.demoset.utils.Utils;

import java.util.concurrent.Executor;

/**
 * DemoBiometricAct
 *
 * This activity handles the UI for Biometric Authentication.
 * It manages BiometricPrompt lifecycle and Toast messages, keeping the ViewModel clean.
 */
public class DemoBiometricAct extends AppCompatActivity {

    private BiometricViewModel mViewModel;
    private BiometricPrompt mBiometricPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDemoBiometricBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_demo_biometric);

        mViewModel = new ViewModelProvider(this).get(BiometricViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        initBiometricPrompt();

        // Observe authentication request event from ViewModel
        mViewModel.getAuthRequestEvent().observe(this, unused -> 
                mBiometricPrompt.authenticate(mViewModel.getPromptInfo()));
    }

    private void initBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        mBiometricPrompt = new BiometricPrompt(this, executor, 
                new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                handleAuthResult(R.string.biometric_auth_error, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                handleAuthResult(R.string.biometric_auth_success, null);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                handleAuthResult(R.string.biometric_auth_failed, null);
            }
        });
    }

    private void handleAuthResult(int resId, CharSequence extra) {
        String msg = (extra != null) ? getString(resId, extra) : getString(resId);
        mViewModel.updateStatus(msg);
        Utils.showToast(this, msg);
    }
}
