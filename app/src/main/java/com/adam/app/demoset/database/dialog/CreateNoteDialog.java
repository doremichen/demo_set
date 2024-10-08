package com.adam.app.demoset.database.dialog;

import android.content.Context;

import com.adam.app.demoset.Utils;

public class CreateNoteDialog extends NoteDialog {

    public CreateNoteDialog(Context context) {
        super(context);
    }

    @Override
    public String onDlgTitle() {
        Utils.info(this, "onDlgTitle enter");
        return TITLE_CREATE_NOTE;
    }

    @Override
    public String onDlgRightButton() {
        Utils.info(this, "onDlgRightButton enter");
        return RBUTTON_CREATE_NOTE;
    }
}
