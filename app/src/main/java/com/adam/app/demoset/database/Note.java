package com.adam.app.demoset.database;

import com.adam.app.demoset.Utils;

public class Note {

    private String mId;
    private String mTimeStamp;
    private String mNote;

    public Note(String id, String timeStamp, String note) {
        this.mId = id;
        this.mTimeStamp = timeStamp;
        this.mNote = note;
    }

    public void updateNote(String note) {
        Utils.inFo(this, "updateNote enter");
        mNote = note;
    }

    public void updateTimeStamp(String time) {
        mTimeStamp = time;
    }

    public String getId() {
        Utils.inFo(this, "getId enter");
        return this.mId;
    }

    public String getTimeStamp() {
        Utils.inFo(this, "getTimeStamp enter");
        return this.mTimeStamp;
    }

    public String getNote() {
        Utils.inFo(this, "getNote enter");
        return this.mNote;
    }
}
