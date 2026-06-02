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
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.adam.app.demoset.databinding.DialogEditWifiBinding;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.wifi2.model.WifiConnectData;

public abstract class BaseWifiDialog {
    protected final Context mContext;
    protected final ScanResult mScanResult;
    protected final DialogListener mListener;
    protected DialogEditWifiBinding mBinding;

    public interface DialogListener {
        void onResult(WifiConnectData data);
    }

    public BaseWifiDialog(Context context, ScanResult scanResult, @NonNull DialogListener listener) {
        this.mContext = context;
        this.mScanResult = scanResult;
        this.mListener = listener;
    }

    /**
     * Template Method
     */
    public final AlertDialog create() {
        Utils.info(this, "create dialog: " + getClass().getSimpleName());
        
        mBinding = DialogEditWifiBinding.inflate(LayoutInflater.from(mContext));
        mBinding.setSsid(mScanResult != null ? mScanResult.SSID : "");
        
        // 1. Setup specific content (Title, Visibility of fields)
        setupContent();
        
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert);
        builder.setView(mBinding.getRoot());
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // 2. Setup button listeners
        setupButtons(dialog);
        
        mBinding.executePendingBindings();
        return dialog;
    }

    protected abstract void setupContent();
    protected abstract void setupButtons(AlertDialog dialog);
}
