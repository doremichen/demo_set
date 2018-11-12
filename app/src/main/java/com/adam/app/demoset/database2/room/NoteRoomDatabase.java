package com.adam.app.demoset.database2.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.adam.app.demoset.Utils;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class NoteRoomDatabase extends RoomDatabase {

    public abstract NoteDao getNoteDao();

    private static volatile NoteRoomDatabase sInatance;

    private static RoomDatabase.Callback sCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            Utils.inFo(this, "Note room database is opened");
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
}
