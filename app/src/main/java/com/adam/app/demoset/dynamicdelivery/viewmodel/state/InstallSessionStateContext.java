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

import com.adam.app.demoset.dynamicdelivery.viewmodel.DynamicDeliveryViewModel;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;

/**
 * Context class for the State Pattern.
 * Manages the current state and delegates the UI update logic.
 */
public class InstallSessionStateContext {

    private InstallSessionState mCurrentState;

    /**
     * Handles the incoming session state from Play Core.
     * @param viewModel The ViewModel to update.
     * @param sessionState The raw session state.
     */
    public void handle(DynamicDeliveryViewModel viewModel, SplitInstallSessionState sessionState) {
        mCurrentState = InstallSessionState.from(sessionState.status());
        // state check
        if (mCurrentState == InstallSessionState.UNKNOWN) {
            viewModel.addLog("Unknown state: " + sessionState.status());
            return;
        }

        mCurrentState.updateUi(viewModel, sessionState);
    }

    /**
     * Optional: manually set a state (if needed for testing or internal overrides).
     * @param state The state to set.
     */
    public void setState(InstallSessionState state) {
        this.mCurrentState = state;
    }

    /**
     * Gets the current identified state.
     * @return The current InstallSessionState.
     */
    public InstallSessionState getCurrentState() {
        return mCurrentState;
    }
}
