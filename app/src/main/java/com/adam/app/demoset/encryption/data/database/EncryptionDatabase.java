/*
 * Copyright (c) 2024 Adam Chen
 */

package com.adam.app.demoset.encryption.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.adam.app.demoset.encryption.data.dao.EncryptionDao;
import com.adam.app.demoset.encryption.data.model.EncryptionItem;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {EncryptionItem.class}, version = 1, exportSchema = false)
public abstract class EncryptionDatabase extends RoomDatabase {

    public abstract EncryptionDao encryptionDao();

    private static volatile EncryptionDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static EncryptionDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (EncryptionDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    EncryptionDatabase.class, "encryption_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
