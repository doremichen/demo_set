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
    public void insertNote(Note note);

    @Query("SELECT * from Adam_note_table")
    public LiveData<List<Note>> loadAllNotes();

    @Update
    public void updateNotes(Note... notes);

    @Delete
    public void deleteNotes(Note... notes);

}
