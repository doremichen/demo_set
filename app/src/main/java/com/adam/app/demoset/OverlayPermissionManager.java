/**
 * Description: This class is used to manage the overlay permission for the app.
 *
 * @author: Adam Chen
 * @date: 2025/08/26
 */
package com.adam.app.demoset;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

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
