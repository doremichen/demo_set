package com.adam.app.demoset.database.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.adam.app.demoset.Utils;

public class MyDBProvider extends ContentProvider {

    // Database information
    private static final String DATABASE_NAME = "Adam";
    private static final String TABLE_NAME = "NoteTable";
    private static final int DATABSE_VERSION = 1;

    // Build the uri
    private static final String AUTHORITY = "com.adam.app.demoset.provider.MyNote";
    private static final String URL = "content://" + AUTHORITY + "/"
            + TABLE_NAME;
    public static final Uri MYTABLE_URI = Uri.parse(URL);

    // Index for Uri matcher
    public static final int MYTABLE = 1;
    public static final int MYTABLE_ID = 2;

    // Construct Uri matcher
    private static final UriMatcher URI_MATCHER = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAME, MYTABLE);
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAME + "/#", MYTABLE_ID);
    }

    // The column name of database
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    // Create table sqlite
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TIMESTAMP + " TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP, "
                    + COLUMN_NOTE + " TEXT"
                    + ")";

    // My database helper
    private static class MyDBHelper extends SQLiteOpenHelper {

        public MyDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABSE_VERSION);
            Utils.inFo(this, "MyDbHelper constructor...");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Utils.inFo(this, "onCreate enter");
            // Create db
            db.execSQL(CREATE_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Utils.inFo(this, "onUpgrade enter");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    private SQLiteDatabase mWriteDatabase;
    private SQLiteDatabase mReadDatabase;

    @Override
    public boolean onCreate() {
        Utils.inFo(this, "onCreate enter");
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
        Utils.inFo(this, "query enter");
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);

        // Check uri type
        int uriType = URI_MATCHER.match(uri);

        switch (uriType) {
            case MYTABLE:
                break;
            case MYTABLE_ID:
                queryBuilder.appendWhere(COLUMN_ID + "=" + uri.getLastPathSegment());
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
        Utils.inFo(this, "getType enter");
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
        Utils.inFo(this, "insert enter");
        int uriType = URI_MATCHER.match(uri);

        long id = 0L;
        switch (uriType) {
            case MYTABLE:
                // insert data
                id = mWriteDatabase.insert(TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri");
        }

        // Notify content resolver
        this.getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse(TABLE_NAME + "/" + id);
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
        Utils.inFo(this, "delete enter");
        int uriType = URI_MATCHER.match(uri);
        int rowDelete = 0;

        switch (uriType) {
            case MYTABLE:
                rowDelete = mWriteDatabase.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case MYTABLE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowDelete = mWriteDatabase.delete(TABLE_NAME, COLUMN_ID + "=" + id, null);
                } else {
                    rowDelete = mWriteDatabase.delete(TABLE_NAME,
                            COLUMN_ID + "=" + id + "AND" + selection, selectionArgs);
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
        Utils.inFo(this, "update enter");
        int uriType = URI_MATCHER.match(uri);
        int rowUpdate = 0;

        switch (uriType) {
            case MYTABLE:
                rowUpdate = mWriteDatabase.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case MYTABLE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowUpdate = mWriteDatabase.update(TABLE_NAME, values, COLUMN_ID + "=" + id, null);
                } else {
                    rowUpdate = mWriteDatabase.update(TABLE_NAME, values,
                            COLUMN_ID + "=" + id + "AND" + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri");
        }

        // Notify content resolver
        this.getContext().getContentResolver().notifyChange(uri, null);

        return rowUpdate;
    }
}
