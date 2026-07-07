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

package com.adam.app.demoset.systemUI.domain.usecase;

import com.adam.app.demoset.R;
import com.adam.app.demoset.systemUI.domain.model.SystemUIStatus;

import javax.inject.Inject;

/**
 * Use case to toggle System UI modes and update status.
 */
public class ToggleSystemUIModeUseCase {

    @Inject
    public ToggleSystemUIModeUseCase() {
    }

    /**
     * Executes the toggle logic.
     *
     * @param isLowProfile  current low profile state
     * @param isImmersive   current immersive state
     * @param toggleDim     whether to toggle dim mode
     * @param toggleHide    whether to toggle hide mode
     * @param normalMsg     localized normal state message
     * @param lowLightMsg   localized low light mode message
     * @param immersiveMsg  localized immersive mode message
     * @return updated SystemUIStatus
     */
    public SystemUIStatus execute(boolean isLowProfile, boolean isImmersive, boolean toggleDim, boolean toggleHide,
                                  String normalMsg, String lowLightMsg, String immersiveMsg) {

        boolean nextLowProfile = toggleDim != isLowProfile;
        boolean nextImmersive = toggleHide != isImmersive;

        String message;
        int colorAttr;
        int dimBtnTextRes;
        int hideBtnTextRes;

        if (nextImmersive) {
            message = immersiveMsg;
            colorAttr = androidx.appcompat.R.attr.colorError;
            dimBtnTextRes = R.string.demo_system_ui_hide_low_light_btn;
            hideBtnTextRes = R.string.action_show_system_ui;
        } else if (nextLowProfile) {
            message = lowLightMsg;
            colorAttr = com.google.android.material.R.attr.colorTertiary;
            dimBtnTextRes = R.string.action_show_system_ui;
            hideBtnTextRes = R.string.demo_system_ui_hide_invisible;
        } else {
            message = normalMsg;
            colorAttr = androidx.appcompat.R.attr.colorPrimary;
            dimBtnTextRes = R.string.demo_system_ui_hide_low_light_btn;
            hideBtnTextRes = R.string.demo_system_ui_hide_invisible;
        }

        return new SystemUIStatus(nextLowProfile, nextImmersive, message, colorAttr, dimBtnTextRes, hideBtnTextRes);
    }
}
