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

package com.adam.app.demoset.database.contentprovider.entity;

import android.database.Cursor;

import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.database.contentprovider.provider.MyDBProvider;

public class Note {

    private final String mId;
    private String mTimeStamp;
    private String mNote;

    public Note(Cursor c) {
        Utils.info(this, "Note constructor");
        // check index
        int resColumnId = getResId(c.getColumnIndex(MyDBProvider.COLUMN_ID));
        int resTimeStamp = getResId(c.getColumnIndex(MyDBProvider.COLUMN_TIMESTAMP));
        int resNote = getResId(c.getColumnIndex(MyDBProvider.COLUMN_NOTE));

        this.mId = c.getString(resColumnId);
        this.mTimeStamp = c.getString(resTimeStamp);
        this.mNote = c.getString(resNote);

    }

    public void updateData(Cursor c) {
        Utils.info(this, "updateData enter");
        int resTimeStamp = getResId(c.getColumnIndex(MyDBProvider.COLUMN_TIMESTAMP));
        int resNote = getResId(c.getColumnIndex(MyDBProvider.COLUMN_NOTE));

        this.mTimeStamp = c.getString(resTimeStamp);
        this.mNote = c.getString(resNote);
    }

    public String getId() {
        Utils.info(this, "getId enter");
        return this.mId;
    }

    public String getTimeStamp() {
        Utils.info(this, "getTimeStamp enter: timestamp " + this.mTimeStamp);
        return this.mTimeStamp;
    }

    public String getNote() {
        Utils.info(this, "getNote enter: note " + this.mNote);
        return this.mNote;
    }

    private int getResId(int idOfColumn) {
        if (idOfColumn < 0)
            throw new IllegalArgumentException("id < 0!!!");
        return idOfColumn;
    }
}
