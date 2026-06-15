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
import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * A Quick Setting TileService that demonstrates basic state toggling.
 * Refactored to avoid memory leaks by removing anonymous HashMap subclasses.
 */
@SuppressLint("Override")
@RequiresApi(api = Build.VERSION_CODES.O)
public class QuickSettingService extends TileService {

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        Utils.info(this, "onTileAdded");
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        Utils.info(this, "onTileRemoved");
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        Utils.info(this, "onStartListening");
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        Utils.info(this, "onStopListening");
    }

    @Override
    public void onClick() {
        super.onClick();
        Utils.info(this, "onClick");
        
        performVibration();
        updateTileState();
    }

    private void performVibration() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(50);
            }
        }
    }

    /**
     * Template Pattern implementation for tile updates.
     */
    static abstract class UpdateTemplate {
        void process(Context context, Tile tile) {
            Utils.makeStatusNotification(context, onMsg());
            
            Icon icon = Icon.createWithResource(context, onResId());
            tile.setLabel(onMsg());
            tile.setIcon(icon);
            tile.setState(onState());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                tile.setSubtitle(onState() == Tile.STATE_ACTIVE ? "Active Mode" : "Inactive Mode");
            }
            
            tile.updateTile();
            Utils.showToast(context, context.getString(R.string.qs_msg_tile_updated_format, onMsg()));
        }

        abstract String onMsg();
        abstract int onResId();
        abstract int onState();
    }

    static class Active extends UpdateTemplate {
        @Override
        String onMsg() { return "Demo QS: Active"; }
        @Override
        int onResId() { return R.drawable.ic_demo_qs1_active; }
        @Override
        int onState() { return Tile.STATE_ACTIVE; }
    }

    static class InActive extends UpdateTemplate {
        @Override
        String onMsg() { return "Demo QS: Inactive"; }
        @Override
        int onResId() { return R.drawable.ic_demo_qs1_inactive; }
        @Override
        int onState() { return Tile.STATE_INACTIVE; }
    }

    /**
     * Context for managing tile state transitions using the Strategy pattern.
     * Uses regular HashMap instead of anonymous subclasses to prevent memory leaks.
     */
    private static class UpdateContext {
        private static final UpdateContext INSTANCE = new UpdateContext();
        private final Map<Integer, UpdateTemplate> strategyMap;

        private UpdateContext() {
            strategyMap = new HashMap<>();
            strategyMap.put(Tile.STATE_INACTIVE, new Active());
            strategyMap.put(Tile.STATE_ACTIVE, new InActive());
        }

        static UpdateContext getInstance() {
            return INSTANCE;
        }

        void update(Tile tile, Context context) {
            UpdateTemplate strategy = strategyMap.get(tile.getState());
            if (strategy != null) {
                strategy.process(context, tile);
            }
        }
    }

    private void updateTileState() {
        Tile tile = getQsTile();
        if (tile != null) {
            UpdateContext.getInstance().update(tile, getApplicationContext());
        }
    }
}
