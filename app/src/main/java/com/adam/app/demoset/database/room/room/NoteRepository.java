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
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.database.room.entity.Note;

import java.util.List;

public class NoteRepository {

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
        new InsertTask(mNoteDao).execute(note);
    }

    public void update(Note... notes) {
        Utils.info(this, "update ");
        new UpdateTask(mNoteDao).execute(notes);
    }

    public void delete(Note... notes) {
        Utils.info(this, "delete ");
        new DeleteTask(mNoteDao).execute(notes);
    }

    /**
     * DB operater task
     */
    private static class InsertTask extends AsyncTask<Note, Void, Void> {

        private final NoteDao mDao;

        InsertTask(NoteDao dao) {
            this.mDao = dao;
        }


        @Override
        protected Void doInBackground(Note... notes) {
            Utils.info(this, "doInBackground enter");
            this.mDao.insertNote(notes[0]);
            return null;
        }
    }

    private static class UpdateTask extends AsyncTask<Note, Void, Void> {

        private final NoteDao mDao;

        UpdateTask(NoteDao dao) {
            this.mDao = dao;
        }


        @Override
        protected Void doInBackground(Note... notes) {
            Utils.info(this, "doInBackground enter");
            this.mDao.updateNotes(notes);
            return null;
        }
    }

    private static class DeleteTask extends AsyncTask<Note, Void, Void> {

        private final NoteDao mDao;

        DeleteTask(NoteDao dao) {
            this.mDao = dao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            Utils.info(this, "doInBackground enter");
            mDao.deleteNotes(notes[0]);
            return null;
        }
    }

}
