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

package com.adam.app.demoset.quicksetting.service;

import android.annotation.SuppressLint;
import androidx.annotation.RequiresApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;

/**
 * A Quick Setting TileService that demonstrates displaying a system dialog.
 * Dialogs shown via showDialog(Dialog) appear above the notification shade.
 */
@SuppressLint("Override")
@RequiresApi(api = Build.VERSION_CODES.O)
public class QuickSettingDialogService extends TileService {

    private final DialogInterface.OnClickListener mPositiveButton = (dialog, which) -> {
        Utils.info(this, "Dialog positive button clicked.");
        updateTileState();
    };

    @Override
    public void onClick() {
        super.onClick();
        Utils.info(this, "onClick");

        // Demonstration: Check if device is locked
        if (isLocked()) {
            Utils.showToast(this, getString(R.string.qs_msg_unlock_required));
            // We can also use unlockAndRun() if we want to prompt for PIN/Pattern
            return;
        }

        Dialog settingDlg = createAlertDialog("Quick Setting Config", mPositiveButton);
        // showDialog(Dialog) is the specific API for TileService
        this.showDialog(settingDlg);
    }

    /**
     * Toggles the tile state between ACTIVE and INACTIVE.
     */
    private void updateTileState() {
        Tile tile = getQsTile();
        if (tile == null) return;

        int newState = (tile.getState() == Tile.STATE_ACTIVE) ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE;
        tile.setState(newState);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            tile.setSubtitle(newState == Tile.STATE_ACTIVE ? "Dialog Mode: ON" : "Dialog Mode: OFF");
        }
        
        tile.updateTile();
        Utils.showToast(this, getString(R.string.qs_msg_dialog_updated));
    }

    /**
     * Creates a custom AlertDialog for the Quick Settings panel.
     */
    private Dialog createAlertDialog(String msg, DialogInterface.OnClickListener positiveListener) {
        Context ctx = getApplicationContext();
        Tile tile = getQsTile();
        
        // Determine button label based on current state
        String actionLabel = (tile != null && tile.getState() == Tile.STATE_ACTIVE) 
                ? getString(R.string.label_qs_turn_off) 
                : getString(R.string.label_qs_turn_on);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.layout_alert_dialog, null);
        TextView messageView = dialogView.findViewById(R.id.dlg_message);
        messageView.setText(msg);

        builder.setTitle("Configuration");
        builder.setView(dialogView);
        builder.setNegativeButton(getString(R.string.label_qs_cancel), (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton(actionLabel, positiveListener);

        return builder.create();
    }
}
