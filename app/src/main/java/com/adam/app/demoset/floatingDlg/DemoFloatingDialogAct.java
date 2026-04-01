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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adam.app.demoset.utils.OverlayPermissionManager;
import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.utils.UIUtils;

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

        UIUtils.applySystemBarInsets(findViewById(R.id.root_layout), findViewById(R.id.tv_header));

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
        new Handler(Looper.getMainLooper()).postDelayed(this::finish, 3000L);

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
