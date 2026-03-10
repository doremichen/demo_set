/**
 * Copyright (C) 2026 Adam Chen. All rights reserved.
 * <p>
 * Description: This class is the dialog of update note
 * </p>
 *
 * @author Adam Chen
 * @version 1.0 - 2018/10/31
 */
package com.adam.app.demoset.database.dialog;

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
