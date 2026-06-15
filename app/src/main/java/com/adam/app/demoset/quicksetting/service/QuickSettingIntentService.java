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
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.adam.app.demoset.quicksetting.presentation.QuickSettingResultAct;
import com.adam.app.demoset.utils.Utils;

/**
 * A Quick Setting TileService that demonstrates launching an Activity.
 * Uses startActivityAndCollapse to transition from notification shade to full UI.
 */
@SuppressLint("Override")
@RequiresApi(api = Build.VERSION_CODES.O)
public class QuickSettingIntentService extends TileService {

    @Override
    public void onClick() {
        super.onClick();
        Utils.info(this, "onClick");

        // Demonstration: Handling interactions while device is locked
        if (isLocked()) {
            // Prompt user to unlock before proceeding
            unlockAndRun(() -> {
                Utils.info(this, "Device unlocked, launching activity.");
                launchResultActivity();
            });
        } else {
            launchResultActivity();
        }
    }

    /**
     * Prepares data and launches the result activity, collapsing the notification shade.
     */
    private void launchResultActivity() {
        Tile tile = getQsTile();
        if (tile == null) return;

        String title = tile.getLabel().toString();
        String status = (tile.getState() == Tile.STATE_ACTIVE) ? "Active" : "Inactive";

        Intent intent = new Intent(getApplicationContext(), QuickSettingResultAct.class);
        intent.putExtra(QuickSettingResultAct.KEY_RESULT_SETTING_TITLE, title);
        intent.putExtra(QuickSettingResultAct.KEY_RESULT_SETTING_STATE, status);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14+ (API 34) requires PendingIntent for startActivityAndCollapse
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            this.startActivityAndCollapse(pendingIntent);
        } else {
            // Legacy implementation for older versions
            this.startActivityAndCollapse(intent);
        }
        
        Utils.info(this, "Activity launch command sent.");
    }
}
