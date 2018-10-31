package com.adam.app.demoset.database.dialog;

import android.content.Context;

import com.adam.app.demoset.Utils;

public class UpdateNoteDialog extends NoteDialog {

    private String mUpdateId;

    public UpdateNoteDialog(Context context, String id) {
        super(context);
        mUpdateId = id;
    }

    @Override
    protected String updateId() {
        return mUpdateId == null ? "" : mUpdateId;
    }

    @Override
    public String onDlgTitle() {
        Utils.inFo(this, "onDlgTitle enter");
        return TITLE_UPDATE_NOTE;
    }

    @Override
    public String onDlgRightButton() {
        Utils.inFo(this, "onDlgRightButton enter");
        return RBUTTON_UPDATE_NOTE;
    }
}
