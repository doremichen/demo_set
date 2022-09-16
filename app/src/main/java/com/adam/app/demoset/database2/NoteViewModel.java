package com.adam.app.demoset.database2;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.adam.app.demoset.Utils;
import com.adam.app.demoset.database2.room.Note;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    LiveData<List<Note>> mAllNotes;

    private NoteRepository mRepository;


    /**
     * Initialize Note repository
     *
     * @param application
     */
    public NoteViewModel(@NonNull Application application) {
        super(application);
        Utils.info(this, "Constructor ");
        mRepository = new NoteRepository(application);
        mAllNotes = mRepository.loadAllNotes();
    }

    /**
     * Access database interface
     */
    public void insert(Note note) {
        Utils.info(this, "insert ");
        mRepository.insert(note);
    }

    public void update(Note... notes) {
        Utils.info(this, "update ");
        mRepository.update(notes);
    }

    public void delete(Note... notes) {
        Utils.info(this, "delete ");
        mRepository.delete(notes);
    }
}
