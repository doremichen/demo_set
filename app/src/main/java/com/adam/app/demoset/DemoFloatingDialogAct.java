package com.adam.app.demoset;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * TODO: Descript the information of this file
 * <p>
 * info:
 *
 * @author: AdamChen
 * @date: 2018/9/27
 */
public class DemoFloatingDialogAct extends AppCompatActivity {

    private static final int REQUEST_ALTER_WINDOW = 0;

    private LinearLayout mLayout;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_demo_floating_dialog);

        mLayout = (LinearLayout) this.findViewById(R.id.demo_fd_layout);

        // set system alert window type
        mDialog = this.floatingDialog(this.getApplicationContext());
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
    }

    public void onStartFlaotingDialog(View v) {

        requestPermission();

    }

    public void onColse(View v) {

        this.finish();
    }

    private void requestPermission() {

        //Guide user to open permission in the settings
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getPackageName()));
            this.startActivityForResult(intent, REQUEST_ALTER_WINDOW);
        } else {
            mDialog.show();

            //Delay 5 second and Close the current UI automatically
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        DemoFloatingDialogAct.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DemoFloatingDialogAct.this.finish();
                            }
                        });
                    }

                }
            }).start();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == REQUEST_ALTER_WINDOW) {
            Utils.inFo(this, "onActivityResult");
            if (!Settings.canDrawOverlays(this)) {
                Utils.showToast(this, "Permission is not been granted yet");
            } else {
                Utils.showToast(this, "Permission is been granted");
                Utils.inFo(this, "Permission is been granted");
            }
        }
    }


    private AlertDialog floatingDialog(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("System dialog");
        builder.setMessage("Click the button to dismiss dialog ");
        builder.setCancelable(false);
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog sysDialog = builder.create();

        return sysDialog;

    }

}
