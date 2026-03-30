/**
 * Copyright (C) 2026 Adam Chen. All rights reserved.
 * <p>
 * Description: This class is the dialog of create note
 * </p>
 *
 * @author Adam Chen
 * @version 1.0 - 2018/10/31
 */
package com.adam.app.demoset.database.dialog;

import android.content.Context;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;

public class CreateNoteDialog extends NoteDialog {

    private Context mContext;

    public CreateNoteDialog(Context context) {
        super(context);
        mContext = context.getApplicationContext();
    }

    @Override
    public String onDlgTitle() {
        Utils.info(this, "onDlgTitle enter");
        return mContext.getString(R.string.demo_database_dlg_create_note);
    }

    @Override
    public String onDlgRightButton() {
        Utils.info(this, "onDlgRightButton enter");
        return mContext.getString(R.string.demo_database_dlg_save);
    }
}
