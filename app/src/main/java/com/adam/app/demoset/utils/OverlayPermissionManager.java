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

package com.adam.app.demoset.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.adam.app.demoset.R;

public class OverlayPermissionManager {
    // Activity
    private Activity mActivity;
    // request code
    private int mRequestCode;

    /**
     * Constructor
     * @param context
     * @param requestCode
     */
    public OverlayPermissionManager(Activity context, int requestCode) {
        mActivity = context;
        mRequestCode = requestCode;
    }

    /**
     * hasOverlayPermission
     *  check overlay permission is granted or not
     */
    public boolean hasOverlayPermission() {
        return Settings.canDrawOverlays(mActivity);
    }

    /**
     * checkAndRequestPermission
     *  Check and guide user to grant overlay permission
     */
    public void checkAndRequestPermission() {
        if (!hasOverlayPermission()) {
            // request overlay permission
            showPermissionExplanationDialog();
        }
    }

    /**
     * show alert dialog to guide user to grant overlay permission
     */
    private void showPermissionExplanationDialog() {
        // post DialogButton
        Utils.DialogButton okBtn = new Utils.DialogButton(mActivity.getString(R.string.label_setting_btn),
                (dialog, which) -> openOverlaySettings());
        Utils.showAlertDialog(mActivity, R.string.label_overlay_permission_label,
                R.string.label_overlay_permission_description, okBtn);
    }

    /**
     * openOverlaySettings
     *  Open overlay setting screen
     */
    private void openOverlaySettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + mActivity.getPackageName()));
        mActivity.startActivityForResult(intent, mRequestCode);

    }
}
