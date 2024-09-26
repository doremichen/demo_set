package com.adam.app.demoset.database2.dialog;

import android.content.Context;

import com.adam.app.demoset.Utils;

public class UpdateNoteDialog extends NoteDialog {

    public UpdateNoteDialog(Context context) {
        super(context);
    }

    @Override
    public String onDlgTitle() {
        Utils.info(this, "onDlgTitle enter");
        return TITLE_UPDATE_NOTE;
    }

    @Override
    public String onDlgRightButton() {
        Utils.info(this, "onDlgRightButton enter");
        return RBUTTON_UPDATE_NOTE;
    }
}
