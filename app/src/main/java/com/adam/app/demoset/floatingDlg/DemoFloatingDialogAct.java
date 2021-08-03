package com.adam.app.demoset.floatingDlg;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_demo_floating_dialog);

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getPackageName()));
            this.startActivityForResult(intent, REQUEST_ALTER_WINDOW);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, FloatingDialogSvr.class);
        this.stopService(intent);
    }

    public void onStartFloatingDialog(View v) {

        if (Settings.canDrawOverlays(this)) {
            triggerDialog();
        }

    }

    public void onClose(View v) {

        this.finish();
    }

    private void triggerDialog() {

        Intent intent = new Intent(this, FloatingDialogSvr.class);
        intent.setAction(FloatingDialogSvr.ACTION_SHOW_FLOATING_DIALOG);
        this.startService(intent);

        //Delay 3 second and Close the current UI automatically
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(3000L);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == REQUEST_ALTER_WINDOW) {
            Utils.info(this, "onActivityResult");
            if (!Settings.canDrawOverlays(this)) {
                Utils.showToast(this, "Permission is not been granted yet");
            } else {
                Utils.showToast(this, "Permission is been granted");
                Utils.info(this, "Permission is been granted");
            }
        }
    }


}
