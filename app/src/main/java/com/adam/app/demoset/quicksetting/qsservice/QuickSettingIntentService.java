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

package com.adam.app.demoset.quicksetting.qsservice;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.quicksetting.QuickSettingResultAct;

@SuppressLint("Override")
@TargetApi(Build.VERSION_CODES.O)
public class QuickSettingIntentService extends TileService {

    @Override
    public void onClick() {
        super.onClick();
        Utils.info(this, "onClick enter");
        boolean isCurrentlyLocked = this.isLocked();

        if (!isCurrentlyLocked) {
            Tile tile = getQsTile();
            String title = tile.getLabel().toString();
            String status = (tile.getState() == Tile.STATE_ACTIVE) ? "Active" : "Inactive";

            Intent intent = new Intent(getApplicationContext(), QuickSettingResultAct.class);
            intent.putExtra(QuickSettingResultAct.KEY_RESULT_SETTING_TITLE, title);
            intent.putExtra(QuickSettingResultAct.KEY_RESULT_SETTING_STATE, status);
            // Start result activity
            this.startActivityAndCollapse(intent);
        }

    }
}
