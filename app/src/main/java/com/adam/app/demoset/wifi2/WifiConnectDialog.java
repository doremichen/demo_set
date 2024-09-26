/**
 * Wifi connect dialog
 */
package com.adam.app.demoset.wifi2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

class WifiConnectDialog {
    private LayoutInflater mInflater;
    private AlertDialog.Builder mAlertBuilder;
    private ScanResult mResult;

    interface DialogListener {
        void onResult(WifiConnectData data);
    }

    private DialogListener mListener;
    WifiConnectDialog(Context ctx, ScanResult result, @NonNull DialogListener listener) {
        // initial dialog
        this.mInflater = LayoutInflater.from(ctx);
        this.mAlertBuilder = new AlertDialog.Builder(ctx, R.style.AppCompatAlertDialogStyle);
        this.mAlertBuilder.setCancelable(false);
        this.mResult = result;
        this.mListener = listener;
    }

    public AlertDialog create() {
        Utils.info(this, "create enter");
        View view = mInflater.inflate(R.layout.dialog_edit_wifi, null);
        this.mAlertBuilder.setView(view);
        // input handler
        final EditText input = view.findViewById(R.id.edit_wifi_password);
        // set positive/negative button
        this.mAlertBuilder.setPositiveButton(R.string.label_ok_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WifiConnectData data = new WifiConnectData(WifiConnectDialog.this.mResult.SSID, input.getText().toString());
                // notify activity
                WifiConnectDialog.this.mListener.onResult(data);
            }
        });
        this.mAlertBuilder.setNegativeButton(R.string.label_cancel_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // dismiss dialog
                dialog.dismiss();
            }
        });

        return this.mAlertBuilder.create();
    }
}
