package com.adam.app.demoset.database.room.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.adam.app.demoset.Utils;
import com.adam.app.demoset.database.room.entity.Note;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class NoteRoomDatabase extends RoomDatabase {

    private static volatile NoteRoomDatabase sInatance;
    private static final RoomDatabase.Callback sCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            Utils.info(this, "Note room database is opened");
        }
    };

    public static NoteRoomDatabase getDatabase(final Context ctx) {
        if (sInatance == null) {
            synchronized (NoteRoomDatabase.class) {
                sInatance = Room.
                        databaseBuilder(ctx.getApplicationContext(), NoteRoomDatabase.class, "AdamNoteDB").
                        addCallback(sCallback).
                        build();
            }
        }
        return sInatance;
    }

    public abstract NoteDao getNoteDao();
}
