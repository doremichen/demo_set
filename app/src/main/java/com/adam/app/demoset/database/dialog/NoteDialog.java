/**
 * Copyright (C) 2026 Adam Chen. All rights reserved.
 * <p>
 * Description: This class is the dialog of note
 * </p>
 *
 * @author Adam Chen
 * @version 1.0 - 2018/10/31
 */
package com.adam.app.demoset.database.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;

import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.databinding.DialogEditNoteBinding;

public abstract class NoteDialog {

    private final AlertDialog.Builder mAlertBuilder;
    private final LayoutInflater mInflater;
    private OnDlgCallBack mCallback;

    private final Context mContext;

    protected NoteDialog(Context context) {
        mInflater = LayoutInflater.from(context);
        mAlertBuilder = new AlertDialog.Builder(context, androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert);
        mAlertBuilder.setCancelable(false);

        mContext = context.getApplicationContext();
    }

    public void registerListener(OnDlgCallBack listener) {
        Utils.info(this, "registerCallBack enter");
        mCallback = listener;
    }

    public AlertDialog create() {
        Utils.info(this, "create enter");
        // View binding
        DialogEditNoteBinding binding = DialogEditNoteBinding.inflate(mInflater);
        // set title
        binding.setTitle(onDlgTitle());
        // set initial note
        binding.setInitialNote("");
        // update ui
        binding.executePendingBindings();

        mAlertBuilder.setView(binding.getRoot());
        final AlertDialog dialog = mAlertBuilder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // set positive button
        final String dlgRbutton = onDlgRightButton();

        binding.btnNoteSave.setText(dlgRbutton);
        binding.btnNoteSave.setOnClickListener(v -> {
            Utils.info(this, "Positive click");
            String strInput = binding.editNote.getText().toString().trim();

            // check input
            if (TextUtils.isEmpty(strInput)) {
                if (mCallback != null) {
                    mCallback.onShowMessage("Please input a valid and non-empty message.");
                }
                return;
            }

            // update list
            if (mCallback != null) {
                mCallback.updateList(strInput);
            }

            dialog.dismiss();
        });

        // set negative button
        binding.btnNoteCancel.setOnClickListener(v -> {
            Utils.info(this, "Negative click");
            dialog.cancel();
        });

        return dialog;
    }

    protected abstract String onDlgTitle();

    protected abstract String onDlgRightButton();


    public interface OnDlgCallBack {
        void onShowMessage(String msg);

        void updateList(String content);
    }
}
