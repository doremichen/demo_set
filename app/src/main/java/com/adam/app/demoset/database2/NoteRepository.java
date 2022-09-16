package com.adam.app.demoset.database2;

import android.app.Application;
import androidx.lifecycle.LiveData;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

import com.adam.app.demoset.Utils;
import com.adam.app.demoset.database2.room.Note;
import com.adam.app.demoset.database2.room.NoteDao;
import com.adam.app.demoset.database2.room.NoteRoomDatabase;

import java.util.List;

public class NoteRepository {

    private LiveData<List<Note>> mAllNotes;
    private NoteDao mNoteDao;

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

        private NoteDao mDao;

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

        private NoteDao mDao;

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

        private NoteDao mDao;

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
