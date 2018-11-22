package com.adam.app.demoset.database2.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

public abstract class NoteDialog {

    protected static final String TITLE_CREATE_NOTE = "Create note";
    protected static final String TITLE_UPDATE_NOTE = "Update note";
    protected static final String RBUTTON_CREATE_NOTE = "Save";
    protected static final String RBUTTON_UPDATE_NOTE = "Update";

    private OnControllerCallBack mListener;

    private LayoutInflater mInflater;
    private final AlertDialog.Builder mAlertBuilder;

    protected NoteDialog(Context context) {
        mInflater = LayoutInflater.from(context);
        mAlertBuilder = new AlertDialog.Builder(context);
        mAlertBuilder.setCancelable(false);
    }

    public void registerListener(OnControllerCallBack listener) {
        Utils.inFo(this, "registerCallBack enter");
        mListener = listener;
    }

    public AlertDialog create() {
        Utils.inFo(this, "create enter");

        View view = mInflater.inflate(R.layout.dialog_edit_note, null);

        mAlertBuilder.setView(view);

        TextView title = view.findViewById(R.id.dialog_note_title);
        final EditText input = view.findViewById(R.id.edit_note);

        // Set Dialog title
        title.setText(onDlgTitle());

        // Register positive/negative button
        final String dlgRbutton = onDlgRightButton();
        mAlertBuilder.setPositiveButton(dlgRbutton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.inFo(this, "Positive click");
                // Check whether the edit string is empty or not
                String strInput = input.getText().toString();
                if (TextUtils.isEmpty(strInput)) {
                    // Info UI
                    if (mListener != null) {
                        mListener.info("Please input the valid and nonempty message.");
                    }
                    return;
                }

                if (RBUTTON_CREATE_NOTE.equals(dlgRbutton)) {
                    // Info UI to insert data
                    if (mListener != null) {
                        mListener.updateList(strInput);
                    }

                } else if (RBUTTON_UPDATE_NOTE.equals(dlgRbutton)) {
                    // Info UI to update databse
                    if (mListener != null) {
                        mListener.updateList(strInput);
                    }
                }
            }
        });
        mAlertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.inFo(this, "Negative click");
                dialog.dismiss();
            }
        });


        return mAlertBuilder.create();

    }

    protected abstract String onDlgTitle();

    protected abstract String onDlgRightButton();


    public interface OnControllerCallBack {
        void info(String msg);

        void updateList(String content);
    }
}
