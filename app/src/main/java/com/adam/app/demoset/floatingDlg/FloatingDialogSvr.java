package com.adam.app.demoset.floatingDlg;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.view.WindowManager;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

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
