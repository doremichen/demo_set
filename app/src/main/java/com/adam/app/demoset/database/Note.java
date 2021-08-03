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
        Utils.info(this, "updateNote enter");
        mNote = note;
    }

    public void updateTimeStamp(String time) {
        mTimeStamp = time;
    }

    public String getId() {
        Utils.info(this, "getId enter");
        return this.mId;
    }

    public String getTimeStamp() {
        Utils.info(this, "getTimeStamp enter");
        return this.mTimeStamp;
    }

    public String getNote() {
        Utils.info(this, "getNote enter");
        return this.mNote;
    }
}
