package com.adam.app.demoset.database;

import android.database.Cursor;

import com.adam.app.demoset.Utils;
import com.adam.app.demoset.database.provider.MyDBProvider;

public class Note {

    private String mId;
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
