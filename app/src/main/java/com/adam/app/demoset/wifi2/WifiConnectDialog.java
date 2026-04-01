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

package com.adam.app.demoset.wifi2;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.databinding.DialogEditWifiBinding;

class WifiConnectDialog {
    private final LayoutInflater mInflater;
    private final ScanResult mResult;
    private final DialogListener mListener;
    // view binding
    private DialogEditWifiBinding mBinding;


    WifiConnectDialog(Context ctx, ScanResult result, @NonNull DialogListener listener) {
        // initial dialog
        this.mInflater = LayoutInflater.from(ctx);
        this.mResult = result;
        this.mListener = listener;
    }

    public AlertDialog create() {
        Utils.info(this, "create enter");
        // view binding
        this.mBinding = DialogEditWifiBinding.inflate(this.mInflater);
        // set ssid
        mBinding.setSsid(mResult.SSID);
        // update ui
        mBinding.executePendingBindings();

        // create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mInflater.getContext(), androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert);
        builder.setView(this.mBinding.getRoot());
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();


        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // set ok button
        mBinding.btnConnect.setOnClickListener(v -> {
            // get password
            String password = mBinding.editWifiPassword.getText().toString();
            WifiConnectData data = new WifiConnectData(mResult.SSID, password);
            if (mListener != null) {
                mListener.onResult(data);
            }
            dialog.dismiss();
        });

        // set cancel button
        mBinding.btnCancel.setOnClickListener(v -> {
            dialog.cancel();
        });
        return dialog;
    }

    interface DialogListener {
        void onResult(WifiConnectData data);
    }
}
