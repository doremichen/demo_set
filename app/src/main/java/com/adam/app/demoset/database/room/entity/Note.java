/**
 * Copyright (C) 2018 Adam Chen. All rights reserved.
 * <p>
 * Description: This class is the entity of note
 * </p>
 *
 * @author Adam Chen
 * @version 1.0 - 2018/11/12
 */
package com.adam.app.demoset.database.room.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        mTimeStamp = timeStamp;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        mNote = note;
    }
}
