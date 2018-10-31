package com.adam.app.demoset.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.adam.app.demoset.Utils;
import com.adam.app.demoset.database.provider.MyDBProvider;

public enum DBController {
    INSTANCE;

    private ContentResolver mResolver;

    /**
     * Set content resolver
     *
     * @param resolver
     */
    public void setContentResolver(@NonNull ContentResolver resolver) {
        Utils.inFo(this, "setContentResolver enter");
        mResolver = resolver;
    }

    /**
     * Add note
     *
     * @param content
     * @return
     */
    public Uri addNote(@NonNull String content) {
        Utils.inFo(this, "addNote enter");
        // Check mResolver validity
        if (mResolver == null) {
            throw new IllegalArgumentException("Please set content resolver first");
        }

        // Build content values
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyDBProvider.COLUMN_NOTE, content);

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
        Utils.inFo(this, "updateNode enter");
        // Check mResolver validity
        if (mResolver == null) {
            throw new IllegalArgumentException("Please set content resolver first");
        }

        // Build content values
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyDBProvider.COLUMN_NOTE, content);

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
        Utils.inFo(this, "queryNote enter");
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
        Utils.inFo(this, "deleteNote enter");
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
        Utils.inFo(this, "deleteNote deleteId = " + deleteId);
        return deleteId;
    }

}
