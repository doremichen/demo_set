package com.adam.app.demoset.database2;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

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
        Utils.inFo(this, "Constructor ");
        mRepository = new NoteRepository(application);
        mAllNotes = mRepository.loadAllNotes();
    }

    /**
     * Access database interface
     */
    public void insert(Note note) {
        Utils.inFo(this, "insert ");
        mRepository.insert(note);
    }

    public void update(Note... notes) {
        Utils.inFo(this, "update ");
        mRepository.update(notes);
    }

    public void delete(Note... notes) {
        Utils.inFo(this, "delete ");
        mRepository.delete(notes);
    }
}
