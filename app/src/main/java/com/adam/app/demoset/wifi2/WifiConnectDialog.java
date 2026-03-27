/**
 * Copyright (C) Adam demo app Project. All rights reserved.
 * <p>
 * Description: This class is the Wifi connect dialog.
 * </p>
 * <p>
 * Author: Adam Chen
 * Date: 2025/10/07
 */
package com.adam.app.demoset.wifi2;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
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
