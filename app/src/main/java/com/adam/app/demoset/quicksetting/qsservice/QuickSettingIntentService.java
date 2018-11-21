package com.adam.app.demoset.quicksetting.qsservice;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.adam.app.demoset.Utils;
import com.adam.app.demoset.quicksetting.QuickSettingResultAct;

@SuppressLint("Override")
@TargetApi(Build.VERSION_CODES.O)
public class QuickSettingIntentService extends TileService {

    @Override
    public void onClick() {
        super.onClick();
        Utils.inFo(this, "onClick enter");
        boolean isCurrentlyLocked = this.isLocked();

        if (!isCurrentlyLocked) {
            Tile tile = getQsTile();
            String title = tile.getLabel().toString();
            String status = (tile.getState() ==  Tile.STATE_ACTIVE)? "Active": "Inactive";

            Intent intent = new Intent(getApplicationContext(), QuickSettingResultAct.class);
            intent.putExtra(QuickSettingResultAct.KEY_RESULT_SETTING_TITLE, title);
            intent.putExtra(QuickSettingResultAct.KEY_RESULT_SETTING_STATE, status);
            // Start result activity
            this.startActivityAndCollapse(intent);
        }

    }
}
