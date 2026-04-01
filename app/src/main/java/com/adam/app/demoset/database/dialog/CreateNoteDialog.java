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
