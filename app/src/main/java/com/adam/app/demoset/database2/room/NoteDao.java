package com.adam.app.demoset.database2.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insertNote(Note note);

    @Query("SELECT * from Adam_note_table")
    LiveData<List<Note>> loadAllNotes();

    @Update
    void updateNotes(Note... notes);

    @Delete
    void deleteNotes(Note... notes);

}
