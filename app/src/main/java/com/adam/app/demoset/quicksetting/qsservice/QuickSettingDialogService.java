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

    private static final String SERVICE_STATUS_FLAG = "Qs2_Status";

    private DialogInterface.OnClickListener mPositiveButton = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Utils.inFo(this, "Dialog positive button click....");
            updateTile();
        }
    };

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        Utils.inFo(this, "onClick");
        Dialog settingDlg = showAlertDialog("Quick setting2", null, mPositiveButton);

        this.showDialog(settingDlg);

    }

    private void updateTile() {
        Utils.inFo(this, "updateTile enter");
        Tile tile = getQsTile();
        boolean updateSetting = Utils.updateServiceStatus(getApplicationContext(), SERVICE_STATUS_FLAG);
        int state = updateSetting? Tile.STATE_ACTIVE: Tile.STATE_INACTIVE;

        tile.setState(state);
        tile.updateTile();

    }

    private Dialog showAlertDialog(String msg,
                                  DialogInterface.OnClickListener listener1,
                                  DialogInterface.OnClickListener listener2) {
        Context ctx = getApplicationContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        LayoutInflater LayoutInflater =
                (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = LayoutInflater.inflate(R.layout.layout_alert_dialog, null);
        TextView message = dialogView.findViewById(R.id.dlg_message);
        message.setText(msg);

        builder.setTitle("Info:");
        builder.setView(dialogView);
        builder.setNegativeButton(ctx.getString(R.string.label_off),
                listener1);
        builder.setPositiveButton(ctx.getString(R.string.label_on),
                listener2);

        return builder.create();
    }
}
