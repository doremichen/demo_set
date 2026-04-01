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

package com.adam.app.demoset.database.contentprovider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.database.contentprovider.provider.MyDBProvider;

import java.text.SimpleDateFormat;
import java.util.Date;

public enum DBController {
    INSTANCE;

    private ContentResolver mResolver;

    /**
     * Set content resolver
     *
     * @param resolver
     */
    public void setContentResolver(@NonNull ContentResolver resolver) {
        Utils.info(this, "setContentResolver enter");
        mResolver = resolver;
    }

    /**
     * Add note
     *
     * @param content
     * @return
     */
    public Uri addNote(@NonNull String content) {
        Utils.info(this, "addNote enter");
        // Check mResolver validity
        if (mResolver == null) {
            throw new IllegalArgumentException("Please set content resolver first");
        }

        // Build content values
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyDBProvider.COLUMN_NOTE, content);
        contentValues.put(MyDBProvider.COLUMN_TIMESTAMP, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        // Insert data
        Uri newUri = mResolver.insert(MyDBProvider.MYTABLE_URI, contentValues);

        return newUri;
    }

    /**
     * Update Note
     *
     * @param id
     * @param content
     * @return
     */
    public int updateNote(@NonNull String id, @NonNull String content) {
        Utils.info(this, "updateNode enter");
        // Check mResolver validity
        if (mResolver == null) {
            throw new IllegalArgumentException("Please set content resolver first");
        }

        // Build content values
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyDBProvider.COLUMN_NOTE, content);
        contentValues.put(MyDBProvider.COLUMN_TIMESTAMP, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));


        String selection = MyDBProvider.COLUMN_ID + "=?";
        String[] selectionArgs = {id};

        // Update data
        int NumupdateId = mResolver.update(MyDBProvider.MYTABLE_URI, contentValues, selection, selectionArgs);

        return NumupdateId;

    }

    /**
     * Query note
     *
     * @param note
     * @return
     */
    public Cursor queryNote(String note) {
        Utils.info(this, "queryNote enter");
        // Check mResolver validity
        if (mResolver == null) {
            throw new IllegalArgumentException("Please set content resolver first");
        }

        // Define projection
        String[] projection = {MyDBProvider.COLUMN_ID, MyDBProvider.COLUMN_TIMESTAMP, MyDBProvider.COLUMN_NOTE};

        String selection = null;
        String[] selectionArgs = new String[1];

        if (TextUtils.isEmpty(note)) {
            selectionArgs = null;
        } else {
            selection = MyDBProvider.COLUMN_NOTE + "=?";
            selectionArgs[0] = note;
        }

        // Query data
        Cursor c = mResolver.query(MyDBProvider.MYTABLE_URI, projection, selection, selectionArgs, null);

        return c;
    }

    /**
     * Delete note
     *
     * @param id
     * @return
     */
    public int deleteNote(String id) {
        Utils.info(this, "deleteNote enter");
        // Check mResolver validity
        if (mResolver == null) {
            throw new IllegalArgumentException("Please set content resolver first");
        }

        String selection = null;
        String[] selectionArgs = new String[1];

        if (TextUtils.isEmpty(id)) {
            selectionArgs = null;
        } else {
            selection = MyDBProvider.COLUMN_ID + "=?";
            selectionArgs[0] = id;
        }

        // Delete data
        int deleteId = mResolver.delete(MyDBProvider.MYTABLE_URI, selection, selectionArgs);
        Utils.info(this, "deleteNote deleteId = " + deleteId);
        return deleteId;
    }

}
