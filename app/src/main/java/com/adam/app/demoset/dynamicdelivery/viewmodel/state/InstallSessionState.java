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

package com.adam.app.demoset.dynamicdelivery.viewmodel.state;

import androidx.annotation.RestrictTo;
import com.adam.app.demoset.R;
import com.adam.app.demoset.dynamicdelivery.viewmodel.DynamicDeliveryViewModel;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;

/**
 * Enum representing the various states of a dynamic module installation session.
 * Each state encapsulates its own UI update logic.
 */
public enum InstallSessionState {

    PENDING(SplitInstallSessionStatus.PENDING) {
        @Override
        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        public void updateUi(DynamicDeliveryViewModel viewModel, SplitInstallSessionState sessionState) {
            viewModel.updateStatus(viewModel.getString(R.string.state_pending));
            viewModel.updateProgressVisible(true);
        }
    },

    DOWNLOADING(SplitInstallSessionStatus.DOWNLOADING) {
        @Override
        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        public void updateUi(DynamicDeliveryViewModel viewModel, SplitInstallSessionState sessionState) {
            viewModel.updateStatus(viewModel.getString(R.string.state_downloading));
            viewModel.updateProgressVisible(true);
            int progress = (int) (100 * sessionState.bytesDownloaded() / sessionState.totalBytesToDownload());
            viewModel.updateProgress(progress);
        }
    },

    INSTALLING(SplitInstallSessionStatus.INSTALLING) {
        @Override
        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        public void updateUi(DynamicDeliveryViewModel viewModel, SplitInstallSessionState sessionState) {
            viewModel.updateStatus(viewModel.getString(R.string.state_installing));
            viewModel.updateProgressVisible(true);
        }
    },

    INSTALLED(SplitInstallSessionStatus.INSTALLED) {
        @Override
        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        public void updateUi(DynamicDeliveryViewModel viewModel, SplitInstallSessionState sessionState) {
            viewModel.updateStatus(viewModel.getString(R.string.state_installed));
            viewModel.updateProgressVisible(false);
            viewModel.checkModuleStatus();
            viewModel.addLog(viewModel.getString(R.string.log_module_installed_success));
        }
    },

    FAILED(SplitInstallSessionStatus.FAILED) {
        @Override
        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        public void updateUi(DynamicDeliveryViewModel viewModel, SplitInstallSessionState sessionState) {
            String label = viewModel.getString(R.string.state_failed, sessionState.errorCode());
            viewModel.updateStatus(label);
            viewModel.updateProgressVisible(false);
            viewModel.addLog(viewModel.getString(R.string.log_module_install_failed, sessionState.errorCode()));
        }
    },

    CANCELED(SplitInstallSessionStatus.CANCELED) {
        @Override
        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        public void updateUi(DynamicDeliveryViewModel viewModel, SplitInstallSessionState sessionState) {
            viewModel.updateStatus(viewModel.getString(R.string.state_canceled));
            viewModel.updateProgressVisible(false);
        }
    },

    UNKNOWN(-1) {
        @Override
        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        public void updateUi(DynamicDeliveryViewModel viewModel, SplitInstallSessionState sessionState) {
            String label = viewModel.getString(R.string.state_unknown, sessionState.status());
            viewModel.updateStatus(label);
            viewModel.updateProgressVisible(false);
        }
    };

    private final int mStatus;

    InstallSessionState(int status) {
        this.mStatus = status;
    }

    /**
     * Maps a Play Core session status code to an InstallSessionState.
     * @param status The status code from SplitInstallSessionState.
     * @return The corresponding InstallSessionState enum.
     */
    public static InstallSessionState from(int status) {
        for (InstallSessionState state : values()) {
            if (state.mStatus == status) {
                return state;
            }
        }
        return UNKNOWN;
    }

    /**
     * Updates the ViewModel's UI state based on the current session status.
     * @param viewModel The ViewModel to update.
     * @param sessionState The raw session state from Play Core.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public abstract void updateUi(DynamicDeliveryViewModel viewModel, SplitInstallSessionState sessionState);
}
