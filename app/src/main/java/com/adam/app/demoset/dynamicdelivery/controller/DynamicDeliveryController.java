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

package com.adam.app.demoset.dynamicdelivery.controller;

import android.content.Context;

import com.adam.app.demoset.utils.DemoAppConstants;
import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;

import java.util.Collections;

public class DynamicDeliveryController {

    private static final String sMODULE_NAME = DemoAppConstants.MODULE_NAME_DYNAMIC;
    private final SplitInstallManager mSplitInstallManager;

    public interface OnControllerCallback {
        void onSessionIdReceived(int sessionId);
        void onFailure(String errorMessage);
        void onStateUpdate(SplitInstallSessionState state);
        void onUninstallRequested();
    }

    public DynamicDeliveryController(Context context) {
        mSplitInstallManager = SplitInstallManagerFactory.create(context);
    }

    public void registerListener(SplitInstallStateUpdatedListener listener) {
        mSplitInstallManager.registerListener(listener);
    }

    public void unregisterListener(SplitInstallStateUpdatedListener listener) {
        mSplitInstallManager.unregisterListener(listener);
    }

    public boolean isModuleInstalled() {
        return mSplitInstallManager.getInstalledModules().contains(sMODULE_NAME);
    }

    public String getModuleName() {
        return sMODULE_NAME;
    }

    public void installModule(OnControllerCallback callback) {
        SplitInstallRequest request = SplitInstallRequest.newBuilder()
                .addModule(sMODULE_NAME)
                .build();

        mSplitInstallManager.startInstall(request)
                .addOnSuccessListener(callback::onSessionIdReceived)
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void uninstallModule(OnControllerCallback callback) {
        mSplitInstallManager.deferredUninstall(Collections.singletonList(sMODULE_NAME))
                .addOnSuccessListener(v -> callback.onUninstallRequested())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}
