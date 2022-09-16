package com.adam.app.demoset.database2.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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
