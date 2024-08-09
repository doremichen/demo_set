package com.adam.app.demoset.quicksetting.qsservice;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("Override")
@TargetApi(Build.VERSION_CODES.O)
public class QuickSettingService extends TileService {

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        Utils.info(this, "onTileAdded enter");
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        Utils.info(this, "onTileRemoved enter");
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        Utils.info(this, "onStartListening enter");
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        Utils.info(this, "onStopListening enter");
    }

    @Override
    public void onClick() {
        super.onClick();
        Utils.info(this, "onClick enter");
        updateTitle();
    }

    /**
     * template pattern
     */
    static abstract class UpdateTemplate {
        void process(Context context, Tile tile) {
            // notification
            Utils.makeStatusNotification(onMsg(), context);
            // tile config
            Icon icon = Icon.createWithResource(context, onResId());
            tile.setLabel(onMsg());
            tile.setIcon(icon);
            tile.setState(onState());
            // update
            tile.updateTile();
        }

        abstract String onMsg();
        abstract int onResId();
        abstract int onState();
    }

    static class Active extends UpdateTemplate {

        @Override
        String onMsg() {
            return "Demo quick setting1: active";
        }

        @Override
        int onResId() {
            return R.drawable.ic_demo_qs1_active;
        }

        @Override
        int onState() {
            return Tile.STATE_ACTIVE;
        }
    }

    static class InActive extends UpdateTemplate {

        @Override
        String onMsg() {
            return "Demo quick setting1: inactive";
        }

        @Override
        int onResId() {
            return R.drawable.ic_demo_qs1_inactive;
        }

        @Override
        int onState() {
            return Tile.STATE_INACTIVE;
        }
    }

    /**
     * Update tile Context
     */
    enum UpdateContext {

        INSTANCE;

        // state map
        Map<Integer , UpdateTemplate> mMap = new HashMap<>() {
            {
                put(Tile.STATE_INACTIVE, new Active());
                put(Tile.STATE_ACTIVE, new InActive());
            }
        };

        void update(Tile tile, Context context) {
            UpdateTemplate which = this.mMap.get(tile.getState());
            if (!Utils.areAllNotNull(which)) {
                Utils.info(this, "This item is no in map!!!");
                return;
            }

            which.process(context, tile);
        }

    }


    private void updateTitle() {
        Utils.info(this, "updateTitle enter");
        Tile tile = this.getQsTile();
        if (!Utils.areAllNotNull(tile)) {
            Utils.info(this, "tile is null!!!");
            return;
        }

        UpdateContext.INSTANCE.update(tile, getApplicationContext());
    }


}
