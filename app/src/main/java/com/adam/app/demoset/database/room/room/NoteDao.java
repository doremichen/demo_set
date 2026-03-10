/**
 * Copyright (C) 2018 Adam Chen. All rights reserved.
 * <p>
 * Description: This class is the dao of note
 * </p>
 *
 * @author Adam Chen
 * @version 1.0 - 2018/11/12
 */
package com.adam.app.demoset.database.room.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.adam.app.demoset.database.room.entity.Note;

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
