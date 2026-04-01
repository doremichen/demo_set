/*
 * Copyright (c) 2026 Adam Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.adam.app.demoset.database.room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.database.room.entity.Note;
import com.adam.app.demoset.database.room.room.NoteRepository;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    private final NoteRepository mRepository;
    LiveData<List<Note>> mAllNotes;


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
