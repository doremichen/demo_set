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

package com.adam.app.demoset.database.contentprovider.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adam.app.demoset.utils.DemoAppConstants;
import com.adam.app.demoset.utils.Utils;

public class MyDBProvider extends ContentProvider {

    // Index for Uri matcher
    public static final int MYTABLE = 1;
    public static final int MYTABLE_ID = 2;

    // Create table sqlite
    public static final String CREATE_TABLE =
            "CREATE TABLE " + DemoAppConstants.TABLE_NAME_NOTE + "("
                    + DemoAppConstants.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + DemoAppConstants.COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                    + DemoAppConstants.COLUMN_NOTE + " TEXT"
                    + ")";
    private static final int DATABSE_VERSION = 1;
    // Build the uri
    private static final String URL = "content://" + DemoAppConstants.AUTHORITY_MY_NOTE + "/"
            + DemoAppConstants.TABLE_NAME_NOTE;
    public static final Uri MYTABLE_URI = Uri.parse(URL);
    // Construct Uri matcher
    private static final UriMatcher URI_MATCHER = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(DemoAppConstants.AUTHORITY_MY_NOTE, DemoAppConstants.TABLE_NAME_NOTE, MYTABLE);
        URI_MATCHER.addURI(DemoAppConstants.AUTHORITY_MY_NOTE, DemoAppConstants.TABLE_NAME_NOTE + "/#", MYTABLE_ID);
    }

    private SQLiteDatabase mWriteDatabase;
    private SQLiteDatabase mReadDatabase;

    @Override
    public boolean onCreate() {
        Utils.info(this, "onCreate enter");
        MyDBHelper helper = new MyDBHelper(this.getContext());

        // Get read/write handler
        mWriteDatabase = helper.getWritableDatabase();
        mReadDatabase = helper.getReadableDatabase();

        return mWriteDatabase != null && mReadDatabase != null;
    }

    /**
     * Query data from database
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        Utils.info(this, "query enter");
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DemoAppConstants.TABLE_NAME_NOTE);

        // Check uri type
        int uriType = URI_MATCHER.match(uri);

        switch (uriType) {
            case MYTABLE:
                break;
            case MYTABLE_ID:
                queryBuilder.appendWhere(DemoAppConstants.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown uri");
        }

        // Create cursor
        Cursor c = queryBuilder.query(mReadDatabase, projection, selection,
                selectionArgs, null, null, sortOrder);

        // Notify content resolver
        c.setNotificationUri(this.getContext().getContentResolver(), uri);

        return c;
    }

    /**
     * Get mime type by uri
     *
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Utils.info(this, "getType enter");
        int uriType = URI_MATCHER.match(uri);
        switch (uriType) {
            case MYTABLE:
                return "vnd.android.cursor.dir/vnd.mynote";
            case MYTABLE_ID:
                return "vnd.android.cursor.item/vnd.mynote";
            default:
                throw new IllegalArgumentException("Unknown uri");
        }
    }

    /**
     * Insert data to database
     *
     * @param uri
     * @param values
     * @return
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Utils.info(this, "insert enter");
        int uriType = URI_MATCHER.match(uri);

        long id = 0L;
        switch (uriType) {
            case MYTABLE:
                // insert data
                id = mWriteDatabase.insert(DemoAppConstants.TABLE_NAME_NOTE, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri");
        }

        // Notify content resolver
        this.getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse(DemoAppConstants.TABLE_NAME_NOTE + "/" + id);
    }

    /**
     * Delete data in database
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Utils.info(this, "delete enter");
        int uriType = URI_MATCHER.match(uri);
        int rowDelete = 0;

        switch (uriType) {
            case MYTABLE:
                rowDelete = mWriteDatabase.delete(DemoAppConstants.TABLE_NAME_NOTE, selection, selectionArgs);
                break;
            case MYTABLE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowDelete = mWriteDatabase.delete(DemoAppConstants.TABLE_NAME_NOTE, DemoAppConstants.COLUMN_ID + "=" + id, null);
                } else {
                    rowDelete = mWriteDatabase.delete(DemoAppConstants.TABLE_NAME_NOTE,
                            DemoAppConstants.COLUMN_ID + "=" + id + " AND " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri");
        }

        // Notify content resolver
        this.getContext().getContentResolver().notifyChange(uri, null);

        return rowDelete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Utils.info(this, "update enter");
        int uriType = URI_MATCHER.match(uri);
        int rowUpdate = 0;

        switch (uriType) {
            case MYTABLE:
                rowUpdate = mWriteDatabase.update(DemoAppConstants.TABLE_NAME_NOTE, values, selection, selectionArgs);
                break;
            case MYTABLE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowUpdate = mWriteDatabase.update(DemoAppConstants.TABLE_NAME_NOTE, values, DemoAppConstants.COLUMN_ID + "=" + id, null);
                } else {
                    rowUpdate = mWriteDatabase.update(DemoAppConstants.TABLE_NAME_NOTE, values,
                            DemoAppConstants.COLUMN_ID + "=" + id + " AND " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri");
        }

        // Notify content resolver
        this.getContext().getContentResolver().notifyChange(uri, null);

        return rowUpdate;
    }

    // My database helper
    private static class MyDBHelper extends SQLiteOpenHelper {

        public MyDBHelper(Context context) {
            super(context, DemoAppConstants.DATABASE_NAME_CP, null, DATABSE_VERSION);
            Utils.info(this, "MyDbHelper constructor...");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Utils.info(this, "onCreate enter");
            // Create db
            db.execSQL(CREATE_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Utils.info(this, "onUpgrade enter");
            db.execSQL("DROP TABLE IF EXISTS " + DemoAppConstants.TABLE_NAME_NOTE);
            onCreate(db);
        }
    }
}
