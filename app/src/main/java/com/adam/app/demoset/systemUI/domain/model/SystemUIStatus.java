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

package com.adam.app.demoset.systemUI.domain.model;

/**
 * Domain model representing the System UI status.
 */
public class SystemUIStatus {
    private final boolean mIsLowProfile;
    private final boolean mIsImmersive;
    private final String mMessage;
    private final int mColorAttr;
    private final int mDimBtnTextRes;
    private final int mHideBtnTextRes;

    public SystemUIStatus(boolean isLowProfile, boolean isImmersive, String message, int colorAttr, 
                          int dimBtnTextRes, int hideBtnTextRes) {
        this.mIsLowProfile = isLowProfile;
        this.mIsImmersive = isImmersive;
        this.mMessage = message;
        this.mColorAttr = colorAttr;
        this.mDimBtnTextRes = dimBtnTextRes;
        this.mHideBtnTextRes = hideBtnTextRes;
    }

    public boolean isLowProfile() {
        return mIsLowProfile;
    }

    public boolean isImmersive() {
        return mIsImmersive;
    }

    public String getMessage() {
        return mMessage;
    }

    public int getColorAttr() {
        return mColorAttr;
    }

    public int getDimBtnTextRes() {
        return mDimBtnTextRes;
    }

    public int getHideBtnTextRes() {
        return mHideBtnTextRes;
    }
}
