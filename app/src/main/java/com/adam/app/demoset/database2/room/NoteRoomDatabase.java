package com.adam.app.demoset.database2.room;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import androidx.annotation.NonNull;

import com.adam.app.demoset.Utils;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class NoteRoomDatabase extends RoomDatabase {

    public abstract NoteDao getNoteDao();

    private static volatile NoteRoomDatabase sInatance;

    private static RoomDatabase.Callback sCallback = new RoomDatabase.Callback() {
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
}
