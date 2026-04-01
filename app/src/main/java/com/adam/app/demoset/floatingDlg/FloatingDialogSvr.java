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

package com.adam.app.demoset.floatingDlg;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.WindowManager;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;

public class FloatingDialogSvr extends Service {

    public static final String ACTION_SHOW_FLOATING_DIALOG = "show floating dialog";
    private AlertDialog mDialog;

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.info(this, "onCreate");
        // set system alert window type
        mDialog = this.floatingDialog(this.getApplicationContext());
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.info(this, "onStartCommand 11");
        if (ACTION_SHOW_FLOATING_DIALOG.equals(intent.getAction())) {
            mDialog.show();
        }

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.info(this, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private AlertDialog floatingDialog(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.demo_floating_system_dialog);
        builder.setMessage(R.string.demo_floating_system_dialog_description);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.label_ok_btn), (dialog, which) -> dialog.cancel());

        return builder.create();

    }
}
