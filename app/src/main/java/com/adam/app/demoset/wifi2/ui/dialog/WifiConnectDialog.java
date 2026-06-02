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

package com.adam.app.demoset.wifi2.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.net.wifi.ScanResult;

import androidx.annotation.NonNull;

import com.adam.app.demoset.R;
import com.adam.app.demoset.wifi2.model.WifiConnectData;

public class WifiConnectDialog extends BaseWifiDialog {

    public WifiConnectDialog(Context ctx, ScanResult result, @NonNull DialogListener listener) {
        super(ctx, result, listener);
    }

    @Override
    protected void setupContent() {
        mBinding.setTitle(mContext.getString(R.string.label_edit_title));
        mBinding.setShowPassword(true);
        mBinding.setButtonText(mContext.getString(R.string.label_ok_btn));
    }

    @Override
    protected void setupButtons(AlertDialog dialog) {
        mBinding.btnConnect.setOnClickListener(v -> {
            String password = mBinding.editWifiPassword.getText().toString();
            if (mListener != null) {
                mListener.onResult(new WifiConnectData(mScanResult.SSID, password, mScanResult.capabilities));
            }
            dialog.dismiss();
        });

        mBinding.btnCancel.setOnClickListener(v -> dialog.cancel());
    }
}
