/*
 * Copyright (c) 2024 Adam Chen
 */

package com.adam.app.demoset.encryption.data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.adam.app.demoset.encryption.data.dao.FullEncryptionDao;
import com.adam.app.demoset.encryption.data.model.FullEncryptionItem;
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {FullEncryptionItem.class}, version = 1, exportSchema = false)
public abstract class FullEncryptionDatabase extends RoomDatabase {

    public abstract FullEncryptionDao fullEncryptionDao();

    private static volatile FullEncryptionDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static FullEncryptionDatabase getDatabase(final Context context, byte[] passphrase) {
        if (INSTANCE == null) {
            synchronized (FullEncryptionDatabase.class) {
                if (INSTANCE == null) {
                    // SQLCipher requires loading native libraries
                    System.loadLibrary("sqlcipher");

                    SupportOpenHelperFactory factory = new SupportOpenHelperFactory(passphrase);
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    FullEncryptionDatabase.class, "full_encryption_database")
                            .openHelperFactory(factory)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
