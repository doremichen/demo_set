/**
 * Copyright (C) 2018 Adam Chen. All rights reserved.
 * <p>
 * Description: This class is the repository of note
 * </p>
 *
 * @author Adam Chen
 * @version 1.0 - 2018/11/12
 */
package com.adam.app.demoset.database.room.room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.adam.app.demoset.database.room.entity.Note;
import com.adam.app.demoset.utils.Utils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteRepository {

    // Executor service
    private static final ExecutorService mDbService = Executors.newSingleThreadExecutor();
    private final LiveData<List<Note>> mAllNotes;
    private final NoteDao mNoteDao;

    /**
     * Initial note dao
     *
     * @param app
     */
    public NoteRepository(Application app) {
        Utils.info(this, "Constructer enter");
        NoteRoomDatabase database = NoteRoomDatabase.getDatabase(app);
        mNoteDao = database.getNoteDao();
        mAllNotes = mNoteDao.loadAllNotes();
    }

    /**
     * Provide to the View modle
     *
     * @return
     */
    public LiveData<List<Note>> loadAllNotes() {
        return mAllNotes;
    }

    /**
     * Access database interface
     */
    public void insert(@NonNull Note note) {
        Utils.info(this, "insert ");
        mDbService.execute(() -> mNoteDao.insertNote(note));
    }

    public void update(Note... notes) {
        Utils.info(this, "update ");
        mDbService.execute(() -> mNoteDao.updateNotes(notes));
    }

    public void delete(Note... notes) {
        Utils.info(this, "delete ");
        mDbService.execute(() -> mNoteDao.deleteNotes(notes));
    }

}
