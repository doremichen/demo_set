package com.adam.app.demoset.quicksetting.qsservice;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import com.adam.app.demoset.Utils;

@SuppressLint("Override")
@TargetApi(Build.VERSION_CODES.O)
public class QuickSettingDialogService extends TileService {

    private DialogInterface.OnClickListener mPositiveButton = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Utils.info(this, "Dialog positive button click....");
            updateTile();
        }
    };

    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    @Override
    public void onClick() {
        super.onClick();
        Utils.info(this, "onClick");
        Dialog settingDlg = showAlertDialog("Quick setting2", null, mPositiveButton);

        this.showDialog(settingDlg);

    }

    private void updateTile() {
        Utils.info(this, "updateTile enter");
        Tile tile = getQsTile();
        int state = (tile.getState() == Tile.STATE_ACTIVE) ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE;

        tile.setState(state);
        tile.updateTile();

    }

    private Dialog showAlertDialog(String msg,
                                   DialogInterface.OnClickListener listener1,
                                   DialogInterface.OnClickListener listener2) {
        Context ctx = getApplicationContext();
        Tile tile = getQsTile();
        // Check state
        String rb_label = (tile.getState() == Tile.STATE_ACTIVE) ? getString(R.string.label_qs_turn_off) : getString(R.string.label_qs_turn_on);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        LayoutInflater LayoutInflater =
                (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = LayoutInflater.inflate(R.layout.layout_alert_dialog, null);
        TextView message = dialogView.findViewById(R.id.dlg_message);
        message.setText(msg);

        builder.setTitle("Info:");
        builder.setView(dialogView);
        builder.setNegativeButton(ctx.getString(R.string.label_qs_cancel),
                listener1);
        builder.setPositiveButton(rb_label,
                listener2);

        return builder.create();
    }
}
