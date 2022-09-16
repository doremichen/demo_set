package com.adam.app.demoset.database2.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "Adam_note_table")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int mId;

    @NonNull
    @ColumnInfo(name = "time_stamp")
    private String mTimeStamp;

    @NonNull
    @ColumnInfo(name = "note")
    private String mNote;

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public void setTimeStamp(String timeStamp) {
        mTimeStamp = timeStamp;
    }

    public String getTimeStamp() {
        return mTimeStamp;
    }

    public void setNote(String note) {
        mNote = note;
    }

    public String getNote() {
        return mNote;
    }
}
