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

package com.adam.app.demoset.database.contentprovider.repository;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.database.contentprovider.controller.DBController;
import com.adam.app.demoset.database.contentprovider.entity.Note;
import com.adam.app.demoset.database.contentprovider.provider.MyDBProvider;
import com.adam.app.demoset.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class NoteRepository {

    private final MutableLiveData<List<Note>> mNotes = new MutableLiveData<>();
    private final Context mContext;

    private final ContentObserver mObserver;

    public NoteRepository(Context context) {
        this.mContext = context.getApplicationContext();
        DBController.INSTANCE.setContentResolver(mContext);

        mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                Utils.info(NoteRepository.this, "ContentObserver onChange");
                refreshNotes();
            }
        };

        // Register ContentObserver to listen for changes in the provider
        mContext.getContentResolver().registerContentObserver(
                MyDBProvider.MYTABLE_URI,
                true,
                mObserver
        );
        refreshNotes();
    }

    public void unregisterObserver() {
        mContext.getContentResolver().unregisterContentObserver(mObserver);
    }

    public LiveData<List<Note>> getNotes() {
        return mNotes;
    }

    public void refreshNotes() {
        Utils.info(this, "refreshNotes enter");
        List<Note> notesList = new ArrayList<>();
        try (Cursor c = DBController.INSTANCE.queryNote("")) {
            if (c != null && c.moveToFirst()) {
                do {
                    notesList.add(new Note(c));
                } while (c.moveToNext());
            }
        }
        mNotes.postValue(notesList);
    }

    public void addNote(String content) {
        DBController.INSTANCE.addNote(content);
        // Note: refreshNotes() will be called by ContentObserver
    }

    public void updateNote(String id, String content) {
        DBController.INSTANCE.updateNote(id, content);
        // Note: refreshNotes() will be called by ContentObserver
    }

    public void deleteNote(String id) {
        DBController.INSTANCE.deleteNote(id);
        // Note: refreshNotes() will be called by ContentObserver
    }
}
