package com.adam.app.demoset.quicksetting.qsservice;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

@SuppressLint("Override")
@TargetApi(Build.VERSION_CODES.O)
public class QuickSettingService extends TileService {

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        Utils.inFo(this, "onTileAdded enter");
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        Utils.inFo(this, "onTileRemoved enter");
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        Utils.inFo(this, "onStartListening enter");
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        Utils.inFo(this, "onStopListening enter");
    }

    @Override
    public void onClick() {
        super.onClick();
        Utils.inFo(this, "onClick enter");
        updateTitle();
    }

    private void updateTitle() {
        Utils.inFo(this, "updateTitle enter");
        Tile tile = this.getQsTile();
        boolean updateSetting = (tile.getState() == Tile.STATE_ACTIVE) ? true : false;
        Icon icon;
        String label;
        int state;

        if (!updateSetting) {
            label = "Demo quick setting1: active";
            icon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_demo_qs1_active);
            state = Tile.STATE_ACTIVE;
            // Notification
            Utils.makeStatusNotification("Quick setting1 is active", getApplicationContext());

        } else {

            label = "Demo quick setting1: inactive";
            icon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_demo_qs1_inactive);
            state = Tile.STATE_INACTIVE;
            // Notification
            Utils.makeStatusNotification("Quick setting1 is inactive", getApplicationContext());
        }

        // Change UI of the tile
        tile.setLabel(label);
        tile.setIcon(icon);
        tile.setState(state);

        // Update Title
        tile.updateTile();

    }


}
