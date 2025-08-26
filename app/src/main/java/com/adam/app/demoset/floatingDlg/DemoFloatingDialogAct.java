/**
 * Description: This class is used to show the demo floating dialog.
 * <p>
 * Author: Adam Chen
 * Date: 2018/09/18
 */
package com.adam.app.demoset.floatingDlg;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adam.app.demoset.OverlayPermissionManager;
import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

/**
 * This class is the main activity of demo floating dialog.
 */
public class DemoFloatingDialogAct extends AppCompatActivity {

    private static final int REQUEST_ALTER_WINDOW = 0;
    private OverlayPermissionManager mOverlayPermissionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_demo_floating_dialog);

        // initial Overlay manager
        mOverlayPermissionManager = new OverlayPermissionManager(this, REQUEST_ALTER_WINDOW);
        mOverlayPermissionManager.checkAndRequestPermission();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, FloatingDialogSvr.class);
        this.stopService(intent);
    }

    public void onStartFloatingDialog(View v) {

        if (mOverlayPermissionManager.hasOverlayPermission()) {
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
        // finish this activity
        new Handler().postDelayed(this::finish, 3000L);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQUEST_ALTER_WINDOW) {
            Utils.info(this, "onActivityResult");
            if (mOverlayPermissionManager.hasOverlayPermission()) {
                Utils.showToast(this, "Permission is not been granted yet");
            } else {
                Utils.showToast(this, "Permission is been granted");
                Utils.info(this, "Permission is been granted");
            }
        }
    }


}
