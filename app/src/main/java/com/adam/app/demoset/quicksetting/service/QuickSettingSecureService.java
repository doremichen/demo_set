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
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;

/**
 * Pillar 5: Secure Lock-Screen Access.
 * Demonstrates the use of isLocked() and unlockAndRun().
 */
@SuppressLint("Override")
@RequiresApi(api = Build.VERSION_CODES.O)
public class QuickSettingSecureService extends TileService {

    @Override
    public void onStartListening() {
        super.onStartListening();
        Tile tile = getQsTile();
        if (tile != null) {
            tile.setState(Tile.STATE_ACTIVE);
            tile.updateTile();
        }
    }

    @Override
    public void onClick() {
        super.onClick();
        
        if (isLocked()) {
            // Demo Pillar 5: Show warning if locked
            Utils.showToast(this, getString(R.string.qs_msg_locked_warning));
            
            // Request unlock and then perform sensitive action
            unlockAndRun(() -> {
                Utils.showToast(this, getString(R.string.qs_msg_auth_success));
                performSensitiveAction();
            });
        } else {
            performSensitiveAction();
        }
    }

    private void performSensitiveAction() {
        Utils.makeStatusNotification(this, "Secure action executed in background.");
        Utils.showToast(this, getString(R.string.qs_msg_secure_complete));
    }
}
