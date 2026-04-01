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

package com.adam.app.demoset.floatingwindow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adam.app.demoset.utils.OverlayPermissionManager;
import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.utils.UIUtils;

/**
 * UI interface of the demo floating
 */
public class DemoFloatingWindowAct extends AppCompatActivity {

    private static final int REQUEST_ALTER_WINDOW = 0;
    private OverlayPermissionManager mOverlayPermissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_floating_window);

        UIUtils.applySystemBarInsets(findViewById(R.id.root_layout), findViewById(R.id.tv_header));


        // initial Overlay manager
        mOverlayPermissionManager = new OverlayPermissionManager(this, REQUEST_ALTER_WINDOW);
        mOverlayPermissionManager.checkAndRequestPermission();

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
            }
        }
    }

    public void openFloatingWindow(View view) {
        Utils.showToast(this, "open floating window");
        Intent intent = new Intent(this, FloatingWindowSvr.class);
        this.startService(intent);
    }

    public void exit(View view) {
        // exit this demo
        this.finish();
    }

    public void closeFloatingWindow(View view) {
        Utils.showToast(this, "close floating window");
        Intent intent = new Intent(this, FloatingWindowSvr.class);
        this.stopService(intent);
    }
}
