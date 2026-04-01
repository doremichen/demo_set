/*
 * Copyright (c) 2026 Adam Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
